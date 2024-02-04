package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.messenger.databinding.ActivityChatsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    private ChatsViewModel viewModel;
    private ChatsAdapter adapter;
    private static final String EXTRA_CURRENT_USER_ID = "current_id";
    private String currentUserId;


    private ActivityChatsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ChatsViewModel.class);
        observeViewModel();
        adapter = new ChatsAdapter();
        binding.rvChats.setAdapter(adapter);

        currentUserId = getIntent().getStringExtra(EXTRA_CURRENT_USER_ID);
        adapter.setOnUserClickListener(new ChatsAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(NewUser user) {
                Intent intent = ChatActivity.newIntent(ChatsActivity.this,currentUserId,user.getId());
                startActivity(intent);
            }
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                        } else {
                           String token = task.getResult();
                            Log.d("TAG", token);
                        }
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

    private void observeViewModel(){
        viewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser==null){
                    Intent intent = LoginActivity.newIntent(ChatsActivity.this);
                    startActivity(intent);
                    finish();
                }
            }
        });

        viewModel.getUsers().observe(this, new Observer<List<NewUser>>() {
            @Override
            public void onChanged(List<NewUser> users) {
                adapter.setUsers(users);
            }
        });
    }

    public static Intent newIntent(Context context, String currentUserId){
        Intent intent = new Intent(context, ChatsActivity.class);
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.chats_menu,menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemSignOut){
            viewModel.signOut();
        }
        return super.onOptionsItemSelected(item);
    }
}