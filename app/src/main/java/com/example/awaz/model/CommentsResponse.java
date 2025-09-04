package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CommentsResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("comments")
    private List<CommentResponse.Comment> comments;

    @SerializedName("message")
    private String message; // Add this field

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<CommentResponse.Comment> getComments() { return comments; }
    public void setComments(List<CommentResponse.Comment> comments) { this.comments = comments; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}