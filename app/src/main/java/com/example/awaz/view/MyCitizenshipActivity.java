package com.example.awaz.view;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.awaz.R;
import com.example.awaz.model.UserResponse;
import com.example.awaz.model.UserData;
import com.example.awaz.service.RetrofitClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCitizenshipActivity extends AppCompatActivity {
    private static final String TAG = "CitizenshipActivity";
    private ImageView frontImage;
    private ImageView backImage;
    private Button verifyButton;
    private Button reapplyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_citizenship_activity);

        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(MyCitizenshipActivity.this, AllSettingActivity.class);
            startActivity(intent);
        });

        frontImage = findViewById(R.id.frontImage);
        backImage = findViewById(R.id.backImage);
        verifyButton = findViewById(R.id.verifyButton);
        reapplyButton = findViewById(R.id.reapplyButton); // Add this ID

        reapplyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyCitizenshipActivity.this, ReapplyCitizenshipActivity.class);
            startActivity(intent);
        });

        fetchUserData();
    }

    private void fetchUserData() {
        String token = RetrofitClient.getAccessToken(this);
        Log.d(TAG, "Fetching user data with token: ****" + (token != null && token.length() > 4 ? token.substring(token.length() - 4) : "null"));
        Call<UserResponse> call = RetrofitClient.getApiService(this).getCurrentUser();
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.d(TAG, "API Response Code: " + response.code());
                Log.d(TAG, "API Response Message: " + response.message());
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    Log.d(TAG, "Response Body: " + new Gson().toJson(userResponse));
                    UserData user = userResponse.getUser();
                    if (user != null) {
                        String frontUrl = user.getCitizenshipFrontImage();
                        String backUrl = user.getCitizenshipBackImage();

                        Log.d(TAG, "Front URL (from API): " + frontUrl);
                        Log.d(TAG, "Back URL (from API): " + backUrl);
                        String status = user.getVerificationStatus();
                        Log.d(TAG, "Verification Status: " + status);

                        loadImageWithGlide(frontUrl, frontImage);
                        loadImageWithGlide(backUrl, backImage);

                        // Update button based on verification_status
                        if ("verified".equalsIgnoreCase(status)) {
                            verifyButton.setText("Verified");
                            verifyButton.setBackgroundResource(R.drawable.rounded_button_green);
                            reapplyButton.setVisibility(Button.GONE); // Hide reapply button
                        } else if ("rejected".equalsIgnoreCase(status)) {
                            verifyButton.setText("Rejected");
                            verifyButton.setBackgroundResource(R.drawable.rounded_button_red);
                            reapplyButton.setVisibility(Button.VISIBLE); // Show reapply button
                        } else if ("pending".equalsIgnoreCase(status)) {
                            verifyButton.setText("Pending");
                            verifyButton.setBackgroundResource(R.drawable.rounded_button_orange);
                            reapplyButton.setVisibility(Button.GONE); // Hide reapply button
                        } else {
                            if (user.isVerified()) {
                                verifyButton.setText("Verified");
                                verifyButton.setBackgroundResource(R.drawable.rounded_button_green);
                            } else {
                                verifyButton.setText("Not Verified");
                                verifyButton.setBackgroundResource(R.drawable.rounded_button_red);
                            }
                            reapplyButton.setVisibility(Button.GONE); // Hide reapply button
                        }
                    } else {
                        Log.e(TAG, "User data is null in response");
                        verifyButton.setText("Error: No User Data");
                        verifyButton.setBackgroundResource(R.drawable.rounded_button_red);
                        reapplyButton.setVisibility(Button.GONE); // Hide reapply button
                    }
                } else {
                    Log.e(TAG, "Response unsuccessful: Code " + response.code() + ", Message: " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error Body: " + errorBody);
                        if (response.code() == 404) {
                            verifyButton.setText("Error: User Not Found");
                            verifyButton.setBackgroundResource(R.drawable.rounded_button_red);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to read error body", e);
                    }
                    reapplyButton.setVisibility(Button.GONE); // Hide reapply button
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e(TAG, "API Failure: " + t.getMessage(), t);
                verifyButton.setText("Network Error");
                verifyButton.setBackgroundResource(R.drawable.rounded_button_red);
                if (reapplyButton != null) reapplyButton.setVisibility(Button.GONE); // Hide reapply button
            }
        });
    }

    private void loadImageWithGlide(String url, ImageView imageView) {
        if (url == null || url.isEmpty()) {
            Log.w(TAG, "Skipping Glide load: URL is null or empty");
            imageView.setImageResource(R.drawable.ic_error);
            return;
        }

        Log.d(TAG, "Attempting to load image from: " + url);
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .override(500, 500)
                .timeout(10000)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Glide Load Failed for URL: " + model, e);
                        if (e != null) {
                            for (Throwable t : e.getRootCauses()) {
                                Log.e(TAG, "Root cause: " + t.getMessage());
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Glide Loaded Successfully for URL: " + model);
                        return false;
                    }
                })
                .into(imageView);
    }
}