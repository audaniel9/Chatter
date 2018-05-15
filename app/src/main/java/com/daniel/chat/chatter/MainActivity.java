package com.daniel.chat.chatter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseRecyclerAdapter adapter;
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
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_main,"You are now signed out",Snackbar.LENGTH_SHORT).show();
                    startSignIn();
                }
            });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Snackbar.make(activity_main,"You are now signed in.", Snackbar.LENGTH_SHORT).show();
                displayMessage();
            }
            else if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                Snackbar.make(activity_main,"No internet connection",Snackbar.LENGTH_SHORT).show();
            }
            else {
                Snackbar.make(activity_main, "There was an error signing in. Please try again.", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Chat");
        setContentView(R.layout.activity_main);

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

        //Check if a user is signed in and launch sign in page if none
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startSignIn();
        }
        else {
            displayMessage();
        }
    }

    private void startSignIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.PhoneBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setTheme(R.style.FirebaseLoginTheme)
                        .setLogo(R.drawable.logo_placeholder)
                        .build()
                ,SIGN_IN_REQUEST_CODE);
    }

    private void displayMessage() {
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
