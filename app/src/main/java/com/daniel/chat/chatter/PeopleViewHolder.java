package com.daniel.chat.chatter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class PeopleViewHolder extends RecyclerView.ViewHolder {
    TextView peopleName;

    public PeopleViewHolder(View view) {
        super(view);

        peopleName = (TextView) view.findViewById(R.id.people_name);
    }
}
