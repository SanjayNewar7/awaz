package com.example.awaz.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.awaz.R;
import com.example.awaz.service.RetrofitClient;

public class FullscreenImageActivity extends AppCompatActivity {

    private static final String TAG = "FullscreenImageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ImageView fullScreenImage = findViewById(R.id.full_screen_image);

        // Get the image URL from intent
        String imageUrl = getIntent().getStringExtra("image_url");
        if (imageUrl == null || imageUrl.isEmpty()) {
            Log.e(TAG, "No image URL provided, using default image");
            fullScreenImage.setImageResource(R.drawable.profile);
        } else {
            Log.d(TAG, "Loading image URL: " + imageUrl);

            // Handle authentication for protected images
            String accessToken = RetrofitClient.getAccessToken(this);
            GlideUrl glideUrl = accessToken != null && !accessToken.isEmpty()
                    ? new GlideUrl(imageUrl, new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build())
                    : new GlideUrl(imageUrl);

            Glide.with(this)
                    .load(glideUrl)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(fullScreenImage);
        }

        // Close activity on image click
        fullScreenImage.setOnClickListener(v -> finish());
    }
}