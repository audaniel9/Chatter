package com.daniel.chat.chatter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private int messageCount = 100;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Toolbar toolbar;
    RelativeLayout activity_main;
    FloatingActionButton fabSend;
    RecyclerView messageList;
    Fragment fragment;
    SharedPreferences messageCountSettings;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Chat");
        setSupportActionBar(toolbar);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(user == null) {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }
            }
        };
        auth.addAuthStateListener(authListener);

        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        fabSend = (FloatingActionButton) findViewById(R.id.fabSend);

        // Save persistent data
        messageCountSettings = getSharedPreferences("count", Context.MODE_PRIVATE);
        messageCount = messageCountSettings.getInt("counts", messageCount);
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageCount += 1;
                final SharedPreferences.Editor edit = messageCountSettings.edit();
                edit.putInt("counts",messageCount).apply();
                EditText input = (EditText) findViewById(R.id.input);

                FirebaseDatabase.getInstance().getReference("Messages").child(String.valueOf(messageCount)).setValue(new Messages(input.getText().toString(),user.getDisplayName()));
                input.setText("");
            }
        });

        displayMessage();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_bar_view);

        View navHeader = navigationView.getHeaderView(0);
        TextView drawerHeaderName = (TextView) navHeader.findViewById(R.id.drawer_header_name);
        drawerHeaderName.setText(user.getDisplayName());

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        // Pop the stack so no new duplicate fragments are added to the stack
        if(fragment != null) {
            getSupportFragmentManager().popBackStack();
            toolbar.setTitle("Chat");
        }
    }

    // Selected menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menuAbout) {
            View aboutView = getLayoutInflater().inflate(R.layout.menu_about,null,false);
            AlertDialog.Builder alertAbout = new AlertDialog.Builder(this);
            alertAbout.setTitle("About");
            alertAbout.setView(aboutView);
            alertAbout.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertAbout.show();
        }

        if(item.getItemId() == R.id.menuSignOut) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }

        if(item.getItemId() == R.id.menuRefresh) {
            AlertDialog.Builder alertRefresh = new AlertDialog.Builder(this);
            alertRefresh.setTitle("Refresh Messages");
            alertRefresh.setMessage("All messages will be deleted. Do you want to continue?");
            alertRefresh.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,int which) {
                    FirebaseDatabase.getInstance().getReference("Messages").removeValue();
                    messageCount = 100;
                }
            });
            alertRefresh.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertRefresh.show();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        int id = item.getItemId();

        switch(id) {
            case R.id.drawer_home:
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragment = null;
                break;
            case R.id.drawer_profile:
                fragment = new ProfileFragment();
                fragmentTransaction.replace(R.id.frame, fragment, "tag_profile");
                break;
            case R.id.drawer_people:
                fragment = new PeopleFragment();
                fragmentTransaction.replace(R.id.frame, fragment, "tag_people");
                break;
            default:
                break;
        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(fragment != null) {
                    for (Fragment fragments : getSupportFragmentManager().getFragments()) {
                        if (fragments.getTag().equals("tag_profile")) {
                            toolbar.setTitle("Profile");
                        }
                        if (fragments.getTag().equals("tag_people")) {
                            toolbar.setTitle("People");
                        }
                    }
                }
                else {
                    toolbar.setTitle("Chat");
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayMessage() {
        final FirebaseRecyclerAdapter adapter;

        messageList = (RecyclerView) findViewById(R.id.messageList);
        Query query = FirebaseDatabase.getInstance().getReference().child("Messages");

        FirebaseRecyclerOptions<Messages> options =
                new FirebaseRecyclerOptions.Builder<Messages>().setQuery(query,Messages.class).setLifecycleOwner(this).build();

        adapter = new FirebaseRecyclerAdapter<Messages,MessagesViewHolder>(options) {
            @Override
            public MessagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                //if(user.toString() == FirebaseDatabase.getInstance().getReference("Messages").child("user").toString()) {
                //}

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,parent,false);

                return new MessagesViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(MessagesViewHolder holder,int position,Messages model) {
                holder.message.setText(model.getMessage());
                holder.username.setText(model.getUser());
                holder.time.setText(DateFormat.format("MM/dd/yy hh:mm aa",model.getTime()));
            }
        };
        messageList.setAdapter(adapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        messageList.setLayoutManager(layoutManager);

        // Auto scroll down when chat list is updated
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                    messageList.scrollToPosition(positionStart);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(authListener);
    }
}