package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;

public class TrendingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trending);

        ImageView profileIcon = findViewById(R.id.imgProfile);
        TextView tabTrending = findViewById(R.id.tabTrending);
        TextView tabAll = findViewById(R.id.tabAll);
        TextView tabNearby = findViewById(R.id.tabNearby);
        LinearLayout myReportLayout = findViewById(R.id.myReportLayout);
        LinearLayout filterLayout = findViewById(R.id.filterLayout);
        LinearLayout raiseIssueLayout = findViewById(R.id.raiseIssueLayout);
        LinearLayout homeLayout = findViewById(R.id.homeLayout);
        LinearLayout settingLayout = findViewById(R.id.settingLayout);

        homeLayout.setOnClickListener(v -> {
            Intent intent = new Intent(TrendingActivity.this, HomeMainActivity.class);
            startActivity(intent);
        });

        raiseIssueLayout.setOnClickListener(v -> {
            Intent intent = new Intent(TrendingActivity.this, HomeMainActivity.class);
            startActivity(intent);
        });

        filterLayout.setOnClickListener(v -> {
            Intent intent = new Intent(TrendingActivity.this, HomeMainActivity.class);
            startActivity(intent);
        });

        myReportLayout.setOnClickListener(v -> {
            Intent intent = new Intent(TrendingActivity.this, HomeMainActivity.class);
            startActivity(intent);
        });

        settingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start PersonalInfoActivity
                Intent intent = new Intent(TrendingActivity.this, AllSettingActivity.class);
                startActivity(intent);
            }
        });

        tabNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AllSettingActivity
                Intent intent = new Intent(TrendingActivity.this, HomeMainActivity.class);
                startActivity(intent);
            }
        });

        tabTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AllSettingActivity
                Intent intent = new Intent(TrendingActivity.this, TrendingActivity.class);
                startActivity(intent);
            }
        });

        tabAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AllSettingActivity
                Intent intent = new Intent(TrendingActivity.this, AllPostActivity.class);
                startActivity(intent);
            }
        });
        // Set click listener for profile icon
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AllSettingActivity
                Intent intent = new Intent(TrendingActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Find the posts container
        LinearLayout postsContainer = findViewById(R.id.postsContainer);

        // Loop through all included item_post layouts (8 in this case)
        for (int i = 0; i < postsContainer.getChildCount(); i++) {
            final View postView = postsContainer.getChildAt(i);

            // Find the title and description TextViews to pass their data
            TextView postTitle = postView.findViewById(R.id.postTitle);
            TextView postDescription = postView.findViewById(R.id.postDescription);

            // Set click listener on the entire post view
            int finalI = i;
            postView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start ItemPostDetailActivity with post data
                    Intent intent = new Intent(TrendingActivity.this, ItemPostDetailActivity.class);
                    if (postTitle != null && postDescription != null) {
                        intent.putExtra("postTitle", postTitle.getText().toString());
                        intent.putExtra("postDescription", postDescription.getText().toString());
                    }
                    startActivity(intent);
                }
            });
        }
    }
}