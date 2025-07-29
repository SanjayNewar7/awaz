package com.example.awaz.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;
import com.example.awaz.model.Post;

public class PostDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_post);

        Post post = (Post) getIntent().getSerializableExtra("post");

        TextView postTitle = findViewById(R.id.postTitle);
        TextView postDescription = findViewById(R.id.postDescription);

        if (post != null) {
            postTitle.setText(post.getTitle());
            postDescription.setText(post.getDescription());
            // Add other views as needed
        }
    }
}