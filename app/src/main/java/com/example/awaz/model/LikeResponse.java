package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;

public class LikeResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("likes_count")
    private int likesCount;

    @SerializedName("is_liked")
    private boolean isLiked;

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

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    @Override
    public String toString() {
        return "LikeResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", likesCount=" + likesCount +
                ", isLiked=" + isLiked +
                '}';
    }
}