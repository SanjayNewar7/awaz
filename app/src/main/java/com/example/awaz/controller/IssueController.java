package com.example.awaz.controller;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.model.IssueRequest;
import com.example.awaz.model.IssueResponse;
import com.example.awaz.service.RetrofitClient;
import com.google.gson.Gson;
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
                try {
                    String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                    Log.d(TAG, "Response error body: " + errorBody);
                } catch (IOException e) {
                    Log.e(TAG, "Error reading error body: " + e.getMessage());
                }

                if (response.isSuccessful() && response.body() != null) {
                    IssueResponse issueResponse = response.body();
                    Log.d(TAG, "Issue response: " + new Gson().toJson(issueResponse));
                    Toast.makeText(context, issueResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity) context).finish();
                    }
                } else {
                    String errorMessage = "Failed to submit issue";
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.d(TAG, "Issue error body (raw): " + errorBody);
                        if (errorBody.contains("errors") || errorBody.contains("message")) {
                            Gson gson = new Gson();
                            Map<String, Object> errorJson = gson.fromJson(errorBody, new TypeToken<Map<String, Object>>(){}.getType());
                            if (errorJson != null) {
                                String message = (String) errorJson.get("message");
                                if (message != null && message.contains("Unauthenticated")) {
                                    errorMessage = "Please log in to submit an issue";
                                } else if (message != null) {
                                    errorMessage = "Failed to submit issue: " + message;
                                }
                                Map<String, String[]> errors = (Map<String, String[]>) errorJson.get("errors");
                                if (errors != null) {
                                    StringBuilder errorMsg = new StringBuilder("Failed to submit issue:\n");
                                    errors.forEach((field, messages) -> {
                                        String fieldName = field.replace("_", " ");
                                        errorMsg.append(fieldName.substring(0, 1).toUpperCase())
                                                .append(fieldName.substring(1))
                                                .append(": ")
                                                .append(messages[0])
                                                .append("\n");
                                    });
                                    errorMessage = errorMsg.toString();
                                }
                            }
                        } else {
                            errorMessage = "Failed to submit issue: " + errorBody;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                        errorMessage = "Failed to submit issue: " + response.message();
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<IssueResponse> call, Throwable t) {
                Log.e(TAG, "Issue submission error: " + t.getMessage(), t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}