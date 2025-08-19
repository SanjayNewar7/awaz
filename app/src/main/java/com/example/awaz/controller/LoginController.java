package com.example.awaz.controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;
import com.example.awaz.service.RetrofitClient;
import com.example.awaz.model.LoginRequest;
import com.example.awaz.model.LoginResponse;
import com.example.awaz.view.HomeMainActivity;
import com.example.awaz.view.LoginActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginController {
    private static final String TAG = "LoginController";
    private Context context;
    private RetrofitClient.ApiService apiService;

    public LoginController(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService(context);
    }

    public boolean validateLoginFields(EditText editEmailOrUsername, EditText editPassword) {
        boolean isValid = true;
        TextInputLayout emailLayout = ((LoginActivity) context).findViewById(R.id.textInputEmailusername);
        TextInputLayout passwordLayout = ((LoginActivity) context).findViewById(R.id.textInputPassword);

        String emailOrUsername = editEmailOrUsername.getText().toString().trim();
        if (emailOrUsername.isEmpty()) {
            emailLayout.setError("Email or username is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches() && !emailOrUsername.matches("\\w+")) {
            emailLayout.setError("Enter a valid email or username");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        String password = editPassword.getText().toString().trim();
        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            isValid = false;
        } else if (password.length() < 8) {
            passwordLayout.setError("Password must be at least 8 characters");
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        return isValid;
    }

    public void login(String emailOrUsername, String password) {
        Log.d(TAG, "Initiating login with email/username: " + emailOrUsername);
        LoginRequest loginRequest = new LoginRequest(emailOrUsername, password);
        Call<LoginResponse> call = apiService.userLogin(loginRequest);


        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, "Login response code: " + response.code());
                if (response.body() != null) {
                    Log.d(TAG, "Response body: " + new Gson().toJson(response.body()));
                } else {
                    Log.d(TAG, "Response body is null");
                }
                try {
                    String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                    Log.d(TAG, "Error body: " + errorBody);
                } catch (IOException e) {
                    Log.e(TAG, "Error reading error body: " + e.getMessage());
                }

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Login response: " + loginResponse.toString());
                    Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT).show();

                    // Store the access token
                    // When storing the token from login response:
                    RetrofitClient.setAccessToken(response.body().getAccessToken(), context);
                    Log.d(TAG, "Login successful, saved token: ****" + loginResponse.getAccessToken().substring(loginResponse.getAccessToken().length() - 4));



                    // Redirect to HomeMainActivity
                    try {
                        Intent intent = new Intent(context, HomeMainActivity.class);
                        intent.putExtra("access_token", loginResponse.getAccessToken());
                        context.startActivity(intent);
                        if (context instanceof AppCompatActivity) {
                            ((AppCompatActivity) context).finish();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting HomeMainActivity: " + e.getMessage(), e);
                        Toast.makeText(context, "Error navigating to home: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    String errorMessage = "Login failed";
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.d(TAG, "Login error body (raw): " + errorBody);
                        if (errorBody.contains("message")) {
                            Gson gson = new Gson();
                            Map<String, Object> errorJson = gson.fromJson(errorBody, Map.class);
                            String message = (String) errorJson.get("message");
                            if (message != null) {
                                errorMessage = "Login failed: " + message;
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage(), e);
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Login error: " + t.getMessage(), t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}