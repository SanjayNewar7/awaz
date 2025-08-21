package com.example.awaz.controller;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.awaz.model.UserData;
import com.example.awaz.model.UserResponse;
import com.example.awaz.service.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserController {
    private static final String TAG = "UserController";
    private final Context context;
    private final ProgressBar progressBar;

    public UserController(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;
    }

    public interface UserDataCallback {
        void onSuccess(UserData userData);
        void onFailure(String errorMessage);
    }

    public interface UserUpdateCallback {
        void onSuccess(UserData updatedUser);
        void onFailure(String errorMessage);
    }

    private void showLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void getCurrentUser(String accessToken, UserDataCallback callback) {
        showLoading();

        if (accessToken == null || accessToken.isEmpty()) {
            hideLoading();
            callback.onFailure("User not authenticated");
            return;
        }

        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(context);
        Call<UserResponse> call = apiService.getCurrentUser();

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    if ("success".equals(userResponse.getStatus())) {
                        callback.onSuccess(userResponse.getUser());
                    } else {
                        String errorMsg = userResponse.getMessage() != null ?
                                userResponse.getMessage() : "Failed to fetch user data";
                        Log.e(TAG, "API Error: " + errorMsg);
                        callback.onFailure(errorMsg);
                    }
                } else {
                    String errorMsg = "Failed to fetch user data. Status: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    Log.e(TAG, errorMsg);
                    callback.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                hideLoading();
                String errorMsg = "Network error: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onFailure(errorMsg);
            }
        });
    }

    public void updateUser(String accessToken,
                           String district, String city, int ward,
                           String areaName, String phoneNumber,
                           String email, String bio,
                           UserUpdateCallback callback) {
        showLoading();

        JsonObject updateData = new JsonObject();
        if (district != null) updateData.addProperty("district", district);
        if (city != null) updateData.addProperty("city", city);
        if (ward > 0) updateData.addProperty("ward", ward);
        if (areaName != null) updateData.addProperty("area_name", areaName);
        if (phoneNumber != null) updateData.addProperty("phone_number", phoneNumber);
        if (email != null) updateData.addProperty("email", email);
        if (bio != null) updateData.addProperty("bio", bio);

        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(context);
        Call<UserResponse> call = apiService.updateUser(updateData);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    if ("success".equals(userResponse.getStatus())) {
                        callback.onSuccess(userResponse.getUser());
                    } else {
                        String errorMsg = userResponse.getMessage() != null ?
                                userResponse.getMessage() : "Failed to update user data";
                        Log.e(TAG, errorMsg);
                        callback.onFailure(errorMsg);
                    }
                } else {
                    String errorMsg = "Failed to update user data. Status: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    Log.e(TAG, errorMsg);
                    callback.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                hideLoading();
                String errorMsg = "Network error: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onFailure(errorMsg);
            }
        });
    }

    public void getUserById(int userId, String accessToken, UserDataCallback callback) {
        showLoading();

        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(context);
        Call<UserResponse> call = apiService.getUser(userId); // Updated to UserResponse

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    Log.d(TAG, "API Response: " + new Gson().toJson(userResponse)); // Log full response
                    if ("success".equals(userResponse.getStatus())) {
                        UserData userData = userResponse.getUser();
                        if (userData != null) {
                            callback.onSuccess(userData);
                        } else {
                            callback.onFailure("User data is null in response");
                        }
                    } else {
                        String errorMsg = userResponse.getMessage() != null ? userResponse.getMessage() : "Failed to fetch user data";
                        callback.onFailure(errorMsg);
                    }
                } else {
                    String errorMsg = "Failed to fetch user with ID: " + userId + ". Status: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += ", Error Body: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    Log.e(TAG, errorMsg);
                    callback.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                hideLoading();
                String errorMsg = "Network error: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onFailure(errorMsg);
            }
        });
    }
}