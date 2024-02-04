package com.example.messenger;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterViewModel extends AndroidViewModel {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersReference;
    private String token;
    private final String url = "https://chat-gkt-default-rtdb.europe-west1.firebasedatabase.app/";
    private final MutableLiveData<FirebaseUser> userFB = new MutableLiveData();
    private final MutableLiveData<String> textError = new MutableLiveData();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(url);
        usersReference = database.getReference("users");
        getToken();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    userFB.setValue(firebaseAuth.getCurrentUser());
                }
            }
        });
    }

    public LiveData<String> getTextError() {
        return textError;
    }

    public MutableLiveData<FirebaseUser> getUser() {
        return userFB;
    }

    public void signUp(
            String login,
            String password,
            String name,
            String surname,
            int age
    ) {

        mAuth.createUserWithEmailAndPassword(login, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = authResult.getUser();
                        if (firebaseUser == null) {
                            return;
                        }

                        NewUser user = new NewUser(
                                firebaseUser.getUid(),
                                name,
                                surname,
                                age,
                                false,
                                token
                        );
                        usersReference.child(user.getId()).setValue(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        textError.setValue(e.getMessage());
                    }
                });

    }

    private void getToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                        } else {
                            token = task.getResult();
                            Log.d("TAG", token);
                        }
                    }
                });


    }

}
