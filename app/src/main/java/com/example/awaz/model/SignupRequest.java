package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;

public class SignupRequest {
    @SerializedName("username")
    private String username;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("district")
    private String district;

    @SerializedName("ward")
    private String ward;

    @SerializedName("city")
    private String city;

    @SerializedName("area_name")
    private String areaName;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("password_confirmation")
    private String passwordConfirmation;

    @SerializedName("citizenship_id_number")
    private String citizenshipNumber;

    @SerializedName("citizenship_front_image")
    private String citizenshipFrontImage;

    @SerializedName("citizenship_back_image")
    private String citizenshipBackImage;

    @SerializedName("agreed_to_terms")
    private boolean agreedToTerms;

    @SerializedName("gender")
    private String gender;

    @SerializedName("is_verified")
    private boolean isVerified;

    // Constructor
    public SignupRequest(String username, String firstName, String lastName, String district,
                         String ward, String city, String areaName, String phoneNumber, String email,
                         String password, String passwordConfirmation, String citizenshipNumber,
                         String citizenshipFrontImage, String citizenshipBackImage, boolean agreedToTerms,
                         String gender, boolean isVerified) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.district = district;
        this.ward = ward;
        this.city = city;
        this.areaName = areaName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.citizenshipNumber = citizenshipNumber;
        this.citizenshipFrontImage = citizenshipFrontImage;
        this.citizenshipBackImage = citizenshipBackImage;
        this.agreedToTerms = agreedToTerms;
        this.gender = gender;
        this.isVerified = isVerified;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPasswordConfirmation() { return passwordConfirmation; }
    public void setPasswordConfirmation(String passwordConfirmation) { this.passwordConfirmation = passwordConfirmation; }
    public String getCitizenshipNumber() { return citizenshipNumber; }
    public void setCitizenshipNumber(String citizenshipNumber) { this.citizenshipNumber = citizenshipNumber; }
    public String getCitizenshipFrontImage() { return citizenshipFrontImage; }
    public void setCitizenshipFrontImage(String citizenshipFrontImage) { this.citizenshipFrontImage = citizenshipFrontImage; }
    public String getCitizenshipBackImage() { return citizenshipBackImage; }
    public void setCitizenshipBackImage(String citizenshipBackImage) { this.citizenshipBackImage = citizenshipBackImage; }
    public boolean isAgreedToTerms() { return agreedToTerms; }
    public void setAgreedToTerms(boolean agreedToTerms) { this.agreedToTerms = agreedToTerms; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean isVerified) { this.isVerified = isVerified; }
}