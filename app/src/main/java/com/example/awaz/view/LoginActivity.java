package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;
import com.example.awaz.controller.AuthController;

public class LoginActivity extends AppCompatActivity {
    private AuthController controller;
    private EditText editEmail;
    private EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize controller
        controller = new AuthController(this);

        // Initialize views
        editEmail = findViewById(R.id.editPhone); // Consider renaming to editEmail in XML
        editPassword = findViewById(R.id.editPassword);

        // Forgot Password click listener
        TextView forgotPassword = findViewById(R.id.textForgotPassword);
        forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPassword1Activity.class);
            startActivity(intent);
        });

        // Signup click listener
        TextView signupText = findViewById(R.id.textRegister);
        signupText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Login button click listener
        Button loginBtn = findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(view -> {
            if (controller.validateLoginFields(editEmail, editPassword)) {
                String email = getEmail();
                String password = getPassword();
                controller.login(email, password);
            }
        });
    }

    public String getEmail() {
        return editEmail.getText().toString().trim();
    }

    public String getPassword() {
        return editPassword.getText().toString().trim();
    }

    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}