package com.example.awaz.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ReactionResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("reaction_counts")
    private Map<String, ReactionCount> reactionCounts;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Map<String, ReactionCount> getReactionCounts() { return reactionCounts; }
    public void setReactionCounts(Map<String, ReactionCount> reactionCounts) { this.reactionCounts = reactionCounts; }

    public static class ReactionCount {
        @SerializedName("count")
        private int count;

        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
    }
}