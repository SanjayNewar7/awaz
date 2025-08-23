package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private ImageView supportIcon, affectedIcon, notSureIcon, invalidIcon, fixedIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_post);

        // Get post from intent
        post = (Post) getIntent().getSerializableExtra("post");
        if (post == null) {
            Toast.makeText(this, "Error: Post data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Post issueId: " + post.getIssueId());
        Log.d(TAG, "Post commentCount: " + post.getCommentCount());

        // Initialize UI elements
        TextView postTitle = findViewById(R.id.postTitle);
        TextView postDescription = findViewById(R.id.postDescription);
        TextView postSeeMore = findViewById(R.id.postSeeMore);
        TextView supportCount = findViewById(R.id.supportCount);
        TextView affectedCount = findViewById(R.id.affectedCount);
        TextView notSureCount = findViewById(R.id.notSureCount);
        TextView invalidCount = findViewById(R.id.invalidCount);
        TextView fixedCount = findViewById(R.id.fixedCount);
        TextView commentCount = findViewById(R.id.commentCount);
        supportIcon = findViewById(R.id.supportIcon);
        affectedIcon = findViewById(R.id.affectedIcon);
        notSureIcon = findViewById(R.id.notSureIcon);
        invalidIcon = findViewById(R.id.invalidIcon);
        fixedIcon = findViewById(R.id.fixedIcon);

        // Set initial counts
        supportCount.setText(String.valueOf(post.getSupportCount()));
        affectedCount.setText(String.valueOf(post.getAffectedCount()));
        notSureCount.setText(String.valueOf(post.getNotSureCount()));
        invalidCount.setText(String.valueOf(post.getInvalidCount()));
        fixedCount.setText(String.valueOf(post.getFixedCount()));
        commentCount.setText(String.valueOf(post.getCommentCount()) + " comments");

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
                            // Optional: Update icon to gray (unselected)
                            /* switch (reactionType) {
                                case "support":
                                    supportIcon.setImageResource(R.drawable.support_gray);
                                    break;
                                case "affected":
                                    affectedIcon.setImageResource(R.drawable.affected_gray);
                                    break;
                                case "not_sure":
                                    notSureIcon.setImageResource(R.drawable.not_sure_gray);
                                    break;
                                case "invalid":
                                    invalidIcon.setImageResource(R.drawable.invalid_gray);
                                    break;
                                case "fixed":
                                    fixedIcon.setImageResource(R.drawable.fixed_gray);
                                    break;
                            } */
                        } else {
                            userReactions.put(reactionType, true); // Add new reaction
                            // Optional: Update icon to colored (selected)
                            /* switch (reactionType) {
                                case "support":
                                    supportIcon.setImageResource(R.drawable.support_colored);
                                    break;
                                case "affected":
                                    affectedIcon.setImageResource(R.drawable.affected_colored);
                                    break;
                                case "not_sure":
                                    notSureIcon.setImageResource(R.drawable.not_sure_colored);
                                    break;
                                case "invalid":
                                    invalidIcon.setImageResource(R.drawable.invalid_colored);
                                    break;
                                case "fixed":
                                    fixedIcon.setImageResource(R.drawable.fixed_colored);
                                    break;
                            } */
                        }

                        Toast.makeText(ItemPostActivity.this, "Reaction " + (userReactions.containsKey(reactionType) ? "added" : "removed"), Toast.LENGTH_SHORT).show();
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