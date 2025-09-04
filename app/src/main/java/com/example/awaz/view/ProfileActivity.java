package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.example.awaz.adapter.PostAdapter;
import com.example.awaz.controller.UserController;
import com.example.awaz.model.LikeResponse;
import com.example.awaz.model.Post;
import com.example.awaz.model.PostsResponse;
import com.example.awaz.model.UserData;
import com.example.awaz.service.RetrofitClient;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final String BASE_URL = "http://192.168.1.70:8000";
    private boolean isLiked = false;
    private ImageView likeButton;
    private Handler handler = new Handler();
    private PopupWindow popupWindow;
    private ShapeableImageView profileImage;
    private String profileImageUrl;
    private TextView userNameTextView;
    private TextView userLocationTextView;
    private TextView postsCountTextView;
    private TextView likesCountTextView;
    private TextView userDescriptionTextView;
    private int userId;
    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> userPosts = new ArrayList<>();
    private LinearLayout postsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        ImageView back = findViewById(R.id.backArrow);
        profileImage = findViewById(R.id.profile_image);
        likeButton = findViewById(R.id.likeButton);
        userNameTextView = findViewById(R.id.userName);
        userLocationTextView = findViewById(R.id.userLocation);
        postsCountTextView = findViewById(R.id.postsCount);
        likesCountTextView = findViewById(R.id.likesCount);
        userDescriptionTextView = findViewById(R.id.userDescription);
        postsContainer = findViewById(R.id.postsContainer);

        // Initialize RecyclerView
        postsRecyclerView = new RecyclerView(this);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(this, userPosts);
        postsRecyclerView.setAdapter(postAdapter);

        // Replace the hardcoded posts with RecyclerView
        postsContainer.removeAllViews();
        postsContainer.addView(postsRecyclerView);
        postsContainer.setOrientation(LinearLayout.VERTICAL);

        TextView filterAll = findViewById(R.id.filterAll);
        TextView filterRoad = findViewById(R.id.filterRoad);
        TextView filterElectricity = findViewById(R.id.filterElectricity);
        TextView filterSanitary = findViewById(R.id.filterSanitary);
        TextView filterWater = findViewById(R.id.filterWater);
        TextView filterLights = findViewById(R.id.filterLights);
        TextView filterEvents = findViewById(R.id.filterMore1);
        TextView filterMore = findViewById(R.id.filterMore2);

        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", -1);
        Log.d(TAG, "Received userId: " + userId);
        if (userId == -1) {
            Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchAndLoadProfileImageAndData();
        fetchUserPosts();

        back.setOnClickListener(view -> {
            Intent intentBack = new Intent(ProfileActivity.this, HomeMainActivity.class);
            startActivity(intentBack);
        });

        profileImage.setOnClickListener(v -> {
            if (profileImageUrl != null) {
                Intent intentGo = new Intent(ProfileActivity.this, FullscreenImageActivity.class);
                intentGo.putExtra("image_url", profileImageUrl);
                startActivity(intentGo);
            } else {
                Toast.makeText(ProfileActivity.this, "No profile image available", Toast.LENGTH_SHORT).show();
            }
        });

        likeButton.setOnClickListener(v -> toggleLike());

        setFilterBackground(filterAll, true);
        resetOtherFilters(filterAll);

        View.OnClickListener filterClickListener = v -> {
            TextView clickedFilter = (TextView) v;
            setFilterBackground(clickedFilter, true);
            resetOtherFilters(clickedFilter);
            String category = clickedFilter.getText().toString();
            filterPostsByCategory(category);
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

    private void toggleLike() {
        String accessToken = RetrofitClient.getAccessToken(this);
        if (accessToken == null) {
            Toast.makeText(this, "Please log in to like this profile", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(this);
        Call<LikeResponse> call = apiService.toggleLike(userId);

        call.enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                // Log raw response for debugging
                Log.d(TAG, "Toggle like raw response: HTTP " + response.code() + ", Body: " + (response.body() != null ? response.body().toString() : "null"));
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        LikeResponse likeResponse = response.body();
                        Log.d(TAG, "Toggle like parsed response: " + likeResponse.toString());
                        if ("success".equals(likeResponse.getStatus())) {
                            isLiked = likeResponse.isLiked();
                            int likesCount = likeResponse.getLikesCount();
                            String message = likeResponse.getMessage();

                            // Update UI
                            likeButton.setImageResource(isLiked ? R.drawable.heart : R.drawable.disheart);
                            likesCountTextView.setText("   " + likesCount + (likesCount == 1 ? " Like" : " Likes"));
                            showPopup(message);
                        } else {
                            String message = likeResponse.getMessage() != null ? likeResponse.getMessage() : "Failed to toggle like";
                            Log.e(TAG, "API Error: " + message);
                            Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            // Fallback: Refresh profile data
                            fetchAndLoadProfileImageAndData();
                        }
                    } else {
                        String errorBody = "Unknown error";
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                        Log.e(TAG, "Failed to toggle like: HTTP " + response.code() + ", Body: " + errorBody);
                        String message = response.code() == 403 ? "Cannot like your own profile" :
                                response.code() == 401 ? "Please log in to like this profile" :
                                        "Failed to toggle like: " + errorBody;
                        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        // Fallback: Refresh profile data
                        fetchAndLoadProfileImageAndData();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing toggle like response: " + e.getMessage(), e);
                    Toast.makeText(ProfileActivity.this, "Error processing like response", Toast.LENGTH_SHORT).show();
                    // Fallback: Refresh profile data
                    fetchAndLoadProfileImageAndData();
                }
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) {
                Log.e(TAG, "Network error in toggleLike: " + t.getMessage(), t);
                Toast.makeText(ProfileActivity.this, "Network error: Unable to toggle like", Toast.LENGTH_SHORT).show();
                // Fallback: Refresh profile data
                fetchAndLoadProfileImageAndData();
            }
        });
    }

    private void fetchAndLoadProfileImageAndData() {
        Glide.with(this).clear(profileImage);
        UserController userController = new UserController(this, null);
        userController.getUserById(userId, RetrofitClient.getAccessToken(this), new UserController.UserDataCallback() {
            @Override
            public void onSuccess(UserData userData) {
                Log.d(TAG, "Fetched user data: " + (userData != null ? userData.toString() : "null"));
                if (userData == null) {
                    Log.e(TAG, "UserData is null, check API response");
                    onFailure("Received null user data");
                    return;
                }

                String profileImagePath = userData.getProfileImage();
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

                userNameTextView.setText((userData.getFirstName() != null && userData.getLastName() != null)
                        ? userData.getFirstName() + " " + userData.getLastName() : "Unknown User");

                String city = userData.getCity() != null ? userData.getCity() : "";
                String district = userData.getDistrict() != null ? userData.getDistrict() : "";
                int ward = userData.getWard() != -1 ? userData.getWard() : 0;
                userLocationTextView.setText(!city.isEmpty() && !district.isEmpty()
                        ? city + "-" + ward + ", " + district
                        : !city.isEmpty() ? city
                        : !district.isEmpty() ? district
                        : "Unknown Location");

                postsCountTextView.setText(userData.getPostsCount() + (userData.getPostsCount() == 1 ? " Post" : " Posts"));
                likesCountTextView.setText("   " + userData.getLikesCount() + (userData.getLikesCount() == 1 ? " Like" : " Likes"));
                isLiked = userData.isLiked(); // Set initial like status
                likeButton.setImageResource(isLiked ? R.drawable.heart : R.drawable.disheart);

                userDescriptionTextView.setText(userData.getBio() != null ? userData.getBio() : "No bio available.");
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

    private void fetchUserPosts() {
        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(this);
        Call<PostsResponse> call = apiService.getPosts();

        call.enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PostsResponse postsResponse = response.body();
                    if ("success".equals(postsResponse.getStatus())) {
                        // Filter posts by the current user ID
                        List<Post> allPosts = postsResponse.getPosts();
                        userPosts.clear();

                        for (Post post : allPosts) {
                            if (post.getUserId() == userId) {
                                userPosts.add(post);
                            }
                        }

                        postAdapter.notifyDataSetChanged();

                        if (userPosts.isEmpty()) {
                            Toast.makeText(ProfileActivity.this, "No posts found for this user", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorBody = "Unknown error";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                        Log.e(TAG, "Failed to fetch posts: HTTP " + response.code() + ", Body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading posts error body: " + e.getMessage());
                    }
                    Toast.makeText(ProfileActivity.this, "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {
                Log.e(TAG, "Network error fetching posts: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterPostsByCategory(String category) {
        if (category.equalsIgnoreCase("All")) {
            // Show all posts
            fetchUserPosts();
        } else {
            // Filter posts by category
            RetrofitClient.ApiService apiService = RetrofitClient.getApiService(this);
            Call<PostsResponse> call = apiService.getPosts();

            call.enqueue(new Callback<PostsResponse>() {
                @Override
                public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PostsResponse postsResponse = response.body();
                        if ("success".equals(postsResponse.getStatus())) {
                            List<Post> allPosts = postsResponse.getPosts();
                            userPosts.clear();

                            for (Post post : allPosts) {
                                if (post.getUserId() == userId &&
                                        post.getCategory().equalsIgnoreCase(category)) {
                                    userPosts.add(post);
                                }
                            }

                            postAdapter.notifyDataSetChanged();

                            if (userPosts.isEmpty()) {
                                Toast.makeText(ProfileActivity.this, "No " + category + " posts found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorBody = "Unknown error";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                            Log.e(TAG, "Failed to filter posts: HTTP " + response.code() + ", Body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading filter posts error body: " + e.getMessage());
                        }
                        Toast.makeText(ProfileActivity.this, "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PostsResponse> call, Throwable t) {
                    Log.e(TAG, "Network error filtering posts: " + t.getMessage());
                    Toast.makeText(ProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

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
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.popup_like_message, null);

        TextView popupText = popupView.findViewById(R.id.popup_text);
        popupText.setText(message);

        ImageView iconLike = popupView.findViewById(R.id.icon_like);
        if ("You removed heart".equals(message)) {
            iconLike.setImageTintList(ContextCompat.getColorStateList(this, R.color.black));
        } else if ("You gave a heart".equals(message)) {
            iconLike.setImageTintList(null);
        }

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));

        View rootView = findViewById(android.R.id.content);
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);

        handler.postDelayed(() -> {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        }, 1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}