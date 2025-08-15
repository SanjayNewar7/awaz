package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;

public class IssueRequest {
    @SerializedName("heading")
    private String heading;

    @SerializedName("description")
    private String description;

    @SerializedName("report_type")
    private String reportType;

    @SerializedName("district")
    private String district;

    @SerializedName("ward")
    private String ward;

    @SerializedName("area_name")
    private String areaName;

    @SerializedName("location")
    private String location;

    @SerializedName("photo1")
    private String photo1;

    @SerializedName("photo2")
    private String photo2;

    public IssueRequest(String heading, String description, String reportType, String district,
                        String ward, String areaName, String location, String photo1, String photo2) {
        this.heading = heading;
        this.description = description;
        this.reportType = reportType;
        this.district = district;
        this.ward = ward;
        this.areaName = areaName;
        this.location = location;
        this.photo1 = photo1;
        this.photo2 = photo2;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoto1() {
        return photo1;
    }

    public void setPhoto1(String photo1) {
        this.photo1 = photo1;
    }

    public String getPhoto2() {
        return photo2;
    }

    public void setPhoto2(String photo2) {
        this.photo2 = photo2;
    }
}