package com.example.awaz.model;

public class LoginModel {
    public boolean validateCredentials(String email, String password) {
        // Basic validation: check if email and password are non-empty
        // In a real app, this could involve checking against a database or API
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.trim().isEmpty() || password.length() < 6) {
            return false;
        }
        // Add more validation as needed (e.g., email format check)
        return true;
    }
}