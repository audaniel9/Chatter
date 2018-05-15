package com.daniel.chat.chatter;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ConvoListActivity extends AppCompatActivity {
    /*RecyclerView convoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Conversations");
        setContentView(R.layout.activity_convo_list);
        displayConvo();
    }

    private void displayConvo() {
        convoList = (RecyclerView) findViewById(R.id.convoList);
        String uid = FirebaseAuth.getInstance().getCurrentUser().get;
        Query query = FirebaseDatabase.getInstance().getReference(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

*/
}
