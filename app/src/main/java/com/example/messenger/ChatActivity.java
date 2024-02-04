package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.messenger.databinding.ActivityChatBinding;
import com.example.messenger.databinding.ActivityChatsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String EXTRA_CURRENT_USER_ID = "current_id";
    private static final String EXTRA_OTHER_USER_ID = "other_id";
    private ActivityChatBinding binding;
    private MessagesAdapter adapter;
    private String currentUserId;
    private String otherUserId;
    private ChatViewModel viewModel;
    private  ChatViewModelFactory viewModelFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().hasExtra("otherId")){
            currentUserId = getIntent().getStringExtra("currentId");
            otherUserId = getIntent().getStringExtra("otherId");
        }else {
            currentUserId = getIntent().getStringExtra(EXTRA_CURRENT_USER_ID);
            otherUserId = getIntent().getStringExtra(EXTRA_OTHER_USER_ID);
        }
        viewModelFactory = new ChatViewModelFactory(currentUserId, otherUserId);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(ChatViewModel.class);

        adapter = new MessagesAdapter(currentUserId);
        binding.rvChat.setAdapter(adapter);
        observeViewModel();

        binding.imvSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(
                        binding.edtMsg.getText().toString().trim(),
                        currentUserId,
                        otherUserId,
                        viewModel.getIdMsg()
                );
                viewModel.sendMessage(message);
                viewModel.getToken(message, ChatActivity.this);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.setUserOnline(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.setUserOnline(false);
    }

    private  void observeViewModel(){
        viewModel.getMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                adapter.setMessages(messages);
            }
        });

        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error!=null) {
                    Toast.makeText(ChatActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getMsgSent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean sent) {
                if (sent){
                    binding.edtMsg.setText("");
                }

            }
        });

        viewModel.getOtherUser().observe(this, new Observer<NewUser>() {
            @Override
            public void onChanged(NewUser user) {
                String userInfo = String.format("%s %s", user.getName(), user.getSurname());
                binding.tvTitle.setText(userInfo);
                int bgResId;

                if(user.isOnline()){
                    bgResId = R.drawable.circle_green;
                } else {
                    bgResId = R.drawable.circle_red;
                }
                Drawable backround = ContextCompat.getDrawable(ChatActivity.this,bgResId);
                binding.OnlineStatus.setBackground(backround);
            }
        });

    }

    public static Intent newIntent(Context context, String currentUserId, String otherUserId){

        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId);
        intent.putExtra(EXTRA_OTHER_USER_ID, otherUserId);

        return intent;

    }

}