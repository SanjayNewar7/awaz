package com.example.awaz.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.awaz.R;
import com.example.awaz.model.CommentResponse;
import com.example.awaz.service.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

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
        String baseUrl = RetrofitClient.getBaseUrl() + "storage/";
        if (comment.getProfileImage() != null && !comment.getProfileImage().isEmpty()) {
            Glide.with(context).load(baseUrl + comment.getProfileImage())
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .circleCrop()
                    .into(holder.profileImage);
        } else {
            Glide.with(context).load(R.drawable.profile).into(holder.profileImage);
        }

        // Load comment image if available
        if (comment.getImagePath() != null && !comment.getImagePath().isEmpty()) {
            Glide.with(context).load(baseUrl + comment.getImagePath())
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(holder.commentImage);
            holder.commentImage.setVisibility(View.VISIBLE);
        } else {
            holder.commentImage.setVisibility(View.GONE);
        }
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
        if (createdAt == null || createdAt.isEmpty()) return "unknown";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date commentDate = sdf.parse(createdAt);
            if (commentDate == null) return "unknown";

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
            Log.e("CommentAdapter", "Error parsing date: " + e.getMessage());
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