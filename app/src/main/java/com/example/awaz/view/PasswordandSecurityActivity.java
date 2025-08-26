package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.awaz.R;
import com.example.awaz.controller.AuthController;
import com.example.awaz.service.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordandSecurityActivity extends AppCompatActivity {

    private static final String TAG = "PasswordandSecurity";
    private TextInputEditText currentPasswordEditText, newPasswordEditText, retypePasswordEditText;
    private TextInputLayout currentPasswordLayout, newPasswordLayout, retypePasswordLayout;
    private Button saveButton, cancelButton;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        // Initialize views
        ImageView back = findViewById(R.id.backArrow);
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        retypePasswordEditText = findViewById(R.id.retypePasswordEditText);
        currentPasswordLayout = findViewById(R.id.currentPasswordLayout);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        retypePasswordLayout = findViewById(R.id.retypePasswordLayout);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Initialize AuthController
        authController = new AuthController(this);

        // Back button click listener
        back.setOnClickListener(view -> {
            Intent intent = new Intent(PasswordandSecurityActivity.this, AllSettingActivity.class);
            startActivity(intent);
            finish();
        });

        // Cancel button click listener
        cancelButton.setOnClickListener(view -> {
            Intent intent = new Intent(PasswordandSecurityActivity.this, AllSettingActivity.class);
            startActivity(intent);
            finish();
        });

        // Save button click listener
        saveButton.setOnClickListener(view -> {
            if (validateInputs()) {
                changePassword();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = retypePasswordEditText.getText().toString().trim();

        // Reset errors
        currentPasswordLayout.setError(null);
        newPasswordLayout.setError(null);
        retypePasswordLayout.setError(null);

        // Validate current password
        if (currentPassword.isEmpty()) {
            currentPasswordLayout.setError("Current password is required");
            isValid = false;
        }

        // Validate new password
        if (newPassword.isEmpty()) {
            newPasswordLayout.setError("New password is required");
            isValid = false;
        } else if (newPassword.length() < 8) {
            newPasswordLayout.setError("Password must be at least 8 characters");
            isValid = false;
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            retypePasswordLayout.setError("Please confirm your new password");
            isValid = false;
        } else if (!newPassword.equals(confirmPassword)) {
            retypePasswordLayout.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    private void changePassword() {
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = retypePasswordEditText.getText().toString().trim();

        authController.changePassword(currentPassword, newPassword, confirmPassword, new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseJson = response.body();
                    String status = responseJson.get("status").getAsString();
                    String message = responseJson.get("message").getAsString();

                    if ("success".equals(status)) {
                        Toast.makeText(PasswordandSecurityActivity.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PasswordandSecurityActivity.this, AllSettingActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        currentPasswordLayout.setError(message);
                        Log.e(TAG, "Password change failed: " + message);
                    }
                } else {
                    String errorMessage = "Failed to change password";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response: " + e.getMessage());
                    }
                    currentPasswordLayout.setError(errorMessage);
                    Log.e(TAG, "Password change failed: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
                Toast.makeText(PasswordandSecurityActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}