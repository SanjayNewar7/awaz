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
import androidx.appcompat.app.AppCompatActivity;
import com.example.awaz.R;
import android.widget.Spinner;

public class FilterActivity extends AppCompatActivity {

    private static final String TAG = "FilterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);

        try {
            // Initialize views with null checks
            ImageView back = findViewById(R.id.backArrow);
            ImageView profileIcon = findViewById(R.id.imgProfile);
            LinearLayout settingLayout = findViewById(R.id.settingLayout);
            TextView tabTrending = findViewById(R.id.tabTrending);
            TextView tabAll = findViewById(R.id.tabAll);
            LinearLayout filterLayout = findViewById(R.id.filterLayout);
            LinearLayout raiseIssueLayout = findViewById(R.id.raiseIssueLayout);
            LinearLayout myReportLayout = findViewById(R.id.myReportLayout);
            LinearLayout homeLayout = findViewById(R.id.homeLayout);  // Add home layout
            AutoCompleteTextView districtAutoComplete = findViewById(R.id.filterDistrict);
            Spinner wardSpinner = findViewById(R.id.filterWard);
            Spinner issueOnSpinner = findViewById(R.id.filterIssueOn);
            LinearLayout postsContainer = findViewById(R.id.postsContainer);

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

            // Profile icon click listener
            if (profileIcon != null) {
                profileIcon.setOnClickListener(view -> {
                    Intent intent = new Intent(FilterActivity.this, ProfileActivity.class);
                    startActivity(intent);
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

            // FIXED: Remove the self-referencing click listener for filterLayout
            // Don't set click listener for filterLayout since we're already in FilterActivity

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
}