package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;

public class ForgetPassword2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_2);

        // Back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Verify button
        Button verify = findViewById(R.id.btnVerify);
        verify.setOnClickListener(v -> {
            Intent intent = new Intent(ForgetPassword2Activity.this, ForgetPassword3Activity.class);
            startActivity(intent);
        });
    }
}
