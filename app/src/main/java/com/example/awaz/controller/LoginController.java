package com.example.awaz.controller;

import android.util.Log;
import com.example.awaz.model.LoginModel;
import com.example.awaz.view.LoginActivity;

public class LoginController {
    private LoginActivity view;
    private LoginModel model;

    public LoginController(LoginActivity view) {
        this.view = view;
        this.model = new LoginModel(); // Initialize the model
    }

    public boolean validateFields() {
        String email = view.getEmail();
        String password = view.getPassword();

        if (model.validateCredentials(email, password)) {
            Log.d("LoginController", "Validation successful");
            return true;
        } else {
            Log.d("LoginController", "Validation failed");
            view.showError("Invalid email or password");
            return false;
        }
    }
}