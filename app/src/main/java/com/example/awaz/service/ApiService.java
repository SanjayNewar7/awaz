package com.example.awaz.service;

import com.example.awaz.model.CommentRequest;
import com.example.awaz.model.CommentResponse;
import com.example.awaz.model.CommentsResponse;
import com.example.awaz.model.IssueRequest;
import com.example.awaz.model.IssueResponse;
import com.example.awaz.model.IssuesResponse;
import com.example.awaz.model.LoginRequest;
import com.example.awaz.model.LoginResponse;
import com.example.awaz.model.PostsResponse;
import com.example.awaz.model.ReactionRequest;
import com.example.awaz.model.ReactionResponse;
import com.example.awaz.model.SignupRequest;
import com.example.awaz.model.SignupResponse;
import com.example.awaz.model.UserResponse;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("api/users")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<SignupResponse> signup(@Body SignupRequest signupRequest);

    @POST("api/user-login")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<LoginResponse> userLogin(@Body LoginRequest loginRequest);

    @POST("api/issues")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<IssueResponse> createIssue(@Body IssueRequest issueRequest);

    @GET("api/users/me")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<UserResponse> getCurrentUser();

    @PUT("api/users/me")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<UserResponse> updateUser(@Body JsonObject updateData);

    @GET("api/issues")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<IssuesResponse> getIssues();

    @POST("api/issues/{id}/react")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<ReactionResponse> addReaction(@Path("id") int issueId, @Body ReactionRequest reactionRequest);

    @POST("api/issues/{id}/comment")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<CommentResponse> addComment(@Path("id") int issueId, @Body CommentRequest commentRequest);

    @GET("api/issues/{id}/comments")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<CommentsResponse> getComments(@Path("id") int issueId);

    @GET("api/posts")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<PostsResponse> getPosts();
}