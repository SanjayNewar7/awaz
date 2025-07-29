package com.example.awaz.view;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;

public class FullscreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView full_screen_image = findViewById(R.id.full_screen_image);

        // Get the image resource from intent
        int imageResource = getIntent().getIntExtra("image_resource", R.drawable.profile);
        full_screen_image.setImageResource(imageResource);

        // Set click listener to close activity
        full_screen_image.setOnClickListener(v -> finish());
    }
}