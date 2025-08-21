package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class IssuesResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("issues")
    private List<Issue> issues;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Issue> getIssues() { return issues; }
    public void setIssues(List<Issue> issues) { this.issues = issues; }
}