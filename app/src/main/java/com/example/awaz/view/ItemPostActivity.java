package com.example.awaz.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;
import com.example.awaz.view.ItemPostDetailActivity;

public class ItemPostActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_post);

        TextView postTitle = findViewById(R.id.postTitle);
        TextView postDescription = findViewById(R.id.postDescription);
        TextView postSeeMore = findViewById(R.id.postSeeMore);

        postTitle.setOnClickListener(v -> {
            Intent intent = new Intent(ItemPostActivity.this, ItemPostDetailActivity.class);
            startActivity(intent);
        });

        postDescription.setOnClickListener(v -> {
            Intent intent = new Intent(ItemPostActivity.this, ItemPostDetailActivity.class);
            startActivity(intent);
        });

        postSeeMore.setOnClickListener(v -> {
            Intent intent = new Intent(ItemPostActivity.this, ItemPostDetailActivity.class);
            startActivity(intent);
        });




    }
}