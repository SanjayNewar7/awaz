package com.example.awaz.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awaz.R;
import com.example.awaz.model.Post;
import com.example.awaz.view.ItemPostDetailActivity;
import com.example.awaz.view.PostDetailActivity;

import java.util.List;

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
        holder.postAuthor.setText(post.getAuthor());
        holder.postCategory.setText(post.getCategory());
        holder.postTime.setText(post.getTime());
        holder.postTitle.setText(post.getTitle());
        holder.postDescription.setText(post.getDescription());
        holder.supportCount.setText(String.valueOf(post.getSupportCount()));
        holder.affectedCount.setText(String.valueOf(post.getAffectedCount()));
        holder.notSureCount.setText(String.valueOf(post.getNotSureCount()));
        holder.invalidCount.setText(String.valueOf(post.getInvalidCount()));
        holder.fixedCount.setText(String.valueOf(post.getFixedCount()));

        Log.d(TAG, "Bound data for position " + position + ": " + post.getTitle());

        // Set click listeners for title and description
        holder.postTitle.setOnClickListener(v -> {
            Toast.makeText(context, "Clicked Title", Toast.LENGTH_SHORT).show();
            openPostDetail(post);
        });
        holder.postDescription.setOnClickListener(v -> {
            Toast.makeText(context, "Clicked Title", Toast.LENGTH_SHORT).show();
            openPostDetail(post);
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