package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;

public class HelpCenterActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_center_activity);

        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(HelpCenterActivity.this, AllSettingActivity.class);
            startActivity(intent);
        });
    }



}
