package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.messenger.databinding.ActivityChangePasswordBinding;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;
    private ChangePasswordViewModel viewModel;
    private static final String EXTRA_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String email = getIntent().getStringExtra(EXTRA_EMAIL);
        binding.edtLogin.setText(email);
        viewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);

        observeViewModel();


        binding.btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });


    }

    private void observeViewModel(){
        viewModel.getIsSuccess().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccess) {
                if (isSuccess){
                    Toast.makeText(ChangePasswordActivity.this,"Вам на почту направлено письмо с ссылкой на смену пароля", Toast.LENGTH_SHORT).show();
                }
            }
        });


        viewModel.getTextError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String textError) {
                if (textError!=null) {
                    Toast.makeText(ChangePasswordActivity.this, textError, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetPassword() {
        String login = "";

        if (binding.edtLogin.getText().toString().isEmpty()) {
            Toast.makeText(ChangePasswordActivity.this, "Введите email!", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.changePassword(binding.edtLogin.getText().toString().trim());
        }
    }

    public static Intent newIntent(Context context,String email){
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        intent.putExtra(EXTRA_EMAIL,email);
        return intent;

    }
}