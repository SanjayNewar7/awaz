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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private static final String TAG = "PostAdapter";
    private final Context context;
    private final List<Post> postList;

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

        Log.d(TAG, "Bound data for position " + position + ": " + post.getTitle());

        holder.postTitle.setOnClickListener(v -> openPostDetail(post));
        holder.postDescription.setOnClickListener(v -> openPostDetail(post));

        holder.supportCount.setOnClickListener(v -> addReaction(post.getId(), "support", holder));
        holder.affectedCount.setOnClickListener(v -> addReaction(post.getId(), "affected", holder));
        holder.notSureCount.setOnClickListener(v -> addReaction(post.getId(), "not_sure", holder));
        holder.invalidCount.setOnClickListener(v -> addReaction(post.getId(), "invalid", holder));
        holder.fixedCount.setOnClickListener(v -> addReaction(post.getId(), "fixed", holder));

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
                    .into((ImageView) holder.itemView.findViewById(R.id.postAuthorProfile));
        } else {
            Glide.with(context).load(R.drawable.profile)
                    .into((ImageView) holder.itemView.findViewById(R.id.postAuthorProfile));
        }
    }

    private void addReaction(int postId, String reactionType, PostViewHolder holder) {
        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(context);
        ReactionRequest reactionRequest = new ReactionRequest(reactionType);
        Call<ReactionResponse> call = apiService.addReaction(postId, reactionRequest);

        call.enqueue(new Callback<ReactionResponse>() {
            @Override
            public void onResponse(Call<ReactionResponse> call, Response<ReactionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReactionResponse reactionResponse = response.body();
                    if ("success".equals(reactionResponse.getStatus())) {
                        // Update the reaction counts
                        ReactionResponse.ReactionCount supportCount = reactionResponse.getReactionCounts().get("support");
                        ReactionResponse.ReactionCount affectedCount = reactionResponse.getReactionCounts().get("affected");
                        ReactionResponse.ReactionCount notSureCount = reactionResponse.getReactionCounts().get("not_sure");
                        ReactionResponse.ReactionCount invalidCount = reactionResponse.getReactionCounts().get("invalid");
                        ReactionResponse.ReactionCount fixedCount = reactionResponse.getReactionCounts().get("fixed");

                        if (supportCount != null) holder.supportCount.setText(String.valueOf(supportCount.getCount()));
                        if (affectedCount != null) holder.affectedCount.setText(String.valueOf(affectedCount.getCount()));
                        if (notSureCount != null) holder.notSureCount.setText(String.valueOf(notSureCount.getCount()));
                        if (invalidCount != null) holder.invalidCount.setText(String.valueOf(invalidCount.getCount()));
                        if (fixedCount != null) holder.fixedCount.setText(String.valueOf(fixedCount.getCount()));

                        Toast.makeText(context, "Reaction added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to add reaction", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Failed to add reaction", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReactionResponse> call, Throwable t) {
                Log.e(TAG, "Error adding reaction: " + t.getMessage());
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
        intent.putExtra("post", post); // pass the entire Post object
        context.startActivity(intent);
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postAuthor, postCategory, postTime, postTitle, postDescription;
        TextView supportCount, affectedCount, notSureCount, invalidCount, fixedCount;

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

            // Log to verify view initialization
            Log.d("PostViewHolder", "Initialized views: " + postAuthor.getId());
        }
    }
}