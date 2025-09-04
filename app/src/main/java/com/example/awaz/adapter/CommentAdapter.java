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
import com.example.awaz.R;
import com.example.awaz.model.CommentResponse;
import com.example.awaz.service.RetrofitClient;
import com.example.awaz.view.FullscreenImageActivity;
import com.example.awaz.view.ProfileActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private static final String TAG = "CommentAdapter";
    private final Context context;
    private final List<CommentResponse.Comment> comments;

    public CommentAdapter(Context context, List<CommentResponse.Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentResponse.Comment comment = comments.get(position);
        holder.commentText.setText(comment.getComment());
        holder.commentAuthor.setText(comment.getFirstName() + " " + comment.getLastName());

        // Set relative time for comment
        holder.commentTime.setText(getRelativeTime(comment.getCreatedAt()));

        // Load profile image
        String baseUrl = RetrofitClient.getBaseUrl();
        if (comment.getProfileImage() != null && !comment.getProfileImage().isEmpty()) {
            String imageUrl;
            if (comment.getProfileImage().startsWith("http://") || comment.getProfileImage().startsWith("https://")) {
                imageUrl = comment.getProfileImage();
                Log.d(TAG, "Using full URL for profile image at position " + position + ": " + imageUrl);
            } else {
                imageUrl = baseUrl + (comment.getProfileImage().startsWith("storage/") ? "" : "storage/") + comment.getProfileImage();
                Log.d(TAG, "Constructed URL for profile image at position " + position + ": " + imageUrl);
            }

            String accessToken = RetrofitClient.getAccessToken(context);
            GlideUrl glideUrl = accessToken != null && !accessToken.isEmpty()
                    ? new GlideUrl(imageUrl, new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build())
                    : new GlideUrl(imageUrl);

            Glide.with(context)
                    .load(glideUrl)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .circleCrop()
                    .into(holder.profileImage);
        } else {
            Log.d(TAG, "No profile image for comment at position " + position);
            Glide.with(context).load(R.drawable.profile).into(holder.profileImage);
        }

        // Load comment image if available
        if (comment.getImagePath() != null && !comment.getImagePath().isEmpty()) {
            String imageUrl = baseUrl + "storage/" + comment.getImagePath();
            Log.d(TAG, "Constructed URL for comment image at position " + position + ": " + imageUrl);

            String accessToken = RetrofitClient.getAccessToken(context);
            GlideUrl glideUrl = accessToken != null && !accessToken.isEmpty()
                    ? new GlideUrl(imageUrl, new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build())
                    : new GlideUrl(imageUrl);

            Glide.with(context)
                    .load(glideUrl)
                    .thumbnail(0.25f)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(holder.commentImage);
            holder.commentImage.setVisibility(View.VISIBLE);

            // Add click listener for full-screen view
            holder.commentImage.setOnClickListener(v -> {
                Log.d(TAG, "Comment image clicked at position " + position + ", launching FullscreenImageActivity with URL: " + imageUrl);
                Intent intent = new Intent(context, FullscreenImageActivity.class);
                intent.putExtra("image_url", imageUrl);
                context.startActivity(intent);
            });
        } else {
            Log.d(TAG, "No comment image for comment at position " + position);
            holder.commentImage.setVisibility(View.GONE);
        }

        // Add click listeners for profile image and username
        holder.profileImage.setOnClickListener(v -> {
            if (comment.getUserId() == -1) {
                Log.e(TAG, "Invalid userId for comment at position " + position);
                Toast.makeText(context, "User ID not available", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Profile image clicked for user ID: " + comment.getUserId());
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("user_id", comment.getUserId());
            context.startActivity(intent);
        });

        holder.commentAuthor.setOnClickListener(v -> {
            if (comment.getUserId() == -1) {
                Log.e(TAG, "Invalid userId for comment at position " + position);
                Toast.makeText(context, "User ID not available", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Comment author clicked for user ID: " + comment.getUserId());
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("user_id", comment.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void updateComments(List<CommentResponse.Comment> newComments) {
        comments.clear();
        if (newComments != null) {
            comments.addAll(newComments);
        }
        notifyDataSetChanged();
    }

    public void addComment(CommentResponse.Comment comment) {
        if (comment != null) {
            comments.add(0, comment); // Add new comment at the top
            notifyItemInserted(0);
        }
    }

    private String getRelativeTime(String createdAt) {
        if (createdAt == null || createdAt.isEmpty()) {
            Log.w(TAG, "createdAt is null or empty");
            return "unknown";
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date commentDate = sdf.parse(createdAt);
            if (commentDate == null) {
                Log.w(TAG, "Failed to parse createdAt: " + createdAt);
                return "unknown";
            }

            long diffInMillis = System.currentTimeMillis() - commentDate.getTime();
            long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (diffInSeconds < 60) {
                return "just now";
            } else if (diffInMinutes < 60) {
                return diffInMinutes + " mins ago";
            } else if (diffInHours < 24) {
                return diffInHours + " hrs ago";
            } else {
                return diffInDays + " days ago";
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + createdAt + ", Error: " + e.getMessage());
            return "unknown";
        }
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentAuthor, commentText, commentTime;
        ImageView profileImage, commentImage;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentAuthor = itemView.findViewById(R.id.commentAuthor);
            commentText = itemView.findViewById(R.id.commentText);
            commentTime = itemView.findViewById(R.id.commentTime);
            profileImage = itemView.findViewById(R.id.profileImage);
            commentImage = itemView.findViewById(R.id.commentImage);
        }
    }
}