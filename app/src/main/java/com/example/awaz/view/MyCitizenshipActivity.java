package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;

public class MyCitizenshipActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_citizenship_activity); // Ensure this matches the XML filename

        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(MyCitizenshipActivity.this, AllSettingActivity.class);
            startActivity(intent);
        });
    }
}