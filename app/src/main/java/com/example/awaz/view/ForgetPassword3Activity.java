package com.example.awaz.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;

public class ForgetPassword3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_3);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());


        Button resetPassword = findViewById(R.id.btnResetPassword);
        resetPassword.setOnClickListener(v -> {
            // TODO: Add real password reset logic here
            Toast.makeText(this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
