package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.awaz.R;
import com.example.awaz.controller.UserController;
import com.example.awaz.model.UserData;
import com.example.awaz.service.RetrofitClient;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final String BASE_URL = "http://192.168.1.70:8000"; // Your server base URL
    private boolean isLiked = false;
    private ImageView likeButton;
    private Handler handler = new Handler();
    private PopupWindow popupWindow;
    private ShapeableImageView profileImage; // Profile image view
    private String profileImageUrl; // To store the dynamically loaded image URL
    private TextView userNameTextView;
    private TextView userLocationTextView;
    private TextView postsCountTextView;
    private TextView likesCountTextView;
    private TextView userDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        // Initialize UI elements
        ImageView back = findViewById(R.id.backArrow);
        profileImage = findViewById(R.id.profile_image);
        likeButton = findViewById(R.id.likeButton);
        userNameTextView = findViewById(R.id.userName);
        userLocationTextView = findViewById(R.id.userLocation);
        postsCountTextView = findViewById(R.id.postsCount);
        likesCountTextView = findViewById(R.id.likesCount); // Assuming an ID for likes count
        userDescriptionTextView = findViewById(R.id.userDescription);

        // Initialize filter buttons
        TextView filterAll = findViewById(R.id.filterAll);
        TextView filterRoad = findViewById(R.id.filterRoad);
        TextView filterElectricity = findViewById(R.id.filterElectricity);
        TextView filterSanitary = findViewById(R.id.filterSanitary);
        TextView filterWater = findViewById(R.id.filterWater);
        TextView filterLights = findViewById(R.id.filterLights);
        TextView filterEvents = findViewById(R.id.filterMore1); // Assuming More1 is Events
        TextView filterMore = findViewById(R.id.filterMore2);

        // Fetch and load profile image and user data dynamically
        fetchAndLoadProfileImageAndData();

        // Set click listener for back arrow
        back.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, HomeMainActivity.class);
            startActivity(intent);
        });

        // Set click listener for profile image
        profileImage.setOnClickListener(v -> {
            if (profileImageUrl != null) {
                Intent intent = new Intent(ProfileActivity.this, FullscreenImageActivity.class);
                intent.putExtra("image_url", profileImageUrl);
                startActivity(intent);
            } else {
                Toast.makeText(ProfileActivity.this, "No profile image available", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener for like button
        likeButton.setOnClickListener(v -> {
            isLiked = !isLiked; // Toggle like state
            if (isLiked) {
                likeButton.setImageResource(R.drawable.heart);
                showPopup("You gave a heart");
            } else {
                likeButton.setImageResource(R.drawable.disheart);
                showPopup("You removed heart");
            }
        });

        // Set initial state (e.g., "All" selected by default)
        setFilterBackground(filterAll, true);
        resetOtherFilters(filterAll);

        // Add click listeners for filter buttons
        View.OnClickListener filterClickListener = v -> {
            TextView clickedFilter = (TextView) v;
            setFilterBackground(clickedFilter, true);
            resetOtherFilters(clickedFilter);
            // Add your filtering logic here (e.g., update posts based on filter)
            // Example: filterPosts(clickedFilter.getText().toString());
        };

        // Attach click listeners to filter buttons (null checks included)
        if (filterAll != null) filterAll.setOnClickListener(filterClickListener);
        if (filterRoad != null) filterRoad.setOnClickListener(filterClickListener);
        if (filterElectricity != null) filterElectricity.setOnClickListener(filterClickListener);
        if (filterSanitary != null) filterSanitary.setOnClickListener(filterClickListener);
        if (filterWater != null) filterWater.setOnClickListener(filterClickListener);
        if (filterLights != null) filterLights.setOnClickListener(filterClickListener);
        if (filterEvents != null) filterEvents.setOnClickListener(filterClickListener);
        if (filterMore != null) filterMore.setOnClickListener(filterClickListener);
    }

    // Method to fetch user data and load profile image
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

                    String accessToken = RetrofitClient.getAccessToken(ProfileActivity.this);
                    GlideUrl glideUrl = accessToken != null && !accessToken.isEmpty()
                            ? new GlideUrl(profileImageUrl, new LazyHeaders.Builder()
                            .addHeader("Authorization", "Bearer " + accessToken)
                            .build())
                            : new GlideUrl(profileImageUrl);

                    RequestOptions requestOptions = new RequestOptions()
                            .placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .circleCrop();

                    Glide.with(ProfileActivity.this)
                            .load(glideUrl)
                            .apply(requestOptions)
                            .into(profileImage);
                } else {
                    Log.d(TAG, "No profile image found, using default");
                    profileImage.setImageResource(R.drawable.profile);
                    profileImageUrl = null;
                }

                // Update user name (first_name + last_name)
                if (userData.getFirstName() != null && userData.getLastName() != null) {
                    String fullName = userData.getFirstName() + " " + userData.getLastName();
                    userNameTextView.setText(fullName);
                } else {
                    userNameTextView.setText("Unknown User");
                }

                // Update user location (city-ward, district)
                String city = userData.getCity() != null ? userData.getCity() : "";
                String district = userData.getDistrict() != null ? userData.getDistrict() : "";
                int ward = userData.getWard(); // Assuming getWard() returns int, default to 0 if not set
                if (!city.isEmpty() && district.isEmpty()) {
                    userLocationTextView.setText(city);
                } else if (city.isEmpty() && !district.isEmpty()) {
                    userLocationTextView.setText(district);
                } else if (!city.isEmpty() && !district.isEmpty()) {
                    String location = city + "-" + ward + ", " + district;
                    userLocationTextView.setText(location);
                } else {
                    userLocationTextView.setText("Unknown Location");
                }

                // Update posts and likes count
                int postsCount = userData.getPostsCount() != 0 ? userData.getPostsCount() : 0;
                postsCountTextView.setText(postsCount + " Posts");
                int likesCount = userData.getLikesCount() != 0 ? userData.getLikesCount() : 0;
                likesCountTextView.setText("   " + likesCount + " Likes");

                // Update user description (bio)
                if (userData.getBio() != null) {
                    userDescriptionTextView.setText(userData.getBio());
                } else {
                    userDescriptionTextView.setText("No bio available.");
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Failed to fetch user: " + errorMessage);
                Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                profileImage.setImageResource(R.drawable.profile);
                profileImageUrl = null;
                userNameTextView.setText("Unknown User");
                userLocationTextView.setText("Unknown Location");
                postsCountTextView.setText("0 Posts");
                likesCountTextView.setText("   0 Likes");
                userDescriptionTextView.setText("No bio available.");
            }
        });
    }

    // Method to set the background of a filter button
    private void setFilterBackground(TextView filter, boolean isSelected) {
        if (filter != null) {
            if (isSelected) {
                filter.setBackgroundResource(R.drawable.rounded_button_blue);
                filter.setTextColor(ContextCompat.getColor(this, R.color.white));
            } else {
                filter.setBackgroundResource(R.drawable.rounded_button_grey);
                filter.setTextColor(ContextCompat.getColor(this, R.color.black));
            }
        }
    }

    // Method to reset backgrounds of all other filters
    private void resetOtherFilters(TextView selectedFilter) {
        TextView[] filters = {
                findViewById(R.id.filterAll),
                findViewById(R.id.filterRoad),
                findViewById(R.id.filterElectricity),
                findViewById(R.id.filterSanitary),
                findViewById(R.id.filterWater),
                findViewById(R.id.filterLights),
                findViewById(R.id.filterMore1),
                findViewById(R.id.filterMore2)
        };
        for (TextView filter : filters) {
            if (filter != null && filter != selectedFilter) {
                setFilterBackground(filter, false);
            }
        }
    }

    private void showPopup(String message) {
        // Inflate the custom popup layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.popup_like_message, null);

        // Set the message
        TextView popupText = popupView.findViewById(R.id.popup_text);
        popupText.setText(message);

        // Configure icon tint based on message
        ImageView iconLike = popupView.findViewById(R.id.icon_like);
        if ("You removed heart".equals(message)) {
            iconLike.setImageTintList(ContextCompat.getColorStateList(this, R.color.black));
        } else if ("You gave a heart".equals(message)) {
            iconLike.setImageTintList(null); // Use default red color of heart drawable
        }

        // Create and configure PopupWindow
        popupWindow = new PopupWindow(popupView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent)); // Required for dismissal

        // Show at the center of the screen
        View rootView = findViewById(android.R.id.content);
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);

        // Dismiss after 1.5 seconds
        handler.postDelayed(() -> {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }, 1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Clean up Handler
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}