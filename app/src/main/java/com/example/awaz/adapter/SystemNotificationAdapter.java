package com.example.awaz.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awaz.R;
import com.example.awaz.model.SystemNotification;
import com.example.awaz.service.RetrofitClient;
import com.example.awaz.view.SystemNotificationDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SystemNotificationAdapter extends RecyclerView.Adapter<SystemNotificationAdapter.ViewHolder> {

    private Context context;
    private List<SystemNotification> notifications;

    public SystemNotificationAdapter(Context context, List<SystemNotification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_system_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SystemNotification notification = notifications.get(position);
        if (notification == null) {
            return;
        }

        holder.authorText.setText("Nepal Government");
        holder.notificationText.setText(notification.getTitle());
        holder.issueDescription.setText(notification.getMessage());

        // Set relative timestamp
        holder.timestamp.setText(getRelativeTime(notification.getCreatedAt()));

        // Set background based on is_read
        holder.itemView.setBackgroundColor(notification.isRead() ? Color.WHITE : Color.parseColor("#ADD8E6"));

        // Handle click to mark as read and open detail
        holder.itemView.setOnClickListener(v -> {
            if (!notification.isRead()) {
                markAsRead(notification.getId(), position);
            }
            Intent intent = new Intent(context, SystemNotificationDetailActivity.class);
            intent.putExtra("notification", notification);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView notificationIcon;
        TextView authorText;
        TextView notificationText;
        TextView timestamp;
        TextView issueDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationIcon = itemView.findViewById(R.id.notificationIcon);
            authorText = itemView.findViewById(R.id.authorText);
            notificationText = itemView.findViewById(R.id.notificationText);
            timestamp = itemView.findViewById(R.id.timestamp);
            issueDescription = itemView.findViewById(R.id.issueDescription);
        }
    }

    private String getRelativeTime(String createdAt) {
        if (createdAt == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            long time = sdf.parse(createdAt).getTime();
            return DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void markAsRead(int notificationId, int position) {
        RetrofitClient.ApiService apiService = RetrofitClient.getApiService(context);
        Call<Void> call = apiService.markSystemNotificationAsRead(notificationId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    notifications.get(position).setRead(true);
                    notifyItemChanged(position);
                } else {
                    Toast.makeText(context, "Failed to mark notification as read", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}