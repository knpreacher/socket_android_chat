package com.example.knp.socketchat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by knp on 2/8/17.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Message> messages;
    String login;

    public ChatRecyclerAdapter(Context context,ArrayList<Message> messages){
        this.messages = messages;
        this.context = context;
    }
    public class MessageHolder extends RecyclerView.ViewHolder{
        TextView tvLogin,tvText,tvTime;
        public MessageHolder(View v){
            super(v);
            tvLogin = (TextView)itemView.findViewById(R.id.tvLogin);
            tvText = (TextView)itemView.findViewById(R.id.tvText);
            tvTime = (TextView)itemView.findViewById(R.id.tvTime);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType){
            case 0:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_my,parent,false);
                break;
            case 1:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_other,parent,false);
                break;
            case 2:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_notification,parent,false);
                break;
            case 3:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_notification,parent,false);
                break;
        }
        return new MessageHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Message message = messages.get(position);
        switch (message.getType()){
            case 0:
                ((MessageHolder)holder).tvText.setText(message.getText());
                ((MessageHolder)holder).tvTime.setText(message.getTime());
                break;
            case 1:
                ((MessageHolder)holder).tvLogin.setText(message.getSender());
                ((MessageHolder)holder).tvText.setText(message.getText());
                ((MessageHolder)holder).tvTime.setText(message.getTime());
                break;
            case 2:
                ((MessageHolder)holder).tvText.setText(message.getSender()+" connected!");
                break;
            case 3:
                ((MessageHolder)holder).tvText.setText(message.getSender()+" disconnected!");
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
