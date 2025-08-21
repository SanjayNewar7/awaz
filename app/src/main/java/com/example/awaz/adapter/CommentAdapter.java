package com.example.awaz.adapter;

import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final Context context;
    private final List<CommentResponse.Comment> comments;

    public CommentAdapter(Context context, List<CommentResponse.Comment> comments) {
        this.context = context;
        this.comments = comments != null ? comments : new ArrayList<>();
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
        holder.commentAuthor.setText(comment.getFirstName() + " " + comment.getLastName());
        holder.commentText.setText(comment.getComment());
        holder.commentTime.setText(getDynamicTime(comment.getCreatedAt()));

        // Load profile image if available
        String baseUrl = RetrofitClient.getBaseUrl();
        if (comment.getProfileImage() != null && !comment.getProfileImage().isEmpty()) {
            Glide.with(context).load(baseUrl + "storage/" + comment.getProfileImage())
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(holder.profileImage);
        } else {
            Glide.with(context).load(R.drawable.profile).into(holder.profileImage);
        }

        // Load comment image if available
        if (comment.getImagePath() != null && !comment.getImagePath().isEmpty()) {
            holder.commentImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(baseUrl + "storage/" + comment.getImagePath())
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(holder.commentImage);
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
            notifyDataSetChanged();
        }
    }

    private String getDynamicTime(String createdAt) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("+0545")); // Nepal Time Zone
            Date commentDate = sdf.parse(createdAt);

            if (commentDate == null) return "Unknown";

            Date currentDate = new Date();
            long diffInMillis = currentDate.getTime() - commentDate.getTime();
            long diffInMinutes = diffInMillis / (1000 * 60);
            long diffInHours = diffInMillis / (1000 * 60 * 60);

            if (diffInMinutes < 1) {
                return "Now";
            } else if (diffInMinutes < 60) {
                return diffInMinutes + " min ago";
            } else {
                return diffInHours + " hr" + (diffInHours > 1 ? "s" : "") + " ago";
            }
        } catch (Exception e) {
            return "Unknown";
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