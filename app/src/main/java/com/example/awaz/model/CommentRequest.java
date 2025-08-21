package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;

public class CommentRequest {
    @SerializedName("comment")
    private String comment;

    @SerializedName("image")
    private String image;

    public CommentRequest(String comment, String image) {
        this.comment = comment;
        this.image = image;
    }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}