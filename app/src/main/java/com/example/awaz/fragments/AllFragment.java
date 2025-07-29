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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awaz.R;
import com.example.awaz.adapter.PostAdapter;
import com.example.awaz.model.Post;
import com.example.awaz.view.ItemPostDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class AllFragment extends Fragment {

    private static final String TAG = "AllFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_feed, container, false);
        RecyclerView recyclerPosts = view.findViewById(R.id.recyclerPosts);

        List<Post> posts = getPosts(); // Your method to get data
        PostAdapter adapter = new PostAdapter(getContext(), posts);

        recyclerPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPosts.setAdapter(adapter);

        return view;
    }
    // Dummy data method to simulate fetching posts
    private List<Post> getPosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post("Title 1", "Description 1", "Author 1", "Category 1", "3 hours ago", 10, 5, 2, 0, 1));
        posts.add(new Post("Title 2", "Description 2", "Author 2", "Category 2", "1 hour ago", 20, 8, 1, 0, 2));

        return posts;
}
}