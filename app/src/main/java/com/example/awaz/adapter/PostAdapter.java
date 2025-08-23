package com.example.awaz.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.example.awaz.R;
import com.example.awaz.model.Post;
import com.example.awaz.model.ReactionRequest;
import com.example.awaz.model.ReactionResponse;
import com.example.awaz.service.RetrofitClient;
import com.example.awaz.view.ItemPostDetailActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private static final String TAG = "PostAdapter";
    private final Context context;
    private final List<Post> postList;
    private final Map<Integer, Map<String, Boolean>> userReactionsMap = new HashMap<>(); // Track user's reactions per post

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        Log.d(TAG, "Inflated item_post layout");
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        if (post == null) {
            Log.e(TAG, "Post at position " + position + " is null");
            return;
        }

        // Initialize user reactions map for this post if not exists
        // Use issueId instead of id for consistency with ItemPostDetailActivity
        if (!userReactionsMap.containsKey(post.getIssueId())) {
            userReactionsMap.put(post.getIssueId(), new HashMap<>());
        }

        // Get the userReactions for this specific post and make it final for inner class access
        final Map<String, Boolean> userReactions = userReactionsMap.get(post.getIssueId());

        holder.postAuthor.setText(post.getUsername());
        holder.postCategory.setText(post.getCategory());
        holder.postTime.setText(post.getCreatedAt());
        holder.postTitle.setText(post.getTitle());
        holder.postDescription.setText(post.getDescription());
        holder.supportCount.setText(String.valueOf(post.getSupportCount()));
        holder.affectedCount.setText(String.valueOf(post.getAffectedCount()));
        holder.notSureCount.setText(String.valueOf(post.getNotSureCount()));
        holder.invalidCount.setText(String.valueOf(post.getInvalidCount()));
        holder.fixedCount.setText(String.valueOf(post.getFixedCount()));
        holder.commentCount.setText(String.valueOf(post.getCommentCount()) + " comments");

        Log.d(TAG, "Bound data for position " + position + ": Title=" + post.getTitle() + ", ID=" + post.getId() + ", IssueID=" + post.getIssueId());

        holder.postTitle.setOnClickListener(v -> openPostDetail(post));
        holder.postDescription.setOnClickListener(v -> openPostDetail(post));
        holder.commentCount.setOnClickListener(v -> openPostDetail(post));

        // Set up reaction listeners with the final userReactions variable
        setupReactionListeners(holder, post, userReactions);

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

            String accessToken = RetrofitClient.getAccessToken(context);
            GlideUrl glideUrl = accessToken != null && !accessToken.isEmpty()
                    ? new GlideUrl(imageUrl, new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build())
                    : new GlideUrl(imageUrl);

            Glide.with(context)
                    .load(glideUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .circleCrop())
                    .into(holder.postAuthorProfile);
        } else {
            Glide.with(context).load(R.drawable.profile)
                    .into(holder.postAuthorProfile);
        }
    }

    private void setupReactionListeners(PostViewHolder holder, Post post, final Map<String, Boolean> userReactions) {
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
            Log.d(TAG, "Reaction clicked: " + reactionType + " for issueId: " + post.getIssueId());
            addReaction(post.getIssueId(), reactionType, holder, userReactions);
        };

        holder.supportReaction.setOnClickListener(reactionListener);
        holder.affectedReaction.setOnClickListener(reactionListener);
        holder.notSureReaction.setOnClickListener(reactionListener);
        holder.invalidReaction.setOnClickListener(reactionListener);
        holder.fixedReaction.setOnClickListener(reactionListener);
    }

    private void addReaction(int issueId, String reactionType, PostViewHolder holder, final Map<String, Boolean> userReactions) {
        Log.d(TAG, "Attempting to add reaction: type=" + reactionType + ", issueId=" + issueId);

        if (userReactions.size() >= 2 && !userReactions.containsKey(reactionType)) {
            Log.d(TAG, "Reaction limit reached: " + userReactions.size());
            Toast.makeText(context, "Max 2 reactions per post", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(context);
        ReactionRequest reactionRequest = new ReactionRequest(reactionType);
        Call<ReactionResponse> call = apiService.addReaction(issueId, reactionRequest);

        call.enqueue(new Callback<ReactionResponse>() {
            @Override
            public void onResponse(Call<ReactionResponse> call, Response<ReactionResponse> response) {
                Log.d(TAG, "API response received: code=" + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ReactionResponse reactionResponse = response.body();
                    Log.d(TAG, "API Response: " + reactionResponse.toString());

                    if ("success".equals(reactionResponse.getStatus())) {
                        // Update reaction counts
                        ReactionResponse.ReactionCount supportCountData = reactionResponse.getReactionCounts().get("support");
                        ReactionResponse.ReactionCount affectedCountData = reactionResponse.getReactionCounts().get("affected");
                        ReactionResponse.ReactionCount notSureCountData = reactionResponse.getReactionCounts().get("not_sure");
                        ReactionResponse.ReactionCount invalidCountData = reactionResponse.getReactionCounts().get("invalid");
                        ReactionResponse.ReactionCount fixedCountData = reactionResponse.getReactionCounts().get("fixed");

                        if (supportCountData != null) holder.supportCount.setText(String.valueOf(supportCountData.getCount()));
                        if (affectedCountData != null) holder.affectedCount.setText(String.valueOf(affectedCountData.getCount()));
                        if (notSureCountData != null) holder.notSureCount.setText(String.valueOf(notSureCountData.getCount()));
                        if (invalidCountData != null) holder.invalidCount.setText(String.valueOf(invalidCountData.getCount()));
                        if (fixedCountData != null) holder.fixedCount.setText(String.valueOf(fixedCountData.getCount()));

                        // Update local reaction state
                        if (userReactions.containsKey(reactionType)) {
                            userReactions.remove(reactionType); // Undo reaction
                        } else {
                            userReactions.put(reactionType, true); // Add new reaction
                        }

                        Toast.makeText(context, "Reaction " + (userReactions.containsKey(reactionType) ? "added" : "removed"), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed reaction response: Status = " + reactionResponse.getStatus());
                        Toast.makeText(context, "Failed to add reaction", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle specific error cases
                    if (response.code() == 400) {
                        // Server is rejecting due to validation (likely reaction limit)
                        Toast.makeText(context, "Max 2 reactions per post", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Unsuccessful response: " + response.code() + " - " + response.message());
                        Toast.makeText(context, "Failed to add reaction", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ReactionResponse> call, Throwable t) {
                Log.e(TAG, "Error adding reaction: " + t.getMessage());
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = postList != null ? postList.size() : 0;
        Log.d(TAG, "Item count: " + count);
        return count;
    }

    private void openPostDetail(Post post) {
        Intent intent = new Intent(context, ItemPostDetailActivity.class);
        intent.putExtra("post", post);
        context.startActivity(intent);
    }

    // Update comment count for a specific post
    public void updateCommentCount(int issueId, int newCommentCount) {
        for (int i = 0; i < postList.size(); i++) {
            Post post = postList.get(i);
            if (post.getIssueId() == issueId) {
                post.setCommentCount(newCommentCount);
                notifyItemChanged(i);
                break;
            }
        }
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postAuthor, postCategory, postTime, postTitle, postDescription;
        TextView supportCount, affectedCount, notSureCount, invalidCount, fixedCount, commentCount;
        ImageView postAuthorProfile;
        View supportReaction, affectedReaction, notSureReaction, invalidReaction, fixedReaction;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postAuthor = itemView.findViewById(R.id.postAuthor);
            postCategory = itemView.findViewById(R.id.postCategory);
            postTime = itemView.findViewById(R.id.postTime);
            postTitle = itemView.findViewById(R.id.postTitle);
            postDescription = itemView.findViewById(R.id.postDescription);
            supportCount = itemView.findViewById(R.id.supportCount);
            affectedCount = itemView.findViewById(R.id.affectedCount);
            notSureCount = itemView.findViewById(R.id.notSureCount);
            invalidCount = itemView.findViewById(R.id.invalidCount);
            fixedCount = itemView.findViewById(R.id.fixedCount);
            commentCount = itemView.findViewById(R.id.commentCount);
            postAuthorProfile = itemView.findViewById(R.id.postAuthorProfile);

            // Reaction containers
            supportReaction = itemView.findViewById(R.id.supportReaction);
            affectedReaction = itemView.findViewById(R.id.affectedReaction);
            notSureReaction = itemView.findViewById(R.id.notSureReaction);
            invalidReaction = itemView.findViewById(R.id.invalidReaction);
            fixedReaction = itemView.findViewById(R.id.fixedReaction);

            Log.d("PostViewHolder", "Initialized views");
        }
    }
}