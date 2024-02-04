package com.example.messenger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>{

    private List<Message> messages = new ArrayList<>();
    private static final int VIEW_TYPE_MY_MSG = 100;
    private static final int VIEW_TYPE_USER_MSG = 200;

    public MessagesAdapter(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    private String currentUserId;


    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoytResId;
        if (viewType == VIEW_TYPE_MY_MSG){
            layoytResId = R.layout.my_msg_item;
        } else {
            layoytResId = R.layout.user_msg_item;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layoytResId, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getSenderId().equals(currentUserId)){
            return VIEW_TYPE_MY_MSG;
        } else {
            return VIEW_TYPE_USER_MSG;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.tvMsg.setText(message.getText());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder{

        private TextView tvMsg;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tvMsg);
        }
    }
}
