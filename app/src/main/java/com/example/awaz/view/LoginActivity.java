package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;
import com.example.awaz.controller.AuthController;
import com.example.awaz.controller.LoginController;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private AuthController authController;
    private LoginController loginController;
    private TextInputEditText editEmail;
    private TextInputEditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize controllers
        authController = new AuthController(this);
        loginController = new LoginController(this);

        // Initialize views
        editEmail = findViewById(R.id.editmailorusername);
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
            if (loginController.validateLoginFields(editEmail, editPassword)) {
                String emailOrUsername = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                loginController.login(emailOrUsername, password);
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