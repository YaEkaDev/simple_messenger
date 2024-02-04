package com.example.messenger;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {

    private List<NewUser> users = new ArrayList<>();
    private OnUserClickListener onUserClickListener;

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    public void setUsers(List<NewUser> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
       return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        NewUser user = users.get(position);
        String userInfo = String.format("%s %s, %s", user.getName(),user.getSurname(),user.getAge());
        holder.tvUserInfo.setText(userInfo);
        int bgResId;
        if(user.isOnline()){
           bgResId = R.drawable.circle_green;
        } else {
            bgResId = R.drawable.circle_red;
        }
        Drawable backround = ContextCompat.getDrawable(holder.itemView.getContext(),bgResId);
        holder.onlineStatus.setBackground(backround);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onUserClickListener!=null){
                    onUserClickListener.onUserClick(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    interface OnUserClickListener{
        void onUserClick(NewUser user);
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{

        private TextView tvUserInfo;
        private View onlineStatus;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserInfo = itemView.findViewById(R.id.tvUserInfo);
            onlineStatus = itemView.findViewById(R.id.onlineStatus);

        }

    }

}
