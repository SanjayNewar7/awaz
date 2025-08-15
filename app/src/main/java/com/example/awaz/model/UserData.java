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
    @SerializedName("email")
    private String email;
    @SerializedName("bio")
    private String bio;

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDistrict() {
        return district;
    }

    public String getCity() {
        return city;
    }

    public int getWard() {
        return ward;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio != null ? bio : "Hello, Namaste everyone";
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setWard(int ward) {
        this.ward = ward;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setBio(String bio) {
        this.bio = bio;
    }
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
                '}';
    }
}