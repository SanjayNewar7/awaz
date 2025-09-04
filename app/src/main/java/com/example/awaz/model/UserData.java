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

    @SerializedName("is_liked")
    private boolean isLiked;

    @SerializedName("likes_count")
    private int likesCount;

    @SerializedName("posts_count")
    private int postsCount;

    @SerializedName("verification_status")
    private String verificationStatus;



    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public int getWard() { return ward; }
    public void setWard(int ward) { this.ward = ward; }
    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
    public int getPostsCount() { return postsCount; }
    public void setPostsCount(int postsCount) { this.postsCount = postsCount; }
    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
    // Added missing getter methods
    public String getCitizenshipFrontImage() { return citizenshipFrontImage; }
    public void setCitizenshipFrontImage(String citizenshipFrontImage) { this.citizenshipFrontImage = citizenshipFrontImage; }

    public String getCitizenshipBackImage() { return citizenshipBackImage; }
    public void setCitizenshipBackImage(String citizenshipBackImage) { this.citizenshipBackImage = citizenshipBackImage; }

    public String getCitizenshipIdNumber() { return citizenshipIdNumber; }
    public void setCitizenshipIdNumber(String citizenshipIdNumber) { this.citizenshipIdNumber = citizenshipIdNumber; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { this.isVerified = verified; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }



    @Override
    public String toString() {
        return "UserData{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", district='" + district + '\'' +
                ", city='" + city + '\'' +
                ", ward=" + ward +
                ", areaName='" + areaName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", bio='" + bio + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", citizenshipFrontImage='" + citizenshipFrontImage + '\'' +
                ", citizenshipBackImage='" + citizenshipBackImage + '\'' +
                ", citizenshipIdNumber='" + citizenshipIdNumber + '\'' +
                ", isVerified=" + isVerified +
                ", verificationStatus='" + verificationStatus + '\'' +
                ", postsCount=" + postsCount +
                ", likesCount=" + likesCount +
                ", isLiked=" + isLiked +

                '}';
    }
}