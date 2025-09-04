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
import com.example.awaz.model.UserData;
import com.example.awaz.service.RetrofitClient;
import com.example.awaz.controller.UserController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyFragment extends Fragment {

    private static final String TAG = "NearbyFragment";
    private RecyclerView recyclerPosts;
    private PostAdapter adapter;
    private List<Post> posts = new ArrayList<>();
    private int userWard = -1;
    private ProgressBar progressBar;
    private View overlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_feed, container, false);
        recyclerPosts = view.findViewById(R.id.recyclerPosts);
        progressBar = view.findViewById(R.id.progressBar);
        overlay = view.findViewById(R.id.overlay);

        adapter = new PostAdapter(getContext(), posts);
        recyclerPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPosts.setAdapter(adapter);

        showLoading();
        fetchUserWardAndPosts();

        return view;
    }

    private void showLoading() {
        if (progressBar != null && overlay != null) {
            progressBar.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);
            recyclerPosts.setVisibility(View.GONE);
        }
    }

    private void hideLoading() {
        if (progressBar != null && overlay != null) {
            progressBar.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            recyclerPosts.setVisibility(View.VISIBLE);
        }
    }

    private void fetchUserWardAndPosts() {
        UserController userController = new UserController(getContext(), null);
        userController.getCurrentUser(RetrofitClient.getAccessToken(getContext()), new UserController.UserDataCallback() {
            @Override
            public void onSuccess(UserData userData) {
                if (userData != null) {
                    userWard = userData.getWard();
                    Log.d(TAG, "User ward fetched: " + userWard);
                    fetchPosts();
                } else {
                    hideLoading();
                    Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                hideLoading();
                Log.e(TAG, "Failed to fetch user data: " + errorMessage);
                Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPosts() {
        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(getContext());
        Call<PostsResponse> call = apiService.getPosts();

        call.enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PostsResponse postsResponse = response.body();
                    if ("success".equals(postsResponse.getStatus())) {
                        List<Post> allPosts = postsResponse.getPosts();
                        Log.d(TAG, "Total posts from API: " + allPosts.size());

                        // Debug: Log all posts to see what we're working with
                        for (Post post : allPosts) {
                            Log.d(TAG, "Post: ID=" + post.getId() +
                                    ", District=" + post.getDistrict() +
                                    ", RegionType=" + post.getRegionType() +
                                    ", Ward=" + post.getWard() +
                                    ", Title=" + post.getTitle());
                        }

                        // Since location data is missing from API, we'll use a different approach
                        simulateNearbyPosts(allPosts);
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Nearby posts displayed. Total posts: " + posts.size());
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                }
                hideLoading();
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "Error fetching posts: " + t.getMessage());
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Since the API doesn't provide location data, we'll simulate nearby posts
     * by extracting ward numbers from post titles and descriptions
     */
    private void simulateNearbyPosts(List<Post> allPosts) {
        posts.clear();

        if (allPosts == null || allPosts.isEmpty()) {
            Log.d(TAG, "No posts to filter");
            return;
        }

        // Extract ward numbers from post content
        List<PostWithWard> postsWithWards = new ArrayList<>();
        for (Post post : allPosts) {
            int extractedWard = extractWardFromPost(post);
            postsWithWards.add(new PostWithWard(post, extractedWard));
            Log.d(TAG, "Post '" + post.getTitle() + "' assigned to ward: " + extractedWard);
        }

        // If user ward is not set, just show all posts
        if (userWard == -1) {
            for (PostWithWard postWithWard : postsWithWards) {
                posts.add(postWithWard.post);
            }
            return;
        }

        // Get ordered wards by proximity using BFS
        List<Integer> orderedWards = getOrderedWardsByProximity(userWard);
        Log.d(TAG, "Ordered wards: " + orderedWards);

        // Sort posts based on ward proximity
        Map<Integer, List<Post>> wardPostsMap = new HashMap<>();
        for (PostWithWard postWithWard : postsWithWards) {
            int ward = postWithWard.ward;
            wardPostsMap.computeIfAbsent(ward, k -> new ArrayList<>()).add(postWithWard.post);
        }

        // Add posts in order of ward proximity
        for (int ward : orderedWards) {
            List<Post> wardPosts = wardPostsMap.get(ward);
            if (wardPosts != null) {
                posts.addAll(wardPosts);
                Log.d(TAG, "Added " + wardPosts.size() + " posts from ward " + ward);
            }
        }

        // Add any posts that couldn't be assigned to a ward (ward = -1)
        List<Post> unassignedPosts = wardPostsMap.get(-1);
        if (unassignedPosts != null) {
            posts.addAll(unassignedPosts);
            Log.d(TAG, "Added " + unassignedPosts.size() + " unassigned posts");
        }
    }

    /**
     * Extract ward number from post title or description
     * This is a heuristic approach since the API doesn't provide ward data
     */
    private int extractWardFromPost(Post post) {
        // Try to extract from title first
        int wardFromTitle = extractWardFromText(post.getTitle());
        if (wardFromTitle != -1) {
            return wardFromTitle;
        }

        // Then try from description
        int wardFromDescription = extractWardFromText(post.getDescription());
        if (wardFromDescription != -1) {
            return wardFromDescription;
        }

        // If no ward found, return -1
        return -1;
    }

    /**
     * Extract ward number from text using pattern matching
     */
    private int extractWardFromText(String text) {
        if (text == null || text.isEmpty()) {
            return -1;
        }

        // Look for patterns like "ward 5", "ward no. 5", "ward number 5", etc.
        String[] patterns = {
                "ward\\s+no\\.?\\s*(\\d+)",
                "ward\\s+number\\s+(\\d+)",
                "ward\\s+(\\d+)",
                "ward-(\\d+)",
                "in ward (\\d+)",
                "ward:\\s*(\\d+)"
        };

        for (String pattern : patterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher m = p.matcher(text);
            if (m.find()) {
                try {
                    int ward = Integer.parseInt(m.group(1));
                    if (ward >= 1 && ward <= 29) { // Validate ward range for Bharatpur
                        return ward;
                    }
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Failed to parse ward number from: " + m.group(1));
                }
            }
        }

        return -1;
    }

    private List<Integer> getOrderedWardsByProximity(int startWard) {
        Map<Integer, List<Integer>> wardNeighbors = new HashMap<>();
        wardNeighbors.put(1, Arrays.asList(2, 5, 6));
        wardNeighbors.put(2, Arrays.asList(1, 3, 6, 7));
        wardNeighbors.put(3, Arrays.asList(2, 4, 7, 8));
        wardNeighbors.put(4, Arrays.asList(3, 5, 8, 9));
        wardNeighbors.put(5, Arrays.asList(1, 4, 6, 9, 10));
        wardNeighbors.put(6, Arrays.asList(1, 2, 5, 10, 11));
        wardNeighbors.put(7, Arrays.asList(2, 3, 8, 11, 12));
        wardNeighbors.put(8, Arrays.asList(3, 4, 7, 9, 12, 13));
        wardNeighbors.put(9, Arrays.asList(4, 5, 8, 10, 13, 14));
        wardNeighbors.put(10, Arrays.asList(5, 6, 9, 11, 14, 15));
        wardNeighbors.put(11, Arrays.asList(6, 7, 10, 12, 15, 16));
        wardNeighbors.put(12, Arrays.asList(7, 8, 11, 13, 16, 17));
        wardNeighbors.put(13, Arrays.asList(8, 9, 12, 14, 17, 18));
        wardNeighbors.put(14, Arrays.asList(9, 10, 13, 15, 18, 19));
        wardNeighbors.put(15, Arrays.asList(10, 11, 14, 16, 19, 20));
        wardNeighbors.put(16, Arrays.asList(11, 12, 15, 17, 20, 21));
        wardNeighbors.put(17, Arrays.asList(12, 13, 16, 18, 21, 22));
        wardNeighbors.put(18, Arrays.asList(13, 14, 17, 19, 22, 23));
        wardNeighbors.put(19, Arrays.asList(14, 15, 18, 20, 23, 24));
        wardNeighbors.put(20, Arrays.asList(15, 16, 19, 21, 24, 25));
        wardNeighbors.put(21, Arrays.asList(16, 17, 20, 22, 25, 26));
        wardNeighbors.put(22, Arrays.asList(17, 18, 21, 23, 26, 27));
        wardNeighbors.put(23, Arrays.asList(18, 19, 22, 24, 27, 28));
        wardNeighbors.put(24, Arrays.asList(19, 20, 23, 25, 28, 29));
        wardNeighbors.put(25, Arrays.asList(20, 21, 24, 26, 29));
        wardNeighbors.put(26, Arrays.asList(21, 22, 25, 27, 29));
        wardNeighbors.put(27, Arrays.asList(22, 23, 26, 28));
        wardNeighbors.put(28, Arrays.asList(23, 24, 27, 29));
        wardNeighbors.put(29, Arrays.asList(24, 25, 26, 28));

        List<Integer> orderedWards = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        // If startWard is not in our map, just return it alone
        if (!wardNeighbors.containsKey(startWard)) {
            orderedWards.add(startWard);
            return orderedWards;
        }

        queue.add(startWard);
        visited.add(startWard);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            orderedWards.add(current);

            List<Integer> neighbors = wardNeighbors.get(current);
            if (neighbors != null) {
                for (int neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }

        return orderedWards;
    }

    // Helper class to associate posts with extracted wards
    private static class PostWithWard {
        Post post;
        int ward;

        PostWithWard(Post post, int ward) {
            this.post = post;
            this.ward = ward;
        }
    }
}