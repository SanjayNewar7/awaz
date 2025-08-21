package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;

public class ReactionRequest {
    @SerializedName("reaction_type")
    private String reactionType;

    public ReactionRequest(String reactionType) {
        this.reactionType = reactionType;
    }

    public String getReactionType() { return reactionType; }
    public void setReactionType(String reactionType) { this.reactionType = reactionType; }
}