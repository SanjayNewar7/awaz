package com.example.awaz.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import com.example.awaz.R;
import com.example.awaz.view.ItemPostDetailActivity;
import com.example.awaz.view.ProfileActivity;
import com.google.android.material.imageview.ShapeableImageView;

public class NearbyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_feed, container, false);

        LinearLayout postsContainer = view.findViewById(R.id.postsContainer);
        for (int i = 0; i < 8; i++) {
            View postView = inflater.inflate(R.layout.item_post, postsContainer, false);
            postsContainer.addView(postView);

            TextView postTitle = postView.findViewById(R.id.postTitle);
            TextView postDescription = postView.findViewById(R.id.postDescription);
            TextView postSeeMore = postView.findViewById(R.id.postSeeMore);
            TextView commentCount = postView.findViewById(R.id.commentCount);
            TextView postAuthor = postView.findViewById(R.id.postAuthor);
            ShapeableImageView profileImage = postView.findViewById(R.id.postAuthorProfile);

            // Set sample data (you can replace this with dynamic data)
            if (postTitle != null) postTitle.setText("Bad Road Condition in Ward No. 4, Chitwan");
            if (postDescription != null) postDescription.setText("The road near Ramghat, Ward 4 has been in a terrible condition for months...");
            if (postAuthor != null) postAuthor.setText("Sanjaya Rajbhandari");

            // Click listener for detail view
            View.OnClickListener detailClickListener = v -> {
                Intent detailIntent = new Intent(getActivity(), ItemPostDetailActivity.class);
                if (postTitle != null) detailIntent.putExtra("postTitle", postTitle.getText().toString());
                if (postDescription != null) detailIntent.putExtra("postDescription", postDescription.getText().toString());
                startActivity(detailIntent);
            };

            // Set click listeners for detail view
            if (postTitle != null) postTitle.setOnClickListener(detailClickListener);
            if (postDescription != null) postDescription.setOnClickListener(detailClickListener);
            if (postSeeMore != null) postSeeMore.setOnClickListener(detailClickListener);
            if (postView != null) postView.setOnClickListener(detailClickListener); // Entire post clickable

            // Comment count click listener (scroll to comments)
            if (commentCount != null) {
                commentCount.setText("4 comments");
                commentCount.setOnClickListener(v -> {
                    Intent commentIntent = new Intent(getActivity(), ItemPostDetailActivity.class);
                    if (postTitle != null) commentIntent.putExtra("postTitle", postTitle.getText().toString());
                    if (postDescription != null) commentIntent.putExtra("postDescription", postDescription.getText().toString());
                    commentIntent.putExtra("scroll_to_comments", true);
                    startActivity(commentIntent);
                });
            }

            // Profile and author click listeners
            if (postAuthor != null) {
                postAuthor.setOnClickListener(v -> {
                    Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                    if (postAuthor.getText() != null) profileIntent.putExtra("authorName", postAuthor.getText().toString());
                    startActivity(profileIntent);
                });
            }

            if (profileImage != null) {
                profileImage.setOnClickListener(v -> {
                    Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                    if (postAuthor != null && postAuthor.getText() != null) profileIntent.putExtra("authorName", postAuthor.getText().toString());
                    startActivity(profileIntent);
                });
            }
        }

        return view;
    }
}