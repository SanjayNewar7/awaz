package com.example.awaz.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
import com.example.awaz.databinding.ItemPostDetailActivityBinding;
import com.example.awaz.model.CommentRequest;
import com.example.awaz.model.CommentResponse;
import com.example.awaz.model.CommentsResponse;
import com.example.awaz.model.Post;
import com.example.awaz.model.ReactionRequest;
import com.example.awaz.model.ReactionResponse;
import com.example.awaz.service.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ItemPostDetailActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get post from intent
        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra("post");
        if (post == null) {
            Toast.makeText(this, "Error: Post data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize RecyclerView for comments
        commentAdapter = new CommentAdapter(this, new java.util.ArrayList<>());
        binding.recyclerComments.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerComments.setAdapter(commentAdapter);

        // Initialize image picker
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageBase64 = convertImageToBase64(uri);
                        binding.commentImagePreview.setVisibility(View.VISIBLE);
                        Glide.with(this).load(uri).into(binding.commentImagePreview);
                    }
                });

        // Populate post details
        populatePostDetails();

        // Fetch comments
        fetchComments();

        // Set up comment submission
        binding.sendButton.setOnClickListener(v -> submitComment());

        // Set up image picker button
        binding.galleryButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // Set up back button
        binding.backArrow.setOnClickListener(v -> finish());

        // Set up reaction buttons
        binding.supportReaction.setOnClickListener(v -> addReaction(post.getId(), "support"));
        binding.affectedReaction.setOnClickListener(v -> addReaction(post.getId(), "affected"));
        binding.notSureReaction.setOnClickListener(v -> addReaction(post.getId(), "not_sure"));
        binding.invalidReaction.setOnClickListener(v -> addReaction(post.getId(), "invalid"));
        binding.fixedReaction.setOnClickListener(v -> addReaction(post.getId(), "fixed"));
    }

    private void populatePostDetails() {
        binding.postTitle.setText(post.getTitle());
        binding.postDescription.setText(post.getDescription());
        binding.issueType.setText(post.getCategory());
        binding.postAuthor.setText(post.getUsername());
        binding.postTime.setText(post.getCreatedAt());
        binding.supportCount.setText(String.valueOf(post.getSupportCount()));
        binding.affectedCount.setText(String.valueOf(post.getAffectedCount()));
        binding.notSureCount.setText(String.valueOf(post.getNotSureCount()));
        binding.invalidCount.setText(String.valueOf(post.getInvalidCount()));
        binding.fixedCount.setText(String.valueOf(post.getFixedCount()));

        // Load profile image
        String profileImagePath = post.getProfileImage();
        Log.d(TAG, "Raw profileImagePath from API: " + profileImagePath);
        if (profileImagePath != null && !profileImagePath.isEmpty()) {
            String imageUrl;
            if (profileImagePath.startsWith("http://") || profileImagePath.startsWith("https://")) {
                imageUrl = profileImagePath; // Use full URL as-is
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

        // Set click listener for username
        binding.postAuthor.setOnClickListener(v -> {
            Intent intent = new Intent(ItemPostDetailActivity.this, ProfileActivity.class);
            intent.putExtra("user_id", post.getUserId()); // Pass the user_id
            startActivity(intent);
        });

        // Set click listener for profile image
        binding.postAuthorProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ItemPostDetailActivity.this, ProfileActivity.class);
            intent.putExtra("user_id", post.getUserId()); // Pass the user_id
            startActivity(intent);
        });

        // Load post images if available
        String baseUrl = RetrofitClient.getBaseUrl() + "storage/";
        if (post.getImage1() != null && !post.getImage1().isEmpty()) {
            Glide.with(this).load(baseUrl + post.getImage1())
                    .placeholder(R.drawable.sample1)
                    .error(R.drawable.sample1)
                    .into(binding.postImage1);
        }
        if (post.getImage2() != null && !post.getImage2().isEmpty()) {
            binding.postImage2.setVisibility(View.VISIBLE);
            Glide.with(this).load(baseUrl + post.getImage2())
                    .placeholder(R.drawable.sample2)
                    .error(R.drawable.sample2)
                    .into(binding.postImage2);
        } else {
            binding.postImage2.setVisibility(View.GONE);
        }
    }

    private void fetchComments() {
        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(this);
        Call<CommentsResponse> call = apiService.getComments(post.getId());

        call.enqueue(new Callback<CommentsResponse>() {
            @Override
            public void onResponse(Call<CommentsResponse> call, Response<CommentsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CommentsResponse commentsResponse = response.body();
                    if ("success".equals(commentsResponse.getStatus())) {
                        commentAdapter.updateComments(commentsResponse.getComments());
                    } else {
                        Toast.makeText(ItemPostDetailActivity.this, "Failed to fetch comments", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ItemPostDetailActivity.this, "Failed to fetch comments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentsResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching comments: " + t.getMessage());
                Toast.makeText(ItemPostDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
        Call<CommentResponse> call = apiService.addComment(post.getId(), commentRequest);

        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CommentResponse commentResponse = response.body();
                    if ("success".equals(commentResponse.getStatus())) {
                        // Clear input fields
                        binding.commentInput.setText("");
                        binding.commentImagePreview.setVisibility(View.GONE);
                        selectedImageBase64 = null;

                        // Add new comment to adapter
                        commentAdapter.addComment(commentResponse.getComment());
                        Toast.makeText(ItemPostDetailActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ItemPostDetailActivity.this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ItemPostDetailActivity.this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                Log.e(TAG, "Error adding comment: " + t.getMessage());
                Toast.makeText(ItemPostDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addReaction(int postId, String reactionType) {
        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(this);
        ReactionRequest reactionRequest = new ReactionRequest(reactionType);
        Call<ReactionResponse> call = apiService.addReaction(postId, reactionRequest);

        call.enqueue(new Callback<ReactionResponse>() {
            @Override
            public void onResponse(Call<ReactionResponse> call, Response<ReactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReactionResponse reactionResponse = response.body();
                    if ("success".equals(reactionResponse.getStatus())) {
                        // Update reaction counts
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

                        Toast.makeText(ItemPostDetailActivity.this, "Reaction added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ItemPostDetailActivity.this, "Failed to add reaction", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ItemPostDetailActivity.this, "Failed to add reaction", Toast.LENGTH_SHORT).show();
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