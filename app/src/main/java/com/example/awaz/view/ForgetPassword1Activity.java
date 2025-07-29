package com.example.awaz.view;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;

public class ForgetPassword1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_1);

        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Send Code button
        Button SendCode = findViewById(R.id.btnSendCode);
        SendCode.setOnClickListener(v -> {
                Intent intent = new Intent(ForgetPassword1Activity.this, ForgetPassword2Activity.class);
                startActivity(intent);
        });

    }
}
