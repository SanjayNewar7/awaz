package com.example.awaz.service;

import com.example.awaz.model.LoginRequest;
import com.example.awaz.model.LoginResponse;
import com.example.awaz.model.SignupRequest;
import com.example.awaz.model.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/users")
    Call<SignupResponse> signup(@Body SignupRequest signupRequest);

    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}