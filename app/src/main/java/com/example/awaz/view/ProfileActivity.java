package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.awaz.R;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {
    private boolean isLiked = false;
    private ImageView likeButton;
    private Handler handler = new Handler();
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, HomeMainActivity.class);
            startActivity(intent);
        });

        // Add click listener for profile image
        ShapeableImageView profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FullscreenImageActivity.class);
            intent.putExtra("image_resource", R.drawable.profile);
            startActivity(intent);
        });

        // Add click listener for like button
        likeButton = findViewById(R.id.likeButton);
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

        // Initialize filter buttons
        TextView filterAll = findViewById(R.id.filterAll);
        TextView filterRoad = findViewById(R.id.filterRoad);
        TextView filterElectricity = findViewById(R.id.filterElectricity);
        TextView filterSanitary = findViewById(R.id.filterSanitary);
        TextView filterWater = findViewById(R.id.filterWater);
        TextView filterLights = findViewById(R.id.filterLights);
        TextView filterEvents = findViewById(R.id.filterMore1); // Assuming More1 is Events
        TextView filterMore = findViewById(R.id.filterMore2);

        // Set initial state (e.g., "All" selected by default)
        setFilterBackground(filterAll, true);
        resetOtherFilters(filterAll);

        // Add click listeners for filter buttons
        View.OnClickListener filterClickListener = v -> {
            TextView clickedFilter = (TextView) v;
            setFilterBackground(clickedFilter, true);
            resetOtherFilters(clickedFilter);
        };

        if (filterAll != null) filterAll.setOnClickListener(filterClickListener);
        if (filterRoad != null) filterRoad.setOnClickListener(filterClickListener);
        if (filterElectricity != null) filterElectricity.setOnClickListener(filterClickListener);
        if (filterSanitary != null) filterSanitary.setOnClickListener(filterClickListener);
        if (filterWater != null) filterWater.setOnClickListener(filterClickListener);
        if (filterLights != null) filterLights.setOnClickListener(filterClickListener);
        if (filterEvents != null) filterEvents.setOnClickListener(filterClickListener);
        if (filterMore != null) filterMore.setOnClickListener(filterClickListener);
    }

    // Method to set the background of a filter button
    private void setFilterBackground(TextView filter, boolean isSelected) {
        if (filter != null) {
            if (isSelected) {
                filter.setBackgroundResource(R.drawable.rounded_button_blue);
                filter.setTextColor(ContextCompat.getColor(this, R.color.white)); // Optional: Change text color for selected state
            } else {
                filter.setBackgroundResource(R.drawable.rounded_button_grey);
                filter.setTextColor(ContextCompat.getColor(this, R.color.black)); // Optional: Reset text color
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