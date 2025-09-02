package com.example.awaz.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;
import com.example.awaz.service.RetrofitClient;

public class ReapplyCitizenshipActivity extends AppCompatActivity {
    private static final String TAG = "ReapplyCitizenshipActivity";
    private ImageView frontImagePreview;
    private ImageView backImagePreview;
    private Button uploadFrontButton;
    private Button uploadBackButton;
    private Button submitButton;
    private Uri frontImageUri;
    private Uri backImageUri;

    private ActivityResultLauncher<String> pickFrontImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    frontImageUri = uri;
                    frontImagePreview.setImageURI(uri); // This will display the selected image
                }
            }
    );

    private ActivityResultLauncher<String> pickBackImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    backImageUri = uri;
                    backImagePreview.setImageURI(uri); // This will display the selected image
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reapply_citizenship_layout);

        frontImagePreview = findViewById(R.id.frontImagePreview);
        backImagePreview = findViewById(R.id.backImagePreview);
        uploadFrontButton = findViewById(R.id.uploadFrontButton);
        uploadBackButton = findViewById(R.id.uploadBackButton);
        submitButton = findViewById(R.id.submitButton);

        uploadFrontButton.setOnClickListener(v -> pickFrontImage.launch("image/*"));
        uploadBackButton.setOnClickListener(v -> pickBackImage.launch("image/*"));

        submitButton.setOnClickListener(v -> {
            if (frontImageUri != null && backImageUri != null) {
                // TODO: Implement API call to submit images
                Log.d(TAG, "Submitting reapplication with front: " + frontImageUri + ", back: " + backImageUri);
                Toast.makeText(this, "Reapplication submitted (API call pending)", Toast.LENGTH_SHORT).show();
                finish(); // Return to previous activity
            } else {
                Toast.makeText(this, "Please upload both front and back images", Toast.LENGTH_SHORT).show();
            }
        });
    }
}