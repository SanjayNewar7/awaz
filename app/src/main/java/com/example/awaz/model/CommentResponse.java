package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;

public class CommentResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("comment")
    private Comment comment;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Comment getComment() { return comment; }
    public void setComment(Comment comment) { this.comment = comment; }

    public static class Comment {
        @SerializedName("id")
        private int id;

        @SerializedName("issue_id")
        private int issueId;

        @SerializedName("user_id")
        private int userId;

        @SerializedName("comment")
        private String comment;

        @SerializedName("image_path")
        private String imagePath;

        @SerializedName("first_name")
        private String firstName;

        @SerializedName("last_name")
        private String lastName;

        @SerializedName("profile_image")
        private String profileImage;

        @SerializedName("created_at")
        private String createdAt;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getIssueId() { return issueId; }
        public void setIssueId(int issueId) { this.issueId = issueId; }

        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public String getImagePath() { return imagePath; }
        public void setImagePath(String imagePath) { this.imagePath = imagePath; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getProfileImage() { return profileImage; }
        public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }
}