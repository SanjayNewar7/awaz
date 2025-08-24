package com.example.awaz.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awaz.R;
import com.example.awaz.adapter.PostAdapter;
import com.example.awaz.model.Post;
import com.example.awaz.model.PostsResponse;
import com.example.awaz.service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllFragment extends Fragment {

    private static final String TAG = "AllFragment";
    private RecyclerView recyclerPosts;
    private PostAdapter adapter;
    private List<Post> posts = new ArrayList<>();
    private ProgressBar progressBar;
    private View overlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_feed, container, false);

        // Initialize views
        recyclerPosts = view.findViewById(R.id.recyclerPosts);
        progressBar = view.findViewById(R.id.progressBar);
        overlay = view.findViewById(R.id.overlay);

        adapter = new PostAdapter(getContext(), posts);
        recyclerPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPosts.setAdapter(adapter);

        fetchPosts();

        return view;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        recyclerPosts.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
        recyclerPosts.setVisibility(View.VISIBLE);
    }

    private void fetchPosts() {
        showLoading(); // Show loading indicator

        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(getContext());
        Call<PostsResponse> call = apiService.getPosts();

        call.enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                hideLoading(); // Hide loading indicator

                if (response.isSuccessful() && response.body() != null) {
                    PostsResponse postsResponse = response.body();
                    if ("success".equals(postsResponse.getStatus())) {
                        posts.clear();
                        posts.addAll(postsResponse.getPosts());
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Posts loaded successfully. Total posts: " + posts.size());
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "API response status: " + postsResponse.getStatus());
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Unsuccessful response: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {
                hideLoading(); // Hide loading indicator on failure too
                Log.e(TAG, "Error fetching posts: " + t.getMessage());
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Optional: Add a method to refresh posts if needed
    public void refreshPosts() {
        fetchPosts();
    }
}