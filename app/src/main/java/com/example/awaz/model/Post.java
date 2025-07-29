package com.example.awaz.model;

import java.io.Serializable;

public class Post implements Serializable {
    private String author;
    private String category;
    private String time;
    private String title;
    private String description;
    private int supportCount;
    private int affectedCount;
    private int notSureCount;
    private int invalidCount;
    private int fixedCount;

    public Post(String author, String category, String time, String title, String description,
                int supportCount, int affectedCount, int notSureCount, int invalidCount, int fixedCount) {
        this.author = author;
        this.category = category;
        this.time = time;
        this.title = title;
        this.description = description;
        this.supportCount = supportCount;
        this.affectedCount = affectedCount;
        this.notSureCount = notSureCount;
        this.invalidCount = invalidCount;
        this.fixedCount = fixedCount;
    }

    // Getters
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getTime() { return time; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getSupportCount() { return supportCount; }
    public int getAffectedCount() { return affectedCount; }
    public int getNotSureCount() { return notSureCount; }
    public int getInvalidCount() { return invalidCount; }
    public int getFixedCount() { return fixedCount; }
}