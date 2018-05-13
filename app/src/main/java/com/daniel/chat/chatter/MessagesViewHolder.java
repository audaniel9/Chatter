package com.daniel.chat.chatter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class MessagesViewHolder extends RecyclerView.ViewHolder {
    TextView message,user,time;

    public MessagesViewHolder(View view) {
        super(view);
        message = (TextView) view.findViewById(R.id.message_text);
        user = (TextView) view.findViewById(R.id.message_user);
        time = (TextView) view.findViewById(R.id.message_time);
    }
}
