package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awaz.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private android.widget.ImageView backArrow;
    private RecyclerView notificationRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize views
        backArrow = findViewById(R.id.backArrow);
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);

        ImageView profileIcon = findViewById(R.id.imgProfile);
        LinearLayout settingLayout = findViewById(R.id.settingLayout);
        LinearLayout filterLayout = findViewById(R.id.filterLayout);
        LinearLayout raiseIssueLayout = findViewById(R.id.raiseIssueLayout);
        LinearLayout myNotificationLayout = findViewById(R.id.myNotificationLayout);


        myNotificationLayout.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        raiseIssueLayout.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, RaiseIssueActivity.class);
            startActivity(intent);
        });

        profileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        settingLayout.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, AllSettingActivity.class);
            startActivity(intent);
        });

        filterLayout.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, FilterActivity.class);
            startActivity(intent);
        });


        // Set up RecyclerView
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        NotificationAdapter adapter = new NotificationAdapter(getSampleNotifications());
        notificationRecyclerView.setAdapter(adapter);

        // Back arrow click listener
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationActivity.this, HomeMainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private List<NotificationItem> getSampleNotifications() {
        List<NotificationItem> notifications = new ArrayList<>();
        notifications.add(new NotificationItem("Gaurab Gywali", "supported", "2m ago", "Bad Road Condition in Ward No.4, Chitwan", R.drawable.support_blue));
        notifications.add(new NotificationItem("Sumikshya Shrestha", "supported fixed", "40m ago", "Bad Road Condition in Ward No.4, Chitwan", R.drawable.fixed_green));
        notifications.add(new NotificationItem("Rajan Sapkota", "supported affected", "1h ago", "Bad Road Condition in Ward No.4, Chitwan", R.drawable.affected_yellow));
        notifications.add(new NotificationItem("Aakriti Shrestha", "commented", "3h ago", "Bad Road Condition in Ward No.4, Chitwan", R.drawable.comment_icon));
        notifications.add(new NotificationItem("Arun Gurung", "commented", "1d ago", "Bad Road Condition in Ward No.4, Chitwan", R.drawable.comment_icon));
        notifications.add(new NotificationItem("Mandira Gurung", "gave you a heart", "2d ago", "Bad Road Condition in Ward No.4, Chitwan", R.drawable.heart));
        notifications.add(new NotificationItem("Sanjaya Gurung", "supported", "2d ago", "Bad Road Condition in Ward No.4, Chitwan", R.drawable.support_blue));
        notifications.add(new NotificationItem("Anita Sharma", "supported not sure", "3d ago", "Water Leakage in Ward No.2", R.drawable.not_sure));
        notifications.add(new NotificationItem("Ramesh Thapa", "supported invalid", "4d ago", "Power Outage in Ward No.5", R.drawable.invalid_white));
        notifications.add(new NotificationItem("Sita Karki", "supported affected", "5d ago", "Potholes in Ward No.3", R.drawable.affected_yellow));
        notifications.add(new NotificationItem("Bikash Rai", "commented", "6d ago", "Garbage Overflow in Ward No.6", R.drawable.comment_icon));
        notifications.add(new NotificationItem("Prakash Lama", "supported", "7d ago", "Drainage Issue in Ward No.1", R.drawable.support_blue));
        notifications.add(new NotificationItem("Nirmala Thapa", "supported fixed", "8d ago", "Street Light Outage in Ward No.7", R.drawable.fixed_green));
        notifications.add(new NotificationItem("Kamal Gurung", "gave you a heart", "9d ago", "Bad Road Condition in Ward No.4, Chitwan", R.drawable.heart));
        notifications.add(new NotificationItem("Sarita Shrestha", "supported not sure", "10d ago", "Water Supply Issue in Ward No.8", R.drawable.not_sure));
        notifications.add(new NotificationItem("Hari Bahadur", "supported invalid", "11d ago", "Noise Pollution in Ward No.9", R.drawable.invalid_white));
        notifications.add(new NotificationItem("Sunita Adhikari", "supported affected", "12d ago", "Park Maintenance in Ward No.10", R.drawable.affected_yellow));
        notifications.add(new NotificationItem("Raju Sharma", "commented", "13d ago", "Traffic Jam in Ward No.11", R.drawable.comment_icon));
        notifications.add(new NotificationItem("Mina Khatri", "received account deletion warning", "14d ago", "Multiple Reports on Profile", R.drawable.warning_sign));
        notifications.add(new NotificationItem("Bimala Tamang", "wrong post warning", "15d ago", "Inappropriate Content Posted", R.drawable.warning_sign));
        return notifications;
    }

    // Notification Item class
    private static class NotificationItem {
        String authorName;
        String issueType;
        String timestamp;
        String issueDescription;
        int iconResId;

        NotificationItem(String authorName, String issueType, String timestamp, String issueDescription, int iconResId) {
            this.authorName = authorName;
            this.issueType = issueType;
            this.timestamp = timestamp;
            this.issueDescription = issueDescription;
            this.iconResId = iconResId;
        }
    }

    // Notification Adapter
    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

        private List<NotificationItem> notifications;

        NotificationAdapter(List<NotificationItem> notifications) {
            this.notifications = notifications;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            NotificationItem item = notifications.get(position);
            // Extract the base action and additional context from issueType
            String[] issueParts = item.issueType.split(" ", 2); // Split into action and optional modifier
            String action = issueParts[0]; // e.g., "supported", "commented"
            String modifier = issueParts.length > 1 ? " " + issueParts[1] : ""; // e.g., " fixed", " not sure"

            // Determine the context phrase from issueDescription (e.g., "Road Issue" from "Bad Road Condition...")
            String contextPhrase = getContextPhrase(item.issueDescription);

            // Construct notification text
            String notificationText;
            if (item.issueType.contains("warning")) {
                // Special case for warnings
                notificationText = item.authorName + " " + item.issueType;
            } else {
                notificationText = item.authorName + " " + action + " your " + contextPhrase + modifier;
            }
            holder.notificationText.setText(notificationText);
            holder.timestamp.setText(item.timestamp);
            holder.issueDescription.setText(item.issueDescription); // Optional: hide this if not needed
            holder.heartIcon.setImageResource(item.iconResId);
        }

        // Helper method to extract context phrase (e.g., "Road Issue" from "Bad Road Condition...")
        private String getContextPhrase(String issueDescription) {
            if (issueDescription.toLowerCase().contains("road")) return "Road Issue";
            if (issueDescription.toLowerCase().contains("water")) return "Water Issue";
            if (issueDescription.toLowerCase().contains("power")) return "Power Issue";
            if (issueDescription.toLowerCase().contains("garbage")) return "Garbage Issue";
            if (issueDescription.toLowerCase().contains("drainage")) return "Drainage Issue";
            if (issueDescription.toLowerCase().contains("street light")) return "Street Light Issue";
            if (issueDescription.toLowerCase().contains("noise")) return "Noise Issue";
            if (issueDescription.toLowerCase().contains("park")) return "Park Issue";
            if (issueDescription.toLowerCase().contains("traffic")) return "Traffic Issue";
            return "Issue"; // Default fallback
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView notificationText, timestamp, issueDescription;
            ImageView heartIcon;

            ViewHolder(View itemView) {
                super(itemView);
                notificationText = itemView.findViewById(R.id.notificationText);
                timestamp = itemView.findViewById(R.id.timestamp);
                issueDescription = itemView.findViewById(R.id.issueDescription);
                heartIcon = itemView.findViewById(R.id.heartIcon);
            }
        }
    }
}