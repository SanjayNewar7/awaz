package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awaz.R;
import com.example.awaz.adapter.SystemNotificationAdapter;
import com.example.awaz.model.SystemNotification;
import com.example.awaz.service.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SystemNotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SystemNotificationAdapter adapter;
    private List<SystemNotification> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_notification);

        recyclerView = findViewById(R.id.recyclerSystemNotifications);
        ImageView backButton = findViewById(R.id.backButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SystemNotificationAdapter(this, notifications);
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SystemNotificationActivity.this, HomeMainActivity.class);
            startActivity(intent);
            finish();
        });

        fetchNotifications();
    }

    private void fetchNotifications() {
        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(this);
        Call<List<SystemNotification>> call = apiService.getSystemNotifications();
        call.enqueue(new Callback<List<SystemNotification>>() {
            @Override
            public void onResponse(Call<List<SystemNotification>> call, Response<List<SystemNotification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notifications.clear();
                    notifications.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SystemNotificationActivity.this, "Failed to load system notifications", Toast.LENGTH_SHORT).show();
                    Log.e("API", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<SystemNotification>> call, Throwable t) {
                Toast.makeText(SystemNotificationActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API", "Network error: " + t.getMessage());
            }
        });
    }
}