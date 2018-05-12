package com.daniel.chat.chatter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<MessageActivity> adapter;
    RelativeLayout activity_main;
    FloatingActionButton fabSend;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menuSignOut) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_main,"You are now signed out",Snackbar.LENGTH_SHORT).show();
                    startSignIn();
                }
            });
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
                displayMessageActivity();
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        fabSend = (FloatingActionButton) findViewById(R.id.fabSend);

        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference().push().setValue(new MessageActivity(input.getText().toString(),FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
            }
        });

        //Check if a user is signed in and launch sign in page if none
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startSignIn();
        }
        else {
            Snackbar.make(activity_main,"Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();
            //Load content
            displayMessageActivity();
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
                        .setLogo(R.mipmap.ic_launcher)
                        .build()
                ,SIGN_IN_REQUEST_CODE);
    }

    private void displayMessageActivity() {
        ListView messageList = (ListView) findViewById(R.id.messageList);
        Query query = FirebaseDatabase.getInstance().getReference().child("chats");
        FirebaseListOptions<MessageActivity> options = new FirebaseListOptions.Builder<MessageActivity>().setQuery(query,MessageActivity.class).setLayout(R.layout.message_item).build();
        adapter = new FirebaseListAdapter<MessageActivity>(options) {
            @Override
            protected void populateView(View v, MessageActivity model, int position) {
                //Get references to views
                TextView message,user,time;
                message = (TextView) v.findViewById(R.id.messageText);
                user = (TextView) v.findViewById(R.id.user);
                time = (TextView) v.findViewById(R.id.messageTime);

                message.setText(model.getMessage());
                user.setText(model.getUser());
                time.setText(DateFormat.format("mm/dd/yy AT hh:mm",model.getTime()));
            }
        };

        messageList.setAdapter(adapter);
    }
}
