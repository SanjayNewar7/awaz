package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("user")
    private UserData user;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private String status;

    // Getters and setters
    public UserData getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}