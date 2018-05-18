package com.daniel.chat.chatter;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private TextView name;
    private ImageView navHeaderbg;
    RelativeLayout activity_main;
    FloatingActionButton fabSend;
    RecyclerView messageList;

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
            alertRefresh.setMessage("All messages will be deleted. Do you want to continue?" + "\n\n(This will crash the app, but it works)");
            alertRefresh.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,int which) {
                    FirebaseDatabase.getInstance().getReference().removeValue();
                    finish();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setTitle("Chat");
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null) {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }
            }
        };

        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        fabSend = (FloatingActionButton) findViewById(R.id.fabSend);

        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference().push().setValue(new Messages(input.getText().toString(),FirebaseAuth.getInstance().getCurrentUser().getEmail()));
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
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();

        if(id == R.id.profile) {
            fragment = new ProfileFragment();
        }
        else if (fragment != null) {
            FragmentManager fragmentManger = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManger.beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayMessage() {
        FirebaseRecyclerAdapter adapter;

        messageList = (RecyclerView) findViewById(R.id.messageList);
        Query query = FirebaseDatabase.getInstance().getReference();

        FirebaseRecyclerOptions<Messages> options =
                new FirebaseRecyclerOptions.Builder<Messages>()
                        .setQuery(query,Messages.class)
                        .setLifecycleOwner(this)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Messages,MessagesViewHolder>(options) {
            @Override
            public MessagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,parent,false);

                return new MessagesViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(MessagesViewHolder holder,int position,Messages model) {
                holder.message.setText(model.getMessage());
                holder.user.setText(model.getUser());
                holder.time.setText(DateFormat.format("MM/dd/yy hh:mm aa",model.getTime()));
            }
        };
        messageList.setAdapter(adapter);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        layoutmanager.setStackFromEnd(true);
        messageList.setLayoutManager(layoutmanager);
    }
}