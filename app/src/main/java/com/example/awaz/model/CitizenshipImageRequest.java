package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;

public class CitizenshipImageRequest {
    @SerializedName("citizenship_front_image")
    private String citizenshipFrontImage;

    @SerializedName("citizenship_back_image")
    private String citizenshipBackImage;

    public CitizenshipImageRequest(String front, String back) {
        this.citizenshipFrontImage = front;
        this.citizenshipBackImage = back;
    }
}
