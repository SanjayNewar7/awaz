package com.example.awaz.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.awaz.R;
import com.example.awaz.adapter.CommentAdapter;
import com.example.awaz.controller.UserController;
import com.example.awaz.databinding.ItemPostDetailActivityBinding;
import com.example.awaz.model.CommentRequest;
import com.example.awaz.model.CommentResponse;
import com.example.awaz.model.CommentsResponse;
import com.example.awaz.model.Post;
import com.example.awaz.model.ReactionRequest;
import com.example.awaz.model.ReactionResponse;
import com.example.awaz.model.UserData;
import com.example.awaz.service.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemPostDetailActivity extends AppCompatActivity {

    private static final String TAG = "ItemPostDetailActivity";
    private ItemPostDetailActivityBinding binding;
    private Post post;
    private CommentAdapter commentAdapter;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private String selectedImageBase64;
    private Map<String, Boolean> userReactions = new HashMap<>();
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ItemPostDetailActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra("post");
        if (post == null) {
            Toast.makeText(this, "Error: Post data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        commentAdapter = new CommentAdapter(this, new java.util.ArrayList<>());
        binding.recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerComments.setAdapter(commentAdapter);

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageBase64 = convertImageToBase64(uri);
                        binding.commentImagePreview.setVisibility(View.VISIBLE);
                        Glide.with(this).load(uri).into(binding.commentImagePreview);
                    }
                });

        populatePostDetails();
        fetchAndLoadProfileImage();
        fetchComments();

        binding.sendButton.setOnClickListener(v -> submitComment());
        binding.galleryButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        binding.backArrow.setOnClickListener(v -> finish());
        binding.threeDotIcon.setOnClickListener(v -> showBottomSheetMenu());

        binding.imgProfile.setOnClickListener(v -> {
            if (currentUserId != -1) {
                Intent profileIntent = new Intent(ItemPostDetailActivity.this, ProfileActivity.class);
                profileIntent.putExtra("user_id", currentUserId);
                startActivity(profileIntent);
            } else {
                Toast.makeText(this, "User ID not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });

        setupReactionListeners();
    }

    private void showBottomSheetMenu() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_menu, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView shareOption = bottomSheetView.findViewById(R.id.share_option);
        TextView reportOption = bottomSheetView.findViewById(R.id.report_option);
        TextView copyLinkOption = bottomSheetView.findViewById(R.id.copy_link_option);

        shareOption.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Post");
            shareIntent.putExtra(Intent.EXTRA_TEXT, post.getTitle() + "\n" + post.getDescription() + "\n\nView more: " + RetrofitClient.getBaseUrl() + "/post/" + post.getIssueId());
            startActivity(Intent.createChooser(shareIntent, "Share Post"));
            bottomSheetDialog.dismiss();
        });

        reportOption.setOnClickListener(v -> {
            Toast.makeText(this, "Report submitted for post ID: " + post.getIssueId(), Toast.LENGTH_SHORT).show();
            // TODO: Implement report functionality (e.g., API call to report post)
            bottomSheetDialog.dismiss();
        });

        copyLinkOption.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Post Link", RetrofitClient.getBaseUrl() + "/post/" + post.getIssueId());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void fetchAndLoadProfileImage() {
        Glide.with(this).clear(binding.imgProfile);
        Glide.with(this).clear(binding.inputProfileImage);
        UserController userController = new UserController(this, null);
        userController.getCurrentUser(RetrofitClient.getAccessToken(this), new UserController.UserDataCallback() {
            @Override
            public void onSuccess(UserData userData) {
                if (userData != null) {
                    currentUserId = userData.getUserId();
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

                        String accessToken = RetrofitClient.getAccessToken(ItemPostDetailActivity.this);
                        GlideUrl glideUrl = accessToken != null && !accessToken.isEmpty()
                                ? new GlideUrl(imageUrl, new LazyHeaders.Builder()
                                .addHeader("Authorization", "Bearer " + accessToken)
                                .build())
                                : new GlideUrl(imageUrl);

                        RequestOptions requestOptions = new RequestOptions()
                                .placeholder(R.drawable.profile)
                                .error(R.drawable.profile)
                                .circleCrop();

                        Glide.with(ItemPostDetailActivity.this)
                                .load(glideUrl)
                                .apply(requestOptions)
                                .into(binding.imgProfile);

                        Glide.with(ItemPostDetailActivity.this)
                                .load(glideUrl)
                                .apply(requestOptions)
                                .into(binding.inputProfileImage);
                    } else {
                        Log.d(TAG, "No profile image found, using default");
                        binding.imgProfile.setImageResource(R.drawable.profile);
                        binding.inputProfileImage.setImageResource(R.drawable.profile);
                    }
                } else {
                    Log.e(TAG, "UserData is null");
                    binding.imgProfile.setImageResource(R.drawable.profile);
                    binding.inputProfileImage.setImageResource(R.drawable.profile);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Failed to fetch user: " + errorMessage);
                Toast.makeText(ItemPostDetailActivity.this, "Failed to load profile image", Toast.LENGTH_SHORT).show();
                binding.imgProfile.setImageResource(R.drawable.profile);
                binding.inputProfileImage.setImageResource(R.drawable.profile);
            }
        });
    }

    private void populatePostDetails() {
        binding.postTitle.setText(post.getTitle());
        binding.postDescription.setText(post.getDescription());
        binding.issueType.setText(post.getCategory());
        binding.postAuthor.setText(post.getUsername());
        binding.postTime.setText(getRelativeTime(post.getCreatedAt()));
        binding.supportCount.setText(String.valueOf(post.getSupportCount()));
        binding.affectedCount.setText(String.valueOf(post.getAffectedCount()));
        binding.notSureCount.setText(String.valueOf(post.getNotSureCount()));
        binding.invalidCount.setText(String.valueOf(post.getInvalidCount()));
        binding.fixedCount.setText(String.valueOf(post.getFixedCount()));

        // Load post author's profile image
        String profileImagePath = post.getProfileImage();
        Log.d(TAG, "Raw profileImagePath from API: " + profileImagePath);
        if (profileImagePath != null && !profileImagePath.isEmpty()) {
            String imageUrl;
            if (profileImagePath.startsWith("http://") || profileImagePath.startsWith("https://")) {
                imageUrl = profileImagePath;
                Log.d(TAG, "Using full URL for profile: " + imageUrl);
            } else {
                imageUrl = RetrofitClient.getBaseUrl() + (profileImagePath.startsWith("/storage/") ? "" : "/storage/") + profileImagePath;
                Log.d(TAG, "Constructed URL for profile: " + imageUrl);
            }

            String accessToken = RetrofitClient.getAccessToken(this);
            GlideUrl glideUrl = accessToken != null && !accessToken.isEmpty()
                    ? new GlideUrl(imageUrl, new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build())
                    : new GlideUrl(imageUrl);

            Glide.with(this)
                    .load(glideUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .circleCrop())
                    .into(binding.postAuthorProfile);
        } else {
            Glide.with(this).load(R.drawable.profile).into(binding.postAuthorProfile);
        }

        // Set click listeners for username and profile image
        binding.postAuthor.setOnClickListener(v -> {
            Intent intent = new Intent(ItemPostDetailActivity.this, ProfileActivity.class);
            intent.putExtra("user_id", post.getUserId());
            startActivity(intent);
        });
        binding.postAuthorProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ItemPostDetailActivity.this, ProfileActivity.class);
            intent.putExtra("user_id", post.getUserId());
            startActivity(intent);
        });

        // Load post images and control visibility
        String baseUrl = RetrofitClient.getBaseUrl();
        boolean hasImages = false;

        if (post.getImage1() != null && !post.getImage1().isEmpty()) {
            String imageUrl;
            if (post.getImage1().startsWith("http://") || post.getImage1().startsWith("https://")) {
                imageUrl = post.getImage1();
                Log.d(TAG, "Using full URL for image1: " + imageUrl);
            } else {
                imageUrl = baseUrl + (post.getImage1().startsWith("/storage/") ? "" : "/storage/") + post.getImage1();
                Log.d(TAG, "Constructed URL for image1: " + imageUrl);
            }

            String accessToken = RetrofitClient.getAccessToken(this);
            GlideUrl glideUrl = accessToken != null && !accessToken.isEmpty()
                    ? new GlideUrl(imageUrl, new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build())
                    : new GlideUrl(imageUrl);

            Glide.with(this)
                    .load(glideUrl)
                    .placeholder(R.drawable.sample1)
                    .error(R.drawable.sample1)
                    .into(binding.postImage1);
            hasImages = true;

            // Add click listener for full-screen view
            binding.postImage1.setOnClickListener(v -> {
                Intent intent = new Intent(ItemPostDetailActivity.this, FullscreenImageActivity.class);
                intent.putExtra("image_url", imageUrl);
                startActivity(intent);
            });
        } else {
            binding.postImage1.setVisibility(View.GONE);
        }

        if (post.getImage2() != null && !post.getImage2().isEmpty()) {
            String imageUrl;
            if (post.getImage2().startsWith("http://") || post.getImage2().startsWith("https://")) {
                imageUrl = post.getImage2();
                Log.d(TAG, "Using full URL for image2: " + imageUrl);
            } else {
                imageUrl = baseUrl + (post.getImage2().startsWith("/storage/") ? "" : "/storage/") + post.getImage2();
                Log.d(TAG, "Constructed URL for image2: " + imageUrl);
            }

            String accessToken = RetrofitClient.getAccessToken(this);
            GlideUrl glideUrl = accessToken != null && !accessToken.isEmpty()
                    ? new GlideUrl(imageUrl, new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build())
                    : new GlideUrl(imageUrl);

            Glide.with(this)
                    .load(glideUrl)
                    .placeholder(R.drawable.sample2)
                    .error(R.drawable.sample2)
                    .into(binding.postImage2);
            binding.postImage2.setVisibility(View.VISIBLE);
            hasImages = true;

            // Add click listener for full-screen view
            binding.postImage2.setOnClickListener(v -> {
                Intent intent = new Intent(ItemPostDetailActivity.this, FullscreenImageActivity.class);
                intent.putExtra("image_url", imageUrl);
                startActivity(intent);
            });
        } else {
            binding.postImage2.setVisibility(View.GONE);
        }

        binding.postImagesContainer.setVisibility(hasImages ? View.VISIBLE : View.GONE);
    }

    private String getRelativeTime(String createdAt) {
        if (createdAt == null || createdAt.isEmpty()) {
            Log.w(TAG, "createdAt is null or empty for post ID: " + post.getIssueId());
            return "unknown";
        }

        // Enhanced regex to handle seconds, minutes, hours, days, weeks, months, years, and "just now"
        if (createdAt.matches("\\d+\\s+(second|sec|minute|min|hour|hr|day|week|month|year)s?\\s+ago") ||
                createdAt.matches("just now")) {

            // Normalize the string for consistency
            if (createdAt.contains("second")) {
                createdAt = createdAt.replace("second", "sec");
            } else if (createdAt.contains("minute")) {
                createdAt = createdAt.replace("minute", "min");
            } else if (createdAt.contains("hour")) {
                createdAt = createdAt.replace("hour", "hour");
            } else if (createdAt.contains("week")) {
                createdAt = createdAt.replace("week", "week");
            } else if (createdAt.contains("month")) {
                createdAt = createdAt.replace("month", "month");
            } else if (createdAt.contains("year")) {
                createdAt = createdAt.replace("year", "year");
            }

            return createdAt; // Return normalized relative time
        }

        // Try parsing as absolute date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date postDate = sdf.parse(createdAt);
            if (postDate == null) {
                Log.w(TAG, "Failed to parse createdAt: " + createdAt);
                return "unknown";
            }

            long diffInMillis = System.currentTimeMillis() - postDate.getTime();
            long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (diffInSeconds < 60) {
                return diffInSeconds + " sec" + (diffInSeconds == 1 ? "" : "s") + " ago";
            } else if (diffInMinutes < 60) {
                return diffInMinutes + " min" + (diffInMinutes == 1 ? "" : "s") + " ago";
            } else if (diffInHours < 24) {
                return diffInHours + " hr" + (diffInHours == 1 ? "" : "s") + " ago";
            } else if (diffInDays < 7) {
                return diffInDays + " day" + (diffInDays == 1 ? "" : "s") + " ago";
            } else if (diffInDays < 30) {
                long weeks = diffInDays / 7;
                return weeks + " wk" + (weeks == 1 ? "" : "s") + " ago";
            } else if (diffInDays < 365) {
                long months = diffInDays / 30;
                return months + " mo" + (months == 1 ? "" : "s") + " ago";
            } else {
                long years = diffInDays / 365;
                return years + " yr" + (years == 1 ? "" : "s") + " ago";
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + createdAt + ", Error: " + e.getMessage());
            return "unknown";
        }
    }


    private void fetchComments() {
        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(this);
        Call<CommentsResponse> call = apiService.getComments(post.getIssueId());

        call.enqueue(new Callback<CommentsResponse>() {
            @Override
            public void onResponse(Call<CommentsResponse> call, Response<CommentsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CommentsResponse commentsResponse = response.body();
                    if ("success".equals(commentsResponse.getStatus())) {
                        commentAdapter.updateComments(commentsResponse.getComments());
                    } else {
                        Log.e(TAG, "Failed to fetch comments: Status = " + commentsResponse.getStatus());
                        Toast.makeText(ItemPostDetailActivity.this, "Failed to fetch comments", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed to fetch comments: " + response.code() + " - " + response.message());
                    Toast.makeText(ItemPostDetailActivity.this, "Failed to fetch comments: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentsResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching comments: " + t.getMessage(), t);
                Toast.makeText(ItemPostDetailActivity.this, "Network error fetching comments: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitComment() {
        String commentText = binding.commentInput.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        CommentRequest commentRequest = new CommentRequest(commentText, selectedImageBase64);
        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(this);
        Call<CommentResponse> call = apiService.addComment(post.getIssueId(), commentRequest);

        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CommentResponse commentResponse = response.body();
                    if ("success".equals(commentResponse.getStatus())) {
                        binding.commentInput.setText("");
                        binding.commentImagePreview.setVisibility(View.GONE);
                        selectedImageBase64 = null;
                        commentAdapter.addComment(commentResponse.getComment());
                        Toast.makeText(ItemPostDetailActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed to add comment: Status = " + commentResponse.getStatus());
                        Toast.makeText(ItemPostDetailActivity.this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed to add comment: " + response.code() + " - " + response.message());
                    Toast.makeText(ItemPostDetailActivity.this, "Failed to add comment: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                Log.e(TAG, "Error adding comment: " + t.getMessage(), t);
                Toast.makeText(ItemPostDetailActivity.this, "Network error adding comment: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupReactionListeners() {
        View.OnClickListener reactionListener = v -> {
            String reactionType = "";
            if (v.getId() == R.id.supportReaction) {
                reactionType = "support";
            } else if (v.getId() == R.id.affectedReaction) {
                reactionType = "affected";
            } else if (v.getId() == R.id.notSureReaction) {
                reactionType = "not_sure";
            } else if (v.getId() == R.id.invalidReaction) {
                reactionType = "invalid";
            } else if (v.getId() == R.id.fixedReaction) {
                reactionType = "fixed";
            }
            addReaction(post.getIssueId(), reactionType);
        };

        binding.supportReaction.setOnClickListener(reactionListener);
        binding.affectedReaction.setOnClickListener(reactionListener);
        binding.notSureReaction.setOnClickListener(reactionListener);
        binding.invalidReaction.setOnClickListener(reactionListener);
        binding.fixedReaction.setOnClickListener(reactionListener);
    }

    private void addReaction(int issueId, String reactionType) {
        if (userReactions.size() >= 2 && !userReactions.containsKey(reactionType)) {
            Toast.makeText(this, "You can only add up to 2 reactions per post", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(this);
        ReactionRequest reactionRequest = new ReactionRequest(reactionType);
        Call<ReactionResponse> call = apiService.addReaction(issueId, reactionRequest);

        call.enqueue(new Callback<ReactionResponse>() {
            @Override
            public void onResponse(Call<ReactionResponse> call, Response<ReactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReactionResponse reactionResponse = response.body();
                    Log.d(TAG, "API Response: " + response.body().toString());
                    if ("success".equals(reactionResponse.getStatus())) {
                        ReactionResponse.ReactionCount supportCount = reactionResponse.getReactionCounts().get("support");
                        ReactionResponse.ReactionCount affectedCount = reactionResponse.getReactionCounts().get("affected");
                        ReactionResponse.ReactionCount notSureCount = reactionResponse.getReactionCounts().get("not_sure");
                        ReactionResponse.ReactionCount invalidCount = reactionResponse.getReactionCounts().get("invalid");
                        ReactionResponse.ReactionCount fixedCount = reactionResponse.getReactionCounts().get("fixed");

                        if (supportCount != null) binding.supportCount.setText(String.valueOf(supportCount.getCount()));
                        if (affectedCount != null) binding.affectedCount.setText(String.valueOf(affectedCount.getCount()));
                        if (notSureCount != null) binding.notSureCount.setText(String.valueOf(notSureCount.getCount()));
                        if (invalidCount != null) binding.invalidCount.setText(String.valueOf(invalidCount.getCount()));
                        if (fixedCount != null) binding.fixedCount.setText(String.valueOf(fixedCount.getCount()));

                        if (userReactions.containsKey(reactionType)) {
                            userReactions.remove(reactionType);
                        } else {
                            userReactions.put(reactionType, true);
                        }

                        Toast.makeText(ItemPostDetailActivity.this, "Reaction " + (userReactions.containsKey(reactionType) ? "added" : "removed"), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed reaction response: Status = " + reactionResponse.getStatus());
                        Toast.makeText(ItemPostDetailActivity.this, "Failed to add reaction", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Unsuccessful response: " + response.code() + " - " + response.message());
                    Toast.makeText(ItemPostDetailActivity.this, "Failed to add reaction: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReactionResponse> call, Throwable t) {
                Log.e(TAG, "Error adding reaction: " + t.getMessage());
                Toast.makeText(ItemPostDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String convertImageToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "Error converting image to Base64: " + e.getMessage());
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}