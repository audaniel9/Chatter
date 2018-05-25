package com.daniel.chat.chatter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class MessagesViewHolder extends RecyclerView.ViewHolder {
    TextView message,username,time;

    public MessagesViewHolder(View view) {
        super(view);

        username = (TextView) view.findViewById(R.id.message_username);
        message = (TextView) view.findViewById(R.id.message_text);
        time = (TextView) view.findViewById(R.id.message_time);
    }
}
