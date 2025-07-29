package com.example.awaz.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.awaz.R;
import com.example.awaz.view.ItemPostDetailActivity;

public class TrendingFragment extends Fragment {

    private static final String TAG = "TrendingFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_feed, container, false);

        LinearLayout postsContainer = view.findViewById(R.id.postsContainer);
        if (postsContainer == null) {
            Toast.makeText(getContext(), "postsContainer is null", Toast.LENGTH_SHORT).show();
            return view;
        }

        for (int i = 0; i < 8; i++) {
            View postView = inflater.inflate(R.layout.item_post, postsContainer, false);
            postsContainer.addView(postView);

            TextView postTitle = postView.findViewById(R.id.postTitle);
            TextView postDescription = postView.findViewById(R.id.postDescription);
            TextView postSeeMore = postView.findViewById(R.id.postSeeMore);

            View.OnClickListener detailClickListener = v -> {
                Toast.makeText(getActivity(), "Redirecting to details: " + ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
                Intent detailIntent = new Intent(getActivity(), ItemPostDetailActivity.class);
                if (postTitle != null) detailIntent.putExtra("postTitle", postTitle.getText().toString());
                if (postDescription != null) detailIntent.putExtra("postDescription", postDescription.getText().toString());
                try {
                    if (getActivity() != null) {
                        startActivity(detailIntent);
                        Log.d(TAG, "Successfully started ItemPostDetailActivity");
                    } else {
                        Toast.makeText(getContext(), "Activity not available", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "getActivity() is null");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to start activity: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Error launching activity: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

            // Set click listeners with debug

        }

        return view;
    }
}