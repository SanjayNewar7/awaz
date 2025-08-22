package com.example.awaz.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;
import com.example.awaz.model.Post;
import com.example.awaz.model.ReactionRequest;
import com.example.awaz.model.ReactionResponse;
import com.example.awaz.service.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemPostActivity extends AppCompatActivity {

    private static final String TAG = "ItemPostActivity";
    private Post post;
    private Map<String, Boolean> userReactions = new HashMap<>(); // Track user's reactions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_post);

        // Get post from intent (assuming it's passed, similar to ItemPostDetailActivity)
        post = (Post) getIntent().getSerializableExtra("post");
        if (post == null) {
            Toast.makeText(this, "Error: Post data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI elements
        TextView postTitle = findViewById(R.id.postTitle);
        TextView postDescription = findViewById(R.id.postDescription);
        TextView postSeeMore = findViewById(R.id.postSeeMore);
        TextView supportCount = findViewById(R.id.supportCount);
        TextView affectedCount = findViewById(R.id.affectedCount);
        TextView notSureCount = findViewById(R.id.notSureCount);
        TextView invalidCount = findViewById(R.id.invalidCount);
        TextView fixedCount = findViewById(R.id.fixedCount);

        // Set initial counts
        supportCount.setText(String.valueOf(post.getSupportCount()));
        affectedCount.setText(String.valueOf(post.getAffectedCount()));
        notSureCount.setText(String.valueOf(post.getNotSureCount()));
        invalidCount.setText(String.valueOf(post.getInvalidCount()));
        fixedCount.setText(String.valueOf(post.getFixedCount()));

        // Set click listeners for navigation
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

        // Set up reaction listeners
        setupReactionListeners();
    }

    private void setupReactionListeners() {
        View.OnClickListener reactionListener = v -> {
            String reactionType = "";
            if (v.getId() == R.id.supportCount) {
                reactionType = "support";
            } else if (v.getId() == R.id.affectedCount) {
                reactionType = "affected";
            } else if (v.getId() == R.id.notSureCount) {
                reactionType = "not_sure";
            } else if (v.getId() == R.id.invalidCount) {
                reactionType = "invalid";
            } else if (v.getId() == R.id.fixedCount) {
                reactionType = "fixed";
            }
            addReaction(post.getId(), reactionType);
        };

        findViewById(R.id.supportCount).setOnClickListener(reactionListener);
        findViewById(R.id.affectedCount).setOnClickListener(reactionListener);
        findViewById(R.id.notSureCount).setOnClickListener(reactionListener);
        findViewById(R.id.invalidCount).setOnClickListener(reactionListener);
        findViewById(R.id.fixedCount).setOnClickListener(reactionListener);
    }

    private void addReaction(int postId, String reactionType) {
        if (userReactions.size() >= 2 && !userReactions.containsKey(reactionType)) {
            Toast.makeText(this, "You can only add up to 2 reactions per post", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Sending reaction for issue_id: " + post.getIssueId()); // Add logging
        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(this);
        ReactionRequest reactionRequest = new ReactionRequest(reactionType);
        Call<ReactionResponse> call = apiService.addReaction(post.getIssueId(), reactionRequest);

        call.enqueue(new Callback<ReactionResponse>() {
            @Override
            public void onResponse(Call<ReactionResponse> call, Response<ReactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReactionResponse reactionResponse = response.body();
                    if ("success".equals(reactionResponse.getStatus())) {
                        // Update reaction counts
                        TextView supportCount = findViewById(R.id.supportCount);
                        TextView affectedCount = findViewById(R.id.affectedCount);
                        TextView notSureCount = findViewById(R.id.notSureCount);
                        TextView invalidCount = findViewById(R.id.invalidCount);
                        TextView fixedCount = findViewById(R.id.fixedCount);

                        ReactionResponse.ReactionCount supportCountData = reactionResponse.getReactionCounts().get("support");
                        ReactionResponse.ReactionCount affectedCountData = reactionResponse.getReactionCounts().get("affected");
                        ReactionResponse.ReactionCount notSureCountData = reactionResponse.getReactionCounts().get("not_sure");
                        ReactionResponse.ReactionCount invalidCountData = reactionResponse.getReactionCounts().get("invalid");
                        ReactionResponse.ReactionCount fixedCountData = reactionResponse.getReactionCounts().get("fixed");

                        if (supportCountData != null) supportCount.setText(String.valueOf(supportCountData.getCount()));
                        if (affectedCountData != null) affectedCount.setText(String.valueOf(affectedCountData.getCount()));
                        if (notSureCountData != null) notSureCount.setText(String.valueOf(notSureCountData.getCount()));
                        if (invalidCountData != null) invalidCount.setText(String.valueOf(invalidCountData.getCount()));
                        if (fixedCountData != null) fixedCount.setText(String.valueOf(fixedCountData.getCount()));

                        // Update local reaction state
                        if (userReactions.containsKey(reactionType)) {
                            userReactions.remove(reactionType); // Undo reaction
                        } else {
                            userReactions.put(reactionType, true); // Add new reaction
                        }

                        Toast.makeText(ItemPostActivity.this, "Reaction " + (userReactions.containsKey(reactionType) ? "added" : "removed"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ItemPostActivity.this, "Failed to add reaction: " + reactionResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
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