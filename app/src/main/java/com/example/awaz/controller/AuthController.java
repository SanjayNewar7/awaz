package com.example.awaz.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.service.RetrofitClient;
import com.example.awaz.model.SignupRequest;
import com.example.awaz.model.SignupResponse;
import com.example.awaz.view.LoginActivity;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthController {
    private static final String TAG = "AuthController";
    private Context context;
    private RetrofitClient.ApiService apiService;

    public AuthController(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService();
    }

    public boolean validateSignupFields(EditText editFirstName, EditText editLastName, EditText editDistrict,
                                        EditText editWard, EditText editCity, EditText editAreaName,
                                        EditText editPhone, EditText editEmail, EditText editPassword,
                                        EditText editConfirmPassword, EditText editCitizenshipNumber,
                                        EditText editUsername, boolean agreedToTerms, int genderId) {
        boolean isValid = true;

        Map<EditText, String> fields = new HashMap<>();
        fields.put(editFirstName, "First name is required");
        fields.put(editLastName, "Last name is required");
        fields.put(editDistrict, "District is required");
        fields.put(editWard, "Ward is required");
        fields.put(editCity, "City is required");
        fields.put(editAreaName, "Area name is required");
        fields.put(editPhone, "Phone number is required");
        fields.put(editEmail, "Email is required");
        fields.put(editPassword, "Password is required");
        fields.put(editConfirmPassword, "Confirm password is required");
        fields.put(editCitizenshipNumber, "Citizenship number is required");
        fields.put(editUsername, "Username is required");

        for (Map.Entry<EditText, String> entry : fields.entrySet()) {
            if (entry.getKey().getText().toString().trim().isEmpty()) {
                entry.getKey().setError(entry.getValue());
                isValid = false;
            } else {
                entry.getKey().setError(null);
            }
        }

        String email = editEmail.getText().toString().trim();
        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Please enter a valid email address");
            isValid = false;
        }

        String phone = editPhone.getText().toString().trim();
        if (!phone.isEmpty() && !phone.matches("\\d{10}")) {
            editPhone.setError("Phone number must be 10 digits");
            isValid = false;
        }

        String password = editPassword.getText().toString().trim();
        if (!password.isEmpty() && password.length() < 8) {
            editPassword.setError("Password must be at least 8 characters");
            isValid = false;
        }

        String confirmPassword = editConfirmPassword.getText().toString().trim();
        if (!password.isEmpty() && !confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
            editConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        if (genderId == -1) {
            Toast.makeText(context, "Please select a gender", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (!agreedToTerms) {
            Toast.makeText(context, "You must agree to the terms and conditions", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    public String convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                return null;
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            byte[] imageBytes = outputStream.toByteArray();
            return "data:image/jpeg;base64," + android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
        } catch (IOException e) {
            Log.e(TAG, "Error converting image to Base64: " + e.getMessage());
            return null;
        }
    }

    public void signup(SignupRequest signupRequest) {
        Log.d(TAG, "Initiating signup with request: " + new Gson().toJson(signupRequest));
        Call<SignupResponse> call = apiService.signup(signupRequest);

        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                Log.d(TAG, "Signup response code: " + response.code());
                Log.d(TAG, "Response body: " + (response.body() != null ? new Gson().toJson(response.body()) : "null"));
                try {
                    Log.d(TAG, "Error body: " + (response.errorBody() != null ? response.errorBody().toString() : response.errorBody() != null ? response.errorBody().string() : "null"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (response.isSuccessful() && response.body() != null) {
                    SignupResponse signupResponse = response.body();
                    Log.d(TAG, "Signup response: " + signupResponse.toString());
                    Toast.makeText(context, signupResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity) context).finish();
                    }
                } else {
                    Log.d(TAG, "Signup failed with message: " + response.message());
                    String errorMessage = "Registration failed";
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.d(TAG, "Signup error body (raw): " + errorBody);
                        if (errorBody.contains("errors") || errorBody.contains("messsage")) {
                            Gson gson = new Gson();
                            Map<String, Object> errorJson = gson.fromJson(errorBody, Map.class);
                            if (errorJson != null) {
                                String message = (String) errorJson.get("messsage") != null ? (String) errorJson.get("messsage") : (String) errorJson.get("message");
                                if (message != null && message.contains("CSRF token mismatch")) {
                                    errorMessage = "Registration failed: CSRF token mismatch";
                                }
                                Map<String, String[]> errors = (Map<String, String[]>) errorJson.get("errors");
                                if (errors != null) {
                                    StringBuilder errorMsg = new StringBuilder("Registration failed:\n");
                                    errors.forEach((field, messages) -> {
                                        String fieldName = field.replace("_", " ");
                                        errorMsg.append(fieldName.substring(0, 1).toUpperCase())
                                                .append(fieldName.substring(1))
                                                .append(": ")
                                                .append(messages[0])
                                                .append("\n");
                                    });
                                    errorMessage = errorMsg.toString();
                                } else if (message != null) {
                                    errorMessage = "Registration failed: " + message;
                                }
                            }
                        } else {
                            errorMessage = "Registration failed: " + errorBody;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage(), e);
                        errorMessage = "Registration failed: " + response.message();
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                Log.e(TAG, "Signup error: " + t.getMessage(), t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean validateLoginFields(EditText editEmail, EditText editPassword) {

        return false;
    }

    public void login(String email, String password) {

    }
}