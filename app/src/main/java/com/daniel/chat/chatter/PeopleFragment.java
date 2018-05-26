package com.daniel.chat.chatter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class PeopleFragment extends Fragment {
    RecyclerView peopleList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_people, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final FirebaseRecyclerAdapter adapter;

        peopleList = (RecyclerView) getView().findViewById(R.id.peopleList);
        Query query = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>().setQuery(query,Users.class).setLifecycleOwner(this).build();

        adapter = new FirebaseRecyclerAdapter<Users,PeopleViewHolder>(options) {
            @Override
            public PeopleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_item,parent,false);

                return new PeopleViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PeopleViewHolder holder, int position, @NonNull Users model) {
                    holder.peopleName.setText(model.getUser());
            }
        };
        peopleList.setAdapter(adapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        peopleList.setLayoutManager(layoutManager);

        // Auto scroll down when chat list is updated
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                peopleList.scrollToPosition(positionStart);
            }
        });
    }
}
