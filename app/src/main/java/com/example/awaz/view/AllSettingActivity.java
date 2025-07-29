package com.example.awaz.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;
import com.google.android.material.imageview.ShapeableImageView;

public class AllSettingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ShapeableImageView profileImage;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_setting);

        // Initialize views
        profileImage = findViewById(R.id.profile_image);
        ImageButton editButton = findViewById(R.id.edit_button);
        LinearLayout personalInfoLayout = findViewById(R.id.personalInfoLayout);
        LinearLayout myCitizenshipLayout = findViewById(R.id.myCitizenshipLayout);
        LinearLayout passwordSecurityLayout = findViewById(R.id.passwordSecurityLayout);
        LinearLayout notificationPreferencesLayout = findViewById(R.id.notificationPreferencesLayout);
        LinearLayout faqLayout = findViewById(R.id.faqLayout);
        LinearLayout helpCenterLayout = findViewById(R.id.helpCenterLayout);
        LinearLayout termsPolicyLayout = findViewById(R.id.termsPolicyLayout);
        LinearLayout aboutUsLayout = findViewById(R.id.aboutUsLayout);
        LinearLayout exitLayout = findViewById(R.id.exitLayout);
        ImageView back = findViewById(R.id.backArrow);

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        profileImage.setImageURI(selectedImageUri);
                    }
                });

        // Edit button click listener
        editButton.setOnClickListener(v -> {
            new AlertDialog.Builder(AllSettingActivity.this)
                    .setTitle("Update Profile Image")
                    .setMessage("Do you want to update your profile image?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Open image picker
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        imagePickerLauncher.launch(intent);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Profile image click listener for full screen view
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, FullscreenImageActivity.class);
            intent.putExtra("image_resource", R.drawable.profile);
            startActivity(intent);
        });

        // Back button click listener
        back.setOnClickListener(view -> {
            Intent intent = new Intent(AllSettingActivity.this, HomeMainActivity.class);
            startActivity(intent);
        });

        // Other menu item click listeners
        personalInfoLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, PersonalInformationActivity.class);
            startActivity(intent);
        });

        myCitizenshipLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, MyCitizenshipActivity.class);
            startActivity(intent);
        });

        passwordSecurityLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, PasswordandSecurityActivity.class);
            startActivity(intent);
        });

        notificationPreferencesLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, NotificationAndPreferences.class);
            startActivity(intent);
        });

        faqLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, FaqActivity.class);
            startActivity(intent);
        });

        helpCenterLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, HelpCenterActivity.class);
            startActivity(intent);
        });

        termsPolicyLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, TermsandPolicyActivity.class);
            startActivity(intent);
        });

        aboutUsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AllSettingActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });

        exitLayout.setOnClickListener(v -> {
            finishAffinity();
            System.exit(0);
        });
    }
}