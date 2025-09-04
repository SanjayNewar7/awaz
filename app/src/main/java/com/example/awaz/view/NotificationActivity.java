package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.awaz.R;
import com.example.awaz.controller.UserController;
import com.example.awaz.model.NotificationResponse;
import com.example.awaz.model.Post;
import com.example.awaz.model.UserData;
import com.example.awaz.service.RetrofitClient;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "NotificationActivity";
    private ImageView backArrow;
    private ShapeableImageView profileIcon;
    private RecyclerView notificationRecyclerView;
    private NotificationAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private LinearLayout homeLayout, filterLayout, raiseIssueLayout, myNotificationLayout, settingLayout;
    private int currentUserId = -1; // To store the current user's ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        fetchNotifications();
        fetchAndLoadProfileImage(); // Load the logged-in user's profile image
    }

    private void initializeViews() {
        backArrow = findViewById(R.id.backArrow);
        profileIcon = findViewById(R.id.imgProfile);
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyStateText = findViewById(R.id.emptyStateText);
        homeLayout = findViewById(R.id.homeLayout);
        filterLayout = findViewById(R.id.filterLayout);
        raiseIssueLayout = findViewById(R.id.raiseIssueLayout);
        myNotificationLayout = findViewById(R.id.myNotificationLayout);
        settingLayout = findViewById(R.id.settingLayout);
    }

    private void setupRecyclerView() {
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(new ArrayList<>());
        notificationRecyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        backArrow.setOnClickListener(v -> navigateToHome());
        homeLayout.setOnClickListener(v -> navigateToHome());
        filterLayout.setOnClickListener(v -> navigateToFilter());
        raiseIssueLayout.setOnClickListener(v -> navigateToRaiseIssue());
        myNotificationLayout.setOnClickListener(v -> fetchNotifications());
        settingLayout.setOnClickListener(v -> navigateToSettings());
        profileIcon.setOnClickListener(v -> navigateToProfile());
    }

    private void fetchAndLoadProfileImage() {
        Glide.with(this).clear(profileIcon); // Clear existing image to avoid cache issues
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
                            imageUrl = RetrofitClient.getBaseUrl() + (profileImagePath.startsWith("/storage/") ? "" : "/storage/") + profileImagePath;
                            Log.d(TAG, "Constructed URL: " + imageUrl);
                        }

                        String accessToken = RetrofitClient.getAccessToken(NotificationActivity.this);
                        GlideUrl glideUrl = accessToken != null && !accessToken.isEmpty()
                                ? new GlideUrl(imageUrl, new LazyHeaders.Builder()
                                .addHeader("Authorization", "Bearer " + accessToken)
                                .build())
                                : new GlideUrl(imageUrl);

                        RequestOptions requestOptions = new RequestOptions()
                                .placeholder(R.drawable.profile)
                                .error(R.drawable.profile)
                                .circleCrop();

                        Glide.with(NotificationActivity.this)
                                .load(glideUrl)
                                .apply(requestOptions)
                                .into(profileIcon);
                    } else {
                        Log.d(TAG, "No profile image found, using default");
                        profileIcon.setImageResource(R.drawable.profile);
                    }
                } else {
                    Log.e(TAG, "UserData is null");
                    profileIcon.setImageResource(R.drawable.profile);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Failed to fetch user: " + errorMessage);
                Toast.makeText(NotificationActivity.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
                profileIcon.setImageResource(R.drawable.profile);
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToFilter() {
        Intent intent = new Intent(this, FilterActivity.class);
        startActivity(intent);
    }

    private void navigateToRaiseIssue() {
        Intent intent = new Intent(this, RaiseIssueActivity.class);
        startActivity(intent);
    }

    private void navigateToSettings() {
        Intent intent = new Intent(this, AllSettingActivity.class);
        startActivity(intent);
    }

    private void navigateToProfile() {
        if (currentUserId != -1) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("user_id", currentUserId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "User ID not loaded yet", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        notificationRecyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);
    }

    private void showEmptyState(boolean isEmpty) {
        notificationRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyStateText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        emptyStateText.setText("No notifications available");
    }

    private void fetchNotifications() {
        showLoading(true);

        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(this);
        String authToken = RetrofitClient.getAccessToken(this);
        Call<NotificationResponse> call = apiService.getNotifications("Bearer " + authToken);

        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    NotificationResponse notificationResponse = response.body();
                    if ("success".equals(notificationResponse.getStatus())) {
                        List<NotificationResponse.Notification> notifications = notificationResponse.getNotifications();
                        adapter.updateNotifications(notifications);
                        showEmptyState(notifications == null || notifications.isEmpty());
                    } else {
                        handleError("Failed to fetch notifications: " + notificationResponse.getMessage());
                    }
                } else {
                    handleError("Failed to fetch notifications: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                showLoading(false);
                handleError("Network error: " + t.getMessage());
            }
        });
    }

    private void handleError(String errorMessage) {
        Log.e(TAG, errorMessage);
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        showEmptyState(true);
    }

    private void markNotificationAsRead(long notificationId, int position) {
        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(this);
        Call<Void> call = apiService.markNotificationAsRead(notificationId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Notification " + notificationId + " marked as read on server");
                    adapter.notifyItemChanged(position);
                } else {
                    Log.e(TAG, "Failed to mark notification as read on server: " + response.message());
                    Toast.makeText(NotificationActivity.this, "Failed to mark notification as read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error marking notification as read: " + t.getMessage());
                Toast.makeText(NotificationActivity.this, "Network error marking notification", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

        private List<NotificationResponse.Notification> notifications;

        NotificationAdapter(List<NotificationResponse.Notification> notifications) {
            this.notifications = notifications;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            NotificationResponse.Notification item = notifications.get(position);

            // Set background based on read status
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                    item.isRead() ? R.color.white : R.color.notification_uncheck));

            // Construct notification text
            String notificationText = formatNotificationText(item);
            holder.notificationText.setText(notificationText);

            // Format timestamp
            String timestamp = formatTimestamp(item.getTimestamp());
            holder.timestamp.setText(timestamp);

            holder.issueDescription.setText(item.getIssueDescription());
            holder.heartIcon.setImageResource(getIconResId(item.getAction()));

            // Set click listener
            holder.itemView.setOnClickListener(v -> handleNotificationClick(item, position));
        }

        private String formatNotificationText(NotificationResponse.Notification item) {
            String authorName = item.getAuthorName() != null ? item.getAuthorName() : "Unknown";
            String contextPhrase = getContextPhrase(item.getIssueDescription());

            if (item.getAction().contains("warning")) {
                return authorName + " " + item.getAction();
            } else {
                return String.format("%s %s your %s", authorName, item.getAction(), contextPhrase);
            }
        }

        private String formatTimestamp(String timestamp) {
            if (timestamp == null) return "just now";
            return timestamp.replace("minutes", "min")
                    .replace("hours", "hrs")
                    .replace("hour", "hr")
                    .replace("seconds", "sec");
        }

        private void handleNotificationClick(NotificationResponse.Notification item, int position) {
            if (!item.isRead()) {
                item.setRead(true);
                markNotificationAsRead(item.getId(), position);
            }

            navigateToPost(item.getIssueId());
        }

        private void navigateToPost(long issueId) {
            RetrofitClient.ApiService apiService = RetrofitClient.getApiService(NotificationActivity.this);
            Call<Post> call = apiService.getPostByIssueId(issueId);

            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Post post = response.body();
                        Log.d(TAG, "Fetched Post for issueId " + issueId + ": " + post.toString());
                        Log.d(TAG, "Profile Image: " + post.getProfileImage());
                        Log.d(TAG, "Created At: " + post.getCreatedAt());
                        Intent intent = new Intent(NotificationActivity.this, ItemPostDetailActivity.class);
                        intent.putExtra("post", post);
                        startActivity(intent);
                    } else {
                        String errorMessage = response.message() != null ? response.message() : "Unknown error";
                        Log.e(TAG, "Failed to load post for issueId " + issueId + ": " + errorMessage);
                        Toast.makeText(NotificationActivity.this, "Failed to load post: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Log.e(TAG, "Network error fetching post for issueId " + issueId + ": " + t.getMessage());
                    Toast.makeText(NotificationActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        private int getIconResId(String action) {
            switch (action) {
                case "supported": return R.drawable.support_blue;
                case "marked as fixed": return R.drawable.fixed_green;
                case "marked as affected": return R.drawable.affected_yellow;
                case "commented": return R.drawable.comment_icon;
                case "gave you a heart": return R.drawable.heart;
                case "marked as not sure": return R.drawable.not_sure;
                case "marked as invalid": return R.drawable.invalid_white;
                case "received account deletion warning":
                case "wrong post warning": return R.drawable.warning_sign;
                default: return R.drawable.support_blue;
            }
        }

        private String getContextPhrase(String issueDescription) {
            if (issueDescription == null) return "Issue";

            String desc = issueDescription.toLowerCase();
            String[] reportTypes = getResources().getStringArray(R.array.report_types);

            for (String type : reportTypes) {
                String typeLower = type.toLowerCase();
                if (typeLower.equals("all issues")) continue;
                if (desc.equals(typeLower) || desc.contains(typeLower)) {
                    switch (typeLower) {
                        case "roads": return "Road Issue";
                        case "water supply": return "Water Issue";
                        case "electricity": return "Power Issue";
                        case "waste": return "Garbage Issue";
                        case "street lights": return "Street Light Issue";
                        case "traffic": return "Traffic Issue";
                        case "transport": return "Transport Issue";
                        case "drainage": return "Drainage Issue";
                        case "pollution": return "Pollution Issue";
                        case "robbery": return "Robbery Issue";
                        case "community": return "Community Issue";
                        case "healthcare": return "Healthcare Issue";
                        case "education": return "Education Issue";
                        case "environmental": return "Environmental Issue";
                        case "noise": return "Noise Issue";
                        case "government": return "Government Issue";
                        case "parks": return "Park Issue";
                        case "construction": return "Construction Issue";
                        case "animal": return "Animal Issue";
                        case "fire": return "Fire Issue";
                        case "others": return "Other Issue";

                        default: return type + " Issue";
                    }
                }
            }
            return "Issue";
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        public void updateNotifications(List<NotificationResponse.Notification> newNotifications) {
            notifications.clear();
            if (newNotifications != null) {
                notifications.addAll(newNotifications);
            }
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView notificationText, timestamp, issueDescription;
            ImageView heartIcon;

            ViewHolder(View itemView) {
                super(itemView);
                notificationText = itemView.findViewById(R.id.notificationText);
                timestamp = itemView.findViewById(R.id.timestamp);
                issueDescription = itemView.findViewById(R.id.issueDescription);
                heartIcon = itemView.findViewById(R.id.heartIcon);
            }
        }
    }
}