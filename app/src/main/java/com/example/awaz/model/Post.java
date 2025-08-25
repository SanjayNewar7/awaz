package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;

public class Post implements java.io.Serializable {
    @SerializedName("post_id")
    private int postId;

    @SerializedName("issue_id")
    private long issueId; // Changed to long

    @SerializedName("user_id")
    private int userId;

    @SerializedName("username")
    private String username;

    @SerializedName("profile_image")
    private String profileImage;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private String category;

    @SerializedName("image1")
    private String image1;

    @SerializedName("image2")
    private String image2;

    @SerializedName("support_count")
    private int supportCount;

    @SerializedName("affected_count")
    private int affectedCount;

    @SerializedName("not_sure_count")
    private int notSureCount;

    @SerializedName("invalid_count")
    private int invalidCount;

    @SerializedName("fixed_count")
    private int fixedCount;

    @SerializedName("comment_count")
    private int commentCount;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("district")
    private String district;

    @SerializedName("region_type")
    private String regionType;

    @SerializedName("ward")
    private int ward;

    // Getters and setters
    public int getId() { return postId; }
    public void setId(int postId) { this.postId = postId; }
    public int getIssueId() { return (int) issueId; } // Changed to long
    public void setIssueId(long issueId) { this.issueId = issueId; } // Changed to long
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImage1() { return image1; }
    public void setImage1(String image1) { this.image1 = image1; }
    public String getImage2() { return image2; }
    public void setImage2(String image2) { this.image2 = image2; }
    public int getSupportCount() { return supportCount; }
    public void setSupportCount(int supportCount) { this.supportCount = supportCount; }
    public int getAffectedCount() { return affectedCount; }
    public void setAffectedCount(int affectedCount) { this.affectedCount = affectedCount; }
    public int getNotSureCount() { return notSureCount; }
    public void setNotSureCount(int notSureCount) { this.notSureCount = notSureCount; }
    public int getInvalidCount() { return invalidCount; }
    public void setInvalidCount(int invalidCount) { this.invalidCount = invalidCount; }
    public int getFixedCount() { return fixedCount; }
    public void setFixedCount(int fixedCount) { this.fixedCount = fixedCount; }
    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getRegionType() { return regionType; }
    public void setRegionType(String regionType) { this.regionType = regionType; }
    public int getWard() { return ward; }
    public void setWard(int ward) { this.ward = ward; }
}