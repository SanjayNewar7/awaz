package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.awaz.R;
import com.example.awaz.controller.UserController;
import com.example.awaz.fragments.AllFragment;
import com.example.awaz.fragments.NearbyFragment;
import com.example.awaz.fragments.TrendingFragment;
import com.example.awaz.model.UserData;
import com.example.awaz.service.RetrofitClient;
import com.google.android.material.imageview.ShapeableImageView;

public class HomeMainActivity extends AppCompatActivity {

    private static final String TAG = "HomeMainActivity";
    private static final String BASE_URL = "http://192.168.1.70:8000"; // Your server base URL
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private ShapeableImageView imgProfile; // Profile image view
    private int currentUserId = -1; // To store the current user's ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);

        // Initialize FragmentManager
        fragmentManager = getSupportFragmentManager();

        // Set initial fragment (Nearby)
        if (savedInstanceState == null) {
            loadFragment(new NearbyFragment());
        }

        // Find views
        TextView tabNearby = findViewById(R.id.tabNearby);
        TextView tabTrending = findViewById(R.id.tabTrending);
        TextView tabAll = findViewById(R.id.tabAll);
        imgProfile = findViewById(R.id.imgProfile); // ShapeableImageView
        LinearLayout settingLayout = findViewById(R.id.settingLayout);
        LinearLayout filterLayout = findViewById(R.id.filterLayout);
        LinearLayout raiseIssueLayout = findViewById(R.id.raiseIssueLayout);
        LinearLayout myNotificationLayout = findViewById(R.id.myNotificationLayout);

        // Fetch and load profile image dynamically
        fetchAndLoadProfileImage();

        // Set click listeners for tabs
        tabNearby.setOnClickListener(v -> {
            loadFragment(new NearbyFragment());
            updateTabSelection(tabNearby);
        });

        tabTrending.setOnClickListener(v -> {
            loadFragment(new TrendingFragment());
            updateTabSelection(tabTrending);
        });

        tabAll.setOnClickListener(v -> {
            loadFragment(new AllFragment());
            updateTabSelection(tabAll);
        });

        // Set click listeners for navigation
        myNotificationLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeMainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        raiseIssueLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeMainActivity.this, RaiseIssueActivity.class);
            startActivity(intent);
        });

        imgProfile.setOnClickListener(v -> {
            if (currentUserId != -1) {
                Intent intent = new Intent(HomeMainActivity.this, ProfileActivity.class);
                intent.putExtra("user_id", currentUserId); // Pass current user ID
                startActivity(intent);
            } else {
                Toast.makeText(this, "User ID not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });

        settingLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeMainActivity.this, AllSettingActivity.class);
            startActivity(intent);
        });

        filterLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeMainActivity.this, FilterActivity.class);
            startActivity(intent);
        });
        ShapeableImageView imgSystemNotification = findViewById(R.id.imgSystemNotification);
        imgSystemNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, SystemNotificationActivity.class));
        });

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

                        String accessToken = RetrofitClient.getAccessToken(HomeMainActivity.this);
                        GlideUrl glideUrl = accessToken != null && !accessToken.isEmpty()
                                ? new GlideUrl(imageUrl, new LazyHeaders.Builder()
                                .addHeader("Authorization", "Bearer " + accessToken)
                                .build())
                                : new GlideUrl(imageUrl);

                        RequestOptions requestOptions = new RequestOptions()
                                .placeholder(R.drawable.profile)
                                .error(R.drawable.profile)
                                .circleCrop();

                        Glide.with(HomeMainActivity.this)
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
                Toast.makeText(HomeMainActivity.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
                imgProfile.setImageResource(R.drawable.profile);
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        if (currentFragment != fragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (currentFragment == null) {
                // First load, no animation
            } else if ((currentFragment instanceof NearbyFragment && fragment instanceof TrendingFragment) ||
                    (currentFragment instanceof TrendingFragment && fragment instanceof AllFragment) ||
                    (currentFragment instanceof NearbyFragment && fragment instanceof AllFragment)) {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if ((currentFragment instanceof TrendingFragment && fragment instanceof NearbyFragment) ||
                    (currentFragment instanceof AllFragment && fragment instanceof TrendingFragment) ||
                    (currentFragment instanceof AllFragment && fragment instanceof NearbyFragment)) {
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            }

            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            currentFragment = fragment;
        }
    }

    private void updateTabSelection(TextView selectedTab) {
        TextView[] tabs = {findViewById(R.id.tabNearby), findViewById(R.id.tabTrending), findViewById(R.id.tabAll)};
        for (TextView tab : tabs) {
            if (tab != null) {
                if (tab == selectedTab) {
                    tab.setTextColor(getResources().getColor(R.color.white));
                    tab.setBackgroundResource(R.drawable.tab_button_selected);
                } else {
                    tab.setTextColor(getResources().getColor(R.color.black));
                    tab.setBackgroundResource(0);
                }
            }
        }
    }
}