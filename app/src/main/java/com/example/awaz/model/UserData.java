package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;

public class UserData {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("username")
    private String username;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("district")
    private String district;

    @SerializedName("city")
    private String city;

    @SerializedName("ward")
    private int ward;

    @SerializedName("area_name")
    private String areaName;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("gender")
    private String gender;

    @SerializedName("email")
    private String email;

    @SerializedName("bio")
    private String bio;

    @SerializedName("profile_image")
    private String profileImage;

    @SerializedName("citizenship_front_image")
    private String citizenshipFrontImage;

    @SerializedName("citizenship_back_image")
    private String citizenshipBackImage;

    @SerializedName("citizenship_id_number")
    private String citizenshipIdNumber;

    @SerializedName("is_verified")
    private boolean isVerified;

    @SerializedName("likes_count")  // Added
    private int likesCount;

    @SerializedName("posts_count")  // Added
    private int postsCount;

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDistrict() { return district; }
    public int getWard() { return ward; }
    public String getCity() { return city; }
    public String getAreaName() { return areaName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getGender() { return gender; }
    public String getEmail() { return email; }
    public String getBio() { return bio; }
    public String getProfileImage() { return profileImage; }
    public String getCitizenshipFrontImage() { return citizenshipFrontImage; }
    public String getCitizenshipBackImage() { return citizenshipBackImage; }
    public String getCitizenshipIdNumber() { return citizenshipIdNumber; }
    public boolean isVerified() { return isVerified; }

    public int getLikesCount() { return likesCount; }
    public int getPostsCount() { return postsCount; }
}