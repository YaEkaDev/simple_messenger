package com.example.messenger;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends AndroidViewModel {

    private FirebaseAuth mAuth;
    private final MutableLiveData<FirebaseUser> userFB = new MutableLiveData();
    private final MutableLiveData<String> textError = new MutableLiveData();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()!=null){
                    userFB.setValue(firebaseAuth.getCurrentUser());
                }
            }
        });
    }

    public MutableLiveData<FirebaseUser> getUser() {
        return userFB;
    }

    public MutableLiveData<String> getTextError() {
        return textError;
    }

    public void signIn(User user){

        mAuth.signInWithEmailAndPassword(user.getLogin(), user.getPassword())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        textError.setValue(e.getMessage());
                    }
                });

    }

    public void test(){
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    Log.d("MyLog", "Not auth");
                } else {
                    Log.d("MyLog", "auth..."+currentUser.getEmail());
                }
            }
        });

    }
}
