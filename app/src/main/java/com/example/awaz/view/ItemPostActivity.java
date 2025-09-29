package com.example.awaz.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.awaz.R;
import com.example.awaz.model.Post;
import com.example.awaz.model.ReactionRequest;
import com.example.awaz.model.ReactionResponse;
import com.example.awaz.service.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemPostActivity extends AppCompatActivity {

    private static final String TAG = "ItemPostActivity";
    private Post post;
    private Map<String, Boolean> userReactions = new HashMap<>();
    private ImageView supportIcon, affectedIcon, notSureIcon, invalidIcon, fixedIcon;
    private ImageView postImage1, postImage2;
    private View postImagesContainer;
    private com.google.android.material.imageview.ShapeableImageView threeDotIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_post);

        post = (Post) getIntent().getSerializableExtra("post");
        if (post == null) {
            Toast.makeText(this, "Error: Post data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Post issueId: " + post.getIssueId());
        Log.d(TAG, "Post commentCount: " + post.getCommentCount());

        TextView postTitle = findViewById(R.id.postTitle);
        TextView postDescription = findViewById(R.id.postDescription);
        TextView postSeeMore = findViewById(R.id.postSeeMore);
        TextView postAuthor = findViewById(R.id.postAuthor);
        TextView postCategory = findViewById(R.id.postCategory);
        TextView postTime = findViewById(R.id.postTime);
        TextView supportCount = findViewById(R.id.supportCount);
        TextView affectedCount = findViewById(R.id.affectedCount);
        TextView notSureCount = findViewById(R.id.notSureCount);
        TextView invalidCount = findViewById(R.id.invalidCount);
        TextView fixedCount = findViewById(R.id.fixedCount);
        TextView commentCount = findViewById(R.id.commentCount);
        ImageView postAuthorProfile = findViewById(R.id.postAuthorProfile);
        supportIcon = findViewById(R.id.supportIcon);
        affectedIcon = findViewById(R.id.affectedIcon);
        notSureIcon = findViewById(R.id.notSureIcon);
        invalidIcon = findViewById(R.id.invalidIcon);
        fixedIcon = findViewById(R.id.fixedIcon);
        postImage1 = findViewById(R.id.postImage1);
        postImage2 = findViewById(R.id.postImage2);
        postImagesContainer = findViewById(R.id.postImagesContainer);
        threeDotIcon = findViewById(R.id.threeDotIcon);

        postTitle.setText(post.getTitle());
        postDescription.setText(post.getDescription());
        postAuthor.setText(post.getUsername());
        postCategory.setText(post.getCategory());
        postTime.setText(post.getCreatedAt());
        supportCount.setText(String.valueOf(post.getSupportCount()));
        affectedCount.setText(String.valueOf(post.getAffectedCount()));
        notSureCount.setText(String.valueOf(post.getNotSureCount()));
        invalidCount.setText(String.valueOf(post.getInvalidCount()));
        fixedCount.setText(String.valueOf(post.getFixedCount()));
        commentCount.setText(String.valueOf(post.getCommentCount()) + " comments");

        // Set click listener for threeDotIcon
        threeDotIcon.setOnClickListener(v -> showBottomSheetMenu());

        // Load profile image
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
                    .into(postAuthorProfile);
        } else {
            Glide.with(this).load(R.drawable.profile).into(postAuthorProfile);
        }

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
                    .into(postImage1);
            hasImages = true;
        } else {
            postImage1.setVisibility(View.GONE);
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
                    .into(postImage2);
            postImage2.setVisibility(View.VISIBLE);
            hasImages = true;
        } else {
            postImage2.setVisibility(View.GONE);
        }

        postImagesContainer.setVisibility(hasImages ? View.VISIBLE : View.GONE);

        postTitle.setOnClickListener(v -> {
            Intent intent = new Intent(ItemPostActivity.this, ItemPostDetailActivity.class);
            intent.putExtra("post", post);
            startActivity(intent);
        });

        postDescription.setOnClickListener(v -> {
            Intent intent = new Intent(ItemPostActivity.this, ItemPostDetailActivity.class);
            intent.putExtra("post", post);
            startActivity(intent);
        });

        postSeeMore.setOnClickListener(v -> {
            Intent intent = new Intent(ItemPostActivity.this, ItemPostDetailActivity.class);
            intent.putExtra("post", post);
            startActivity(intent);
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
            Log.d(TAG, "Reaction clicked: " + reactionType);
            addReaction(post.getIssueId(), reactionType);
        };

        findViewById(R.id.supportReaction).setOnClickListener(reactionListener);
        findViewById(R.id.affectedReaction).setOnClickListener(reactionListener);
        findViewById(R.id.notSureReaction).setOnClickListener(reactionListener);
        findViewById(R.id.invalidReaction).setOnClickListener(reactionListener);
        findViewById(R.id.fixedReaction).setOnClickListener(reactionListener);
    }

    private void addReaction(int issueId, String reactionType) {
        Log.d(TAG, "Attempting to add reaction: type=" + reactionType + ", issueId=" + issueId);

        // ✅ Reaction limit check
        if (userReactions.size() >= 2 && !userReactions.containsKey(reactionType)) {
            Log.d(TAG, "Reaction limit reached: " + userReactions.size());
            Toast.makeText(this, "You can only add up to 2 reactions per post", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(this);
        ReactionRequest reactionRequest = new ReactionRequest(reactionType);
        Call<ReactionResponse> call = apiService.addReaction(issueId, reactionRequest);
        Log.d(TAG, "API call initiated for endpoint: /issues/" + issueId + "/react");

        call.enqueue(new Callback<ReactionResponse>() {
            @Override
            public void onResponse(Call<ReactionResponse> call, Response<ReactionResponse> response) {
                Log.d(TAG, "API response received: code=" + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ReactionResponse reactionResponse = response.body();
                    Log.d(TAG, "API Response: " + reactionResponse.toString());

                    if ("success".equals(reactionResponse.getStatus())) {
                        // ✅ Map UI TextViews and Icons
                        Map<String, TextView> reactionViews = new HashMap<>();
                        reactionViews.put("support", findViewById(R.id.supportCount));
                        reactionViews.put("affected", findViewById(R.id.affectedCount));
                        reactionViews.put("not_sure", findViewById(R.id.notSureCount));
                        reactionViews.put("invalid", findViewById(R.id.invalidCount));
                        reactionViews.put("fixed", findViewById(R.id.fixedCount));

                        Map<String, ImageView> reactionIcons = new HashMap<>();
                        reactionIcons.put("support", findViewById(R.id.supportIcon));
                        reactionIcons.put("affected", findViewById(R.id.affectedIcon));
                        reactionIcons.put("not_sure", findViewById(R.id.notSureIcon));
                        reactionIcons.put("invalid", findViewById(R.id.invalidIcon));
                        reactionIcons.put("fixed", findViewById(R.id.fixedIcon));

                        // ✅ Capture old count for clicked reaction BEFORE updating UI
                        TextView clickedView = reactionViews.get(reactionType);
                        int oldCount = 0;
                        if (clickedView != null) {
                            try {
                                oldCount = Integer.parseInt(clickedView.getText().toString());
                            } catch (NumberFormatException e) {
                                Log.w(TAG, "Invalid old count format, defaulting to 0");
                            }
                        }

                        // ✅ Update all counts
                        Map<String, ReactionResponse.ReactionCount> reactionData = reactionResponse.getReactionCounts();
                        for (Map.Entry<String, TextView> entry : reactionViews.entrySet()) {
                            String type = entry.getKey();
                            TextView view = entry.getValue();
                            ReactionResponse.ReactionCount countData = reactionData.get(type);

                            if (countData != null) {
                                view.setText(String.valueOf(countData.getCount()));
                            }

                            // ✅ Update icon based on userReactions
                            ImageView icon = reactionIcons.get(type);
                            if (icon != null) {
                                if (userReactions.containsKey(type)) {
                                    switch (type) {
                                        case "support":
                                            icon.setImageResource(R.drawable.support_given);
                                            break;
                                        case "affected":
                                            icon.setImageResource(R.drawable.affected_given);
                                            break;
                                        case "not_sure":
                                            icon.setImageResource(R.drawable.notsure_given);
                                            break;
                                        case "invalid":
                                            icon.setImageResource(R.drawable.invalid_given);
                                            break;
                                        case "fixed":
                                            icon.setImageResource(R.drawable.fixed_given);
                                            break;
                                    }
                                } else {
                                    switch (type) {
                                        case "support":
                                            icon.setImageResource(R.drawable.support_gray);
                                            break;
                                        case "affected":
                                            icon.setImageResource(R.drawable.affected_gray);
                                            break;
                                        case "not_sure":
                                            icon.setImageResource(R.drawable.not_sure_gray);
                                            break;
                                        case "invalid":
                                            icon.setImageResource(R.drawable.invalid_gray);
                                            break;
                                        case "fixed":
                                            icon.setImageResource(R.drawable.fixed_gray);
                                            break;
                                    }
                                }
                            }
                        }

                        // ✅ Compare new count for clicked reaction
                        if (clickedView != null) {
                            int newCount = reactionData.get(reactionType) != null
                                    ? reactionData.get(reactionType).getCount()
                                    : oldCount;

                            if (newCount > oldCount) {
                                Toast.makeText(ItemPostActivity.this, "Reaction added", Toast.LENGTH_SHORT).show();
                                userReactions.put(reactionType, true);
                            } else if (newCount < oldCount) {
                                Toast.makeText(ItemPostActivity.this, "Reaction removed", Toast.LENGTH_SHORT).show();
                                userReactions.remove(reactionType);
                            }
                        }

                    } else {
                        Log.e(TAG, "Failed reaction response: Status = " + reactionResponse.getStatus());
                        Toast.makeText(ItemPostActivity.this, "Failed to add reaction", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Unsuccessful response: " + response.code() + " - " + response.message());
                    Toast.makeText(ItemPostActivity.this, "Failed to add reaction: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReactionResponse> call, Throwable t) {
                Log.e(TAG, "Error adding reaction: " + t.getMessage());
                Toast.makeText(ItemPostActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}