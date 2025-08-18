package com.example.awaz.controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.model.IssueRequest;
import com.example.awaz.model.IssueResponse;
import com.example.awaz.service.RetrofitClient;
import com.example.awaz.view.HomeMainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueController {
    private static final String TAG = "IssueController";
    private final Context context;
    private final RetrofitClient.ApiService apiService;
    private final AuthController authController;

    public IssueController(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService(context);
        this.authController = new AuthController(context);
    }

    public boolean validateIssueFields(EditText editIssueHeading, EditText editIssueDescription,
                                       EditText editAreaName, EditText editLocation,
                                       Spinner spinnerReportType, Spinner spinnerDistrict,
                                       Spinner spinnerWard) {
        boolean isValid = true;

        if (editIssueHeading.getText().toString().trim().isEmpty()) {
            editIssueHeading.setError("Issue heading is required");
            isValid = false;
        } else {
            editIssueHeading.setError(null);
        }

        if (editIssueDescription.getText().toString().trim().isEmpty()) {
            editIssueDescription.setError("Description is required");
            isValid = false;
        } else {
            editIssueDescription.setError(null);
        }

        if (editAreaName.getText().toString().trim().isEmpty()) {
            editAreaName.setError("Area name is required");
            isValid = false;
        } else {
            editAreaName.setError(null);
        }

        if (editLocation.getText().toString().trim().isEmpty()) {
            editLocation.setError("Location is required");
            isValid = false;
        } else {
            editLocation.setError(null);
        }

        if (spinnerReportType.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please select a report type", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (spinnerDistrict.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please select a district", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (spinnerWard.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please select a ward", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    public void submitIssue(IssueRequest issueRequest) {
        Log.d(TAG, "Initiating issue submission with request: " + new Gson().toJson(issueRequest));
        Call<IssueResponse> call = apiService.createIssue(issueRequest);

        call.enqueue(new Callback<IssueResponse>() {
            @Override
            public void onResponse(Call<IssueResponse> call, Response<IssueResponse> response) {
                Log.d(TAG, "Issue submission response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    IssueResponse issueResponse = response.body();
                    Log.d(TAG, "Issue created successfully: " + new Gson().toJson(issueResponse));

                    // Show success message and redirect
                    Toast.makeText(context, "Issue submitted successfully", Toast.LENGTH_SHORT).show();

                    if (context instanceof AppCompatActivity) {
                        Intent intent = new Intent(context, HomeMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        ((AppCompatActivity) context).finish();
                    }
                } else {
                    // Handle error cases
                    String errorMessage = "Failed to submit issue";
                    try {
                        if (response.code() == 401) {
                            errorMessage = "Please log in to submit an issue";
                        } else if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JsonObject errorJson = new JsonParser().parse(errorBody).getAsJsonObject();
                            if (errorJson.has("message")) {
                                errorMessage = errorJson.get("message").getAsString();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<IssueResponse> call, Throwable t) {
                Log.e(TAG, "Issue submission error: " + t.getMessage(), t);
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}