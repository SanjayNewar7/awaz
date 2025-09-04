package com.example.awaz.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.awaz.R;
import com.example.awaz.controller.UserController;
import com.example.awaz.model.UserData;
import com.example.awaz.service.RetrofitClient;
import com.google.android.material.imageview.ShapeableImageView;

public class AllSettingActivity extends AppCompatActivity {

    private static final String TAG = "AllSettingActivity";
    private static final String BASE_URL = "http://192.168.1.70:8000";
    private static final int PICK_IMAGE_REQUEST = 1;
    private ShapeableImageView profileImage;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String profileImageUrl;
    private TextView userNameTextView;
    private TextView phoneNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_setting);

        // Initialize views
        profileImage = findViewById(R.id.profile_image);
        ImageButton editButton = findViewById(R.id.edit_button);
        LinearLayout personalInfoLayout = findViewById(R.id.personalInfoLayout);
        LinearLayout myCitizenshipLayout = findViewById(R.id.myCitizenshipLayout);
        LinearLayout passwordSecurityLayout = findViewById(R.id.passwordSecurityLayout);
        LinearLayout notificationPreferencesLayout = findViewById(R.id.notificationPreferencesLayout);
        LinearLayout faqLayout = findViewById(R.id.faqLayout);
        LinearLayout helpCenterLayout = findViewById(R.id.helpCenterLayout);
        LinearLayout termsPolicyLayout = findViewById(R.id.termsPolicyLayout);
        LinearLayout aboutUsLayout = findViewById(R.id.aboutUsLayout);
        LinearLayout exitLayout = findViewById(R.id.exitLayout);
        LinearLayout logoutLayout = findViewById(R.id.logoutLayout);
        ImageView back = findViewById(R.id.backArrow);
        userNameTextView = findViewById(R.id.user_name); // Assuming IDs will be added
        phoneNumberTextView = findViewById(R.id.phone_number); // Assuming IDs will be added

        // Fetch and load profile image and user data dynamically
        fetchAndLoadProfileImageAndData();

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        profileImage.setImageURI(selectedImageUri);
                    }
                });

        // Edit button click listener
        editButton.setOnClickListener(v -> {
            new AlertDialog.Builder(AllSettingActivity.this)
                    .setTitle("Update Profile Image")
                    .setMessage("Do you want to update your profile image?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        imagePickerLauncher.launch(intent);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Profile image click listener for full screen view
        profileImage.setOnClickListener(v -> {
            if (profileImageUrl != null) {
                Intent intent = new Intent(AllSettingActivity.this, FullscreenImageActivity.class);
                intent.putExtra("image_url", profileImageUrl);
                startActivity(intent);
            } else {
                Toast.makeText(AllSettingActivity.this, "No profile image available", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button click listener
        back.setOnClickListener(view -> {
            Intent intent = new Intent(AllSettingActivity.this, HomeMainActivity.class);
            startActivity(intent);
        });

        // Other menu item click listeners
        personalInfoLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, PersonalInformationActivity.class);
            startActivity(intent);
        });

        myCitizenshipLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, MyCitizenshipActivity.class);
            startActivity(intent);
        });

        passwordSecurityLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, PasswordandSecurityActivity.class);
            startActivity(intent);
        });

        notificationPreferencesLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, NotificationAndPreferences.class);
            startActivity(intent);
        });

        faqLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, FaqActivity.class);
            startActivity(intent);
        });

        helpCenterLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, HelpCenterActivity.class);
            startActivity(intent);
        });

        termsPolicyLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, TermsandPolicyActivity.class);
            startActivity(intent);
        });

        aboutUsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });

        exitLayout.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });

        logoutLayout.setOnClickListener(v -> {
            // Clear session/token
            RetrofitClient.clearAccessToken(AllSettingActivity.this);

            // Redirect to LoginActivity and clear the back stack
            Intent intent = new Intent(AllSettingActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Finish current activity (optional, because flags already clear stack)
            finish();
        });

    }

    // Method to fetch user data and load profile image
    private void fetchAndLoadProfileImageAndData() {
        Glide.with(this).clear(profileImage); // Clear existing image to avoid cache issues
        UserController userController = new UserController(this, null);
        userController.getCurrentUser(RetrofitClient.getAccessToken(this), new UserController.UserDataCallback() {
            @Override
            public void onSuccess(UserData userData) {
                String profileImagePath = userData.getProfileImage();
                Log.d(TAG, "Profile image path from API: " + profileImagePath);

                if (profileImagePath != null && !profileImagePath.isEmpty()) {
                    if (profileImagePath.startsWith("http://") || profileImagePath.startsWith("https://")) {
                        profileImageUrl = profileImagePath;
                        Log.d(TAG, "Using full URL from API: " + profileImageUrl);
                    } else {
                        profileImageUrl = BASE_URL + (profileImagePath.startsWith("/storage/") ? "" : "/storage/") + profileImagePath;
                        Log.d(TAG, "Constructed URL: " + profileImageUrl);
                    }

                    String accessToken = RetrofitClient.getAccessToken(AllSettingActivity.this);
                    GlideUrl glideUrl = accessToken != null && !accessToken.isEmpty()
                            ? new GlideUrl(profileImageUrl, new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + accessToken)
                            .build())
                            : new GlideUrl(profileImageUrl);

                    RequestOptions requestOptions = new RequestOptions()
                            .placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .circleCrop();

                    Glide.with(AllSettingActivity.this)
                            .load(glideUrl)
                            .apply(requestOptions)
                            .into(profileImage);
                } else {
                    Log.d(TAG, "No profile image found, using default");
                    profileImage.setImageResource(R.drawable.profile);
                    profileImageUrl = null;
                }

                // Update TextViews with user data
                if (userData.getFirstName() != null && userData.getLastName() != null) {
                    String fullName = userData.getFirstName() + " " + userData.getLastName();
                    userNameTextView.setText(fullName);
                } else {
                    userNameTextView.setText("Unknown User");
                }

                if (userData.getPhoneNumber() != null) {
                    phoneNumberTextView.setText(userData.getPhoneNumber());
                } else {
                    phoneNumberTextView.setText("N/A");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Failed to fetch user: " + errorMessage);
                Toast.makeText(AllSettingActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                profileImage.setImageResource(R.drawable.profile);
                profileImageUrl = null;
                userNameTextView.setText("Unknown User");
                phoneNumberTextView.setText("N/A");
            }
        });
    }
}