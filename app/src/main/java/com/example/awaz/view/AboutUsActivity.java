package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;
import com.google.android.material.imageview.ShapeableImageView;

public class AboutUsActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);

        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(AboutUsActivity.this, AllSettingActivity.class);
            startActivity(intent);
        });

        // Initialize ShapeableImageViews
        ShapeableImageView sumikshyaImage = findViewById(R.id.sumikshya_shrestha_image);
        ShapeableImageView sanjayaImage = findViewById(R.id.sanjaya_rajbhandari_image);
        ShapeableImageView gaurabImage = findViewById(R.id.gaurab_gywali_image);

        // Set click listeners for full-screen display
        sumikshyaImage.setOnClickListener(v -> {
            Intent intent = new Intent(AboutUsActivity.this, FullscreenImageActivity.class);
            intent.putExtra("image_resource", R.drawable.sumikshya_shrestha);
            startActivity(intent);
        });

        sanjayaImage.setOnClickListener(v -> {
            Intent intent = new Intent(AboutUsActivity.this, FullscreenImageActivity.class);
            intent.putExtra("image_resource", R.drawable.profile); // Using profile as placeholder
            startActivity(intent);
        });

        gaurabImage.setOnClickListener(v -> {
            Intent intent = new Intent(AboutUsActivity.this, FullscreenImageActivity.class);
            intent.putExtra("image_resource", R.drawable.gaurab_gywali);
            startActivity(intent);
        });
    }
}