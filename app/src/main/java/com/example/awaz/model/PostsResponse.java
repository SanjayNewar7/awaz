package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PostsResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("posts")
    private List<Post> posts;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }
}