package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.awaz.R;
import com.example.awaz.fragments.AllFragment;
import com.example.awaz.fragments.NearbyFragment;
import com.example.awaz.fragments.TrendingFragment;

public class HomeMainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Fragment currentFragment;

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
        ImageView profileIcon = findViewById(R.id.imgProfile);
        LinearLayout settingLayout = findViewById(R.id.settingLayout);
        LinearLayout filterLayout = findViewById(R.id.filterLayout);
        LinearLayout raiseIssueLayout = findViewById(R.id.raiseIssueLayout);
        LinearLayout myNotificationLayout = findViewById(R.id.myNotificationLayout);

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

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomeMainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        settingLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeMainActivity.this, AllSettingActivity.class);
            startActivity(intent);
        });

        filterLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeMainActivity.this, FilterActivity.class);
            startActivity(intent);
        });
    }

    private void loadFragment(Fragment fragment) {
        if (currentFragment != fragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // Determine animation direction based on tab sequence
            if (currentFragment == null) {
                // First load, no animation
            } else if ((currentFragment instanceof NearbyFragment && fragment instanceof TrendingFragment) ||
                    (currentFragment instanceof TrendingFragment && fragment instanceof AllFragment) ||
                    (currentFragment instanceof NearbyFragment && fragment instanceof AllFragment))
                   {
                // Forward: slide in right, slide out left
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if ((currentFragment instanceof TrendingFragment && fragment instanceof NearbyFragment) ||
                    (currentFragment instanceof AllFragment && fragment instanceof TrendingFragment) ||
                    (currentFragment instanceof AllFragment && fragment instanceof NearbyFragment))
                     {
                // Backward: slide in left, slide out right
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
                    tab.setBackgroundResource(0); // Remove background
                }
            }
        }
    }
}