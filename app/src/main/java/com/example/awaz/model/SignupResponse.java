package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class SignupResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

    @SerializedName("errors")
    private Map<String, String[]> errors;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<String, String[]> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String[]> errors) {
        this.errors = errors;
    }

    public static class User {
        @SerializedName("user_id")
        private int userId;

        @SerializedName("username")
        private String username;

        // Add other fields as needed
        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }


    }
}