package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.messenger.databinding.LoginMainBinding;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {


    private LoginMainBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupClickListeners();
        observeViewModel();

    }

    private void setupClickListeners(){

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        binding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = RegisterActivity.newIntent(LoginActivity.this);
                startActivity(intent);
            }
        });

        binding.tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ChangePasswordActivity.newIntent(
                        LoginActivity.this,
                        binding.edtLogin.getText().toString().trim());
                startActivity(intent);
            }
        });


    }

    private void observeViewModel(){
        viewModel.getTextError().observe(LoginActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String textError) {
                if(textError!=null) {
                    Toast.makeText(LoginActivity.this, textError, Toast.LENGTH_SHORT).show();
                }
            }
        });


        viewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser!=null){
                    Toast.makeText(LoginActivity.this, "Вы успешно авторизованы", Toast.LENGTH_SHORT).show();
                    Intent intent = ChatsActivity.newIntent(LoginActivity.this,firebaseUser.getUid());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void signIn(){

        String login = "";
        String password = "";
        if(binding.edtLogin.getText().toString().isEmpty()){
            Toast.makeText(LoginActivity.this,"Введите логин!", Toast.LENGTH_SHORT).show();
        } else if (binding.edtPassword.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this,"Введите пароль!", Toast.LENGTH_SHORT).show();
        } else {
            login = binding.edtLogin.getText().toString().trim();
            password = binding.edtPassword.getText().toString();
            User user = new User(login, password);
            viewModel.signIn(user);


        }

    }

    public static Intent newIntent(Context context){
        return new Intent(context, LoginActivity.class);
    }


}