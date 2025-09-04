package com.example.awaz.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;
import com.example.awaz.controller.AuthController;
import com.example.awaz.model.CitizenshipImageRequest;
import com.example.awaz.model.UserResponse;
import com.example.awaz.service.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReapplyCitizenshipActivity extends AppCompatActivity {
    private static final String TAG = "ReapplyCitizenshipActivity";
    private ImageView frontImagePreview;
    private ImageView backImagePreview;
    private Button uploadFrontButton;
    private Button uploadBackButton;
    private Button submitButton;
    private Uri frontImageUri;
    private Uri backImageUri;
    private AuthController authController;

    private ActivityResultLauncher<String> pickFrontImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    frontImageUri = uri;
                    frontImagePreview.setImageURI(uri);
                    Toast.makeText(this, "Front photo uploaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No image selected for front", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private ActivityResultLauncher<String> pickBackImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    backImageUri = uri;
                    backImagePreview.setImageURI(uri);
                    Toast.makeText(this, "Back photo uploaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No image selected for back", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reapply_citizenship_layout);

        // Initialize AuthController
        authController = new AuthController(this);

        // Initialize views
        ImageView backButton = findViewById(R.id.backButton);
        frontImagePreview = findViewById(R.id.frontImagePreview);
        backImagePreview = findViewById(R.id.backImagePreview);
        uploadFrontButton = findViewById(R.id.uploadFrontButton);
        uploadBackButton = findViewById(R.id.uploadBackButton);
        submitButton = findViewById(R.id.submitButton);

        // Set placeholder images
        frontImagePreview.setImageResource(R.drawable.gallery_icon);
        backImagePreview.setImageResource(R.drawable.gallery_icon);

        // Set back button listener
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReapplyCitizenshipActivity.this, MyCitizenshipActivity.class);
            startActivity(intent);
            finish();
        });


        // Set upload button listeners
        uploadFrontButton.setOnClickListener(v -> pickFrontImage.launch("image/*"));
        uploadBackButton.setOnClickListener(v -> pickBackImage.launch("image/*"));

        // Set submit button listener
        submitButton.setOnClickListener(v -> {
            if (frontImageUri != null && backImageUri != null) {
                String base64Front = authController.convertImageToBase64(frontImageUri);
                String base64Back = authController.convertImageToBase64(backImageUri);

                if (base64Front == null || base64Back == null) {
                    Toast.makeText(this, "Error converting images to Base64", Toast.LENGTH_SHORT).show();
                    return;
                }

                CitizenshipImageRequest request = new CitizenshipImageRequest(base64Front, base64Back);

                RetrofitClient.ApiService apiService = RetrofitClient.getApiService(this);
                apiService.updateCitizenshipImages(request).enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(ReapplyCitizenshipActivity.this,
                                    "Citizenship images updated successfully!",
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Response: " + response.body().toString());
                            finish();
                        } else {
                            Toast.makeText(ReapplyCitizenshipActivity.this,
                                    "Update failed: " + response.message(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error: " + response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Toast.makeText(ReapplyCitizenshipActivity.this,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failure: " + t.getMessage());
                    }
                });
            } else {
                Toast.makeText(this, "Please upload both front and back images", Toast.LENGTH_SHORT).show();
            }
        });
    }
}