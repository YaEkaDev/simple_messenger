package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.messenger.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        observeViewModel();


    }

    private void observeViewModel(){

        viewModel.getTextError().observe(RegisterActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String textError) {

                Toast.makeText(RegisterActivity.this,textError, Toast.LENGTH_SHORT).show();

            }
        });

        viewModel.getUser().observe(RegisterActivity.this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser!=null) {
                    Intent intent = ChatsActivity.newIntent(RegisterActivity.this, firebaseUser.getUid());
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
    private void signUp(){

        String login = "";
        String password = "";
        String name = "";
        String surname = "";
        int age;
        if(binding.edtLogin.getText().toString().isEmpty()){
            Toast.makeText(RegisterActivity.this,"Введите email!", Toast.LENGTH_SHORT).show();
        } else if (binding.edtPassword.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this,"Введите пароль!", Toast.LENGTH_SHORT).show();
        } else if (binding.edtName.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this,"Введите имя!", Toast.LENGTH_SHORT).show();
        } else if (binding.edtSurname.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this,"Введите фамилию!", Toast.LENGTH_SHORT).show();
        } else if (binding.edtAge.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this,"Введите возраст!", Toast.LENGTH_SHORT).show();
        } else {
            login = binding.edtLogin.getText().toString().trim();
            name = binding.edtName.getText().toString().trim();
            surname = binding.edtSurname.getText().toString().trim();
            age = Integer.parseInt(binding.edtAge.getText().toString().trim());
            password = binding.edtPassword.getText().toString();
            viewModel.signUp(login,password,name,surname,age);


        }

    }

    public static Intent newIntent(Context context){
        return new Intent(context, RegisterActivity.class);
    }
}