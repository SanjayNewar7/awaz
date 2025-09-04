package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
import android.widget.Spinner;

public class FilterActivity extends AppCompatActivity {

    private static final String TAG = "FilterActivity";
    private static final String BASE_URL = "http://192.168.1.70:8000"; // Your server base URL
    private ShapeableImageView imgProfile; // Profile image view
    private int currentUserId = -1; // To store the current user's ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);

        try {
            // Initialize views with null checks
            ImageView back = findViewById(R.id.backArrow);
            imgProfile = findViewById(R.id.imgProfile);
            LinearLayout settingLayout = findViewById(R.id.settingLayout);
            TextView tabTrending = findViewById(R.id.tabTrending);
            TextView tabAll = findViewById(R.id.tabAll);
            LinearLayout filterLayout = findViewById(R.id.filterLayout);
            LinearLayout raiseIssueLayout = findViewById(R.id.raiseIssueLayout);
            LinearLayout myReportLayout = findViewById(R.id.myReportLayout);
            LinearLayout myNotificationLayout = findViewById(R.id.myNotificationLayout);
            LinearLayout homeLayout = findViewById(R.id.homeLayout);  // Add home layout
            AutoCompleteTextView districtAutoComplete = findViewById(R.id.filterDistrict);
            Spinner wardSpinner = findViewById(R.id.filterWard);
            Spinner issueOnSpinner = findViewById(R.id.filterIssueOn);
            LinearLayout postsContainer = findViewById(R.id.postsContainer);

            // Fetch and load profile image dynamically
            fetchAndLoadProfileImage();

            // Back button click listener
            if (back != null) {
                back.setOnClickListener(view -> {
                    Intent intent = new Intent(FilterActivity.this, HomeMainActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            // Home layout click listener (navigate back to home)
            if (homeLayout != null) {
                homeLayout.setOnClickListener(view -> {
                    Intent intent = new Intent(FilterActivity.this, HomeMainActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            myNotificationLayout.setOnClickListener(view -> {
                Intent intent = new Intent(FilterActivity.this, NotificationActivity.class);
                startActivity(intent);
                finish();
            });



            // Profile icon click listener
            if (imgProfile != null) {
                imgProfile.setOnClickListener(view -> {
                    if (currentUserId != -1) {
                        Intent intent = new Intent(FilterActivity.this, ProfileActivity.class);
                        intent.putExtra("user_id", currentUserId); // Pass current user ID
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "User ID not loaded yet", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // Setting layout click listener
            if (settingLayout != null) {
                settingLayout.setOnClickListener(view -> {
                    Intent intent = new Intent(FilterActivity.this, AllSettingActivity.class);
                    startActivity(intent);
                });
            }

            // Tab Trending click listener
            if (tabTrending != null) {
                tabTrending.setOnClickListener(view -> {
                    Intent intent = new Intent(FilterActivity.this, TrendingActivity.class);
                    startActivity(intent);
                });
            }

            // Tab All click listener
            if (tabAll != null) {
                tabAll.setOnClickListener(view -> {
                    Intent intent = new Intent(FilterActivity.this, PostDetailActivity.class);
                    startActivity(intent);
                });
            }

            // My Report layout click listener
            if (myReportLayout != null) {
                myReportLayout.setOnClickListener(view -> {
                    Intent intent = new Intent(FilterActivity.this, MyReportActivity.class);
                    startActivity(intent);
                });
            }

            // Raise Issue layout click listener
            if (raiseIssueLayout != null) {
                raiseIssueLayout.setOnClickListener(view -> {
                    Intent intent = new Intent(FilterActivity.this, RaiseIssueActivity.class);
                    startActivity(intent);
                });
            }

            // District AutoCompleteTextView setup
            if (districtAutoComplete != null) {
                String[] districts = {
                        "Bharatpur", "Bhaktapur", "Butwal", "Biratnagar", "Birgunj", "Dharan", "Hetauda",
                        "Itahari", "Janakpur", "Kathmandu", "Lalitpur", "Pokhara", "Siddharthanagar"
                };
                ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        districts
                );
                districtAutoComplete.setAdapter(districtAdapter);
                districtAutoComplete.setThreshold(1);
            }

            // Ward Spinner setup
            if (wardSpinner != null) {
                // Create default ward array if resource doesn't exist
                String[] defaultWards = {"Select Ward", "Ward 1", "Ward 2", "Ward 3", "Ward 4", "Ward 5"};
                ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        defaultWards
                );
                wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                wardSpinner.setAdapter(wardAdapter);
                wardSpinner.setSelection(0);
            }

            // Issue On Spinner setup
            if (issueOnSpinner != null) {
                // Create default issue types array if resource doesn't exist
                String[] defaultIssueTypes = {"All Issues", "Water Supply", "Electricity", "Roads", "Waste Management", "Public Transport"};
                ArrayAdapter<String> issueAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        defaultIssueTypes
                );
                issueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                issueOnSpinner.setAdapter(issueAdapter);
                issueOnSpinner.setSelection(0);
            }

            // Loop through posts in postsContainer
            if (postsContainer != null) {
                for (int i = 0; i < postsContainer.getChildCount(); i++) {
                    View postView = postsContainer.getChildAt(i);
                    if (postView != null) {
                        TextView postTitle = postView.findViewById(R.id.postTitle);
                        TextView postDescription = postView.findViewById(R.id.postDescription);

                        postView.setOnClickListener(v -> {
                            Intent intent = new Intent(FilterActivity.this, ItemPostDetailActivity.class);
                            if (postTitle != null && postDescription != null) {
                                intent.putExtra("postTitle", postTitle.getText().toString());
                                intent.putExtra("postDescription", postDescription.getText().toString());
                            }
                            startActivity(intent);
                        });
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            // Handle the error gracefully, maybe show a toast or finish the activity
            finish();
        }
    }

    // Method to fetch user data and load profile image
    private void fetchAndLoadProfileImage() {
        Glide.with(this).clear(imgProfile); // Clear existing image to avoid cache issues
        UserController userController = new UserController(this, null);
        userController.getCurrentUser(RetrofitClient.getAccessToken(this), new UserController.UserDataCallback() {
            @Override
            public void onSuccess(UserData userData) {
                if (userData != null) {
                    currentUserId = userData.getUserId(); // Store the current user's ID
                    Log.d(TAG, "Current userId set to: " + currentUserId);

                    String profileImagePath = userData.getProfileImage();
                    Log.d(TAG, "Profile image path from API: " + profileImagePath);

                    String imageUrl;
                    if (profileImagePath != null && !profileImagePath.isEmpty()) {
                        if (profileImagePath.startsWith("http://") || profileImagePath.startsWith("https://")) {
                            imageUrl = profileImagePath;
                            Log.d(TAG, "Using full URL from API: " + imageUrl);
                        } else {
                            imageUrl = BASE_URL + (profileImagePath.startsWith("/storage/") ? "" : "/storage/") + profileImagePath;
                            Log.d(TAG, "Constructed URL: " + imageUrl);
                        }

                        String accessToken = RetrofitClient.getAccessToken(FilterActivity.this);
                        GlideUrl glideUrl = accessToken != null && !accessToken.isEmpty()
                                ? new GlideUrl(imageUrl, new LazyHeaders.Builder()
                                .addHeader("Authorization", "Bearer " + accessToken)
                                .build())
                                : new GlideUrl(imageUrl);

                        RequestOptions requestOptions = new RequestOptions()
                                .placeholder(R.drawable.profile)
                                .error(R.drawable.profile)
                                .circleCrop();

                        Glide.with(FilterActivity.this)
                                .load(glideUrl)
                                .apply(requestOptions)
                                .into(imgProfile);
                    } else {
                        Log.d(TAG, "No profile image found, using default");
                        imgProfile.setImageResource(R.drawable.profile);
                    }
                } else {
                    Log.e(TAG, "UserData is null");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Failed to fetch user: " + errorMessage);
                Toast.makeText(FilterActivity.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
                imgProfile.setImageResource(R.drawable.profile);
            }
        });
    }
}