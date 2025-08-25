package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NotificationResponse {
    private String status;
    private String message;
    private List<Notification> notifications;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public static class Notification {
        @SerializedName("id")
        private long id; // Added to track notification ID
        @SerializedName("author_name")
        private String authorName;
        @SerializedName("username") // Fallback for alternative field name
        private String username;
        private String action;
        private String timestamp;
        @SerializedName("issue_description")
        private String issueDescription;
        @SerializedName("issue_id")
        private long issueId;

        @SerializedName("is_read")
        private boolean isRead;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getAuthorName() {
            return authorName != null ? authorName : (username != null ? username : "Unknown");
        }

        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getIssueDescription() {
            return issueDescription;
        }

        public void setIssueDescription(String issueDescription) {
            this.issueDescription = issueDescription;
        }

        public int getIssueId() {
            return (int) issueId;
        }

        public void setIssueId(long issueId) {
            this.issueId = issueId;
        }

        public boolean isRead() {
            return isRead;
        }

        public void setRead(boolean read) {
            isRead = read;
        }
    }
}