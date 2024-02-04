package com.example.messenger;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsViewModel extends AndroidViewModel {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersReference;
    private final String url = "https://chat-gkt-default-rtdb.europe-west1.firebasedatabase.app/";
    private final MutableLiveData<FirebaseUser> userFB = new MutableLiveData();
    private MutableLiveData<List<NewUser>> usersFB = new MutableLiveData();

    public LiveData<List<NewUser>> getUsers() {
        return usersFB;
    }

    public MutableLiveData<FirebaseUser> getUser() {
        return userFB;
    }

    public ChatsViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(url);
        usersReference = database.getReference("users");

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser==null){
                    return;
                }
                List<NewUser> users = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    NewUser user = dataSnapshot.getValue(NewUser.class);
                    if (user==null){
                        return;
                    }
                    if (!user.getId().equals(currentUser.getUid())) {
                        users.add(user);
                    }
                }
                usersFB.setValue(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 userFB.setValue(firebaseAuth.getCurrentUser());

            }
        });
    }

    public void setUserOnline(boolean isOnline){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser==null){
            Log.d("MyLog", "bhbhb");
            return;
        }
        usersReference.child(mAuth.getCurrentUser().getUid()).child("online").setValue(isOnline);
    }

    public void signOut(){
        setUserOnline(false);
        mAuth.signOut();
    }
}
