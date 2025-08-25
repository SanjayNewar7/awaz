package com.example.awaz.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.awaz.model.CommentRequest;
import com.example.awaz.model.CommentResponse;
import com.example.awaz.model.CommentsResponse;
import com.example.awaz.model.IssueRequest;
import com.example.awaz.model.IssueResponse;
import com.example.awaz.model.IssuesResponse;
import com.example.awaz.model.LikeResponse;
import com.example.awaz.model.LoginRequest;
import com.example.awaz.model.LoginResponse;
import com.example.awaz.model.NotificationResponse;
import com.example.awaz.model.Post;
import com.example.awaz.model.PostsResponse;
import com.example.awaz.model.ReactionRequest;
import com.example.awaz.model.ReactionResponse;
import com.example.awaz.model.SignupRequest;
import com.example.awaz.model.SignupResponse;
import com.example.awaz.model.UserResponse;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private static final String BASE_URL = "http://192.168.1.70:8000/"; // Updated BASE_URL
    private static Retrofit retrofit = null;
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String PREF_NOTIFICATION = "NotificationPrefs";
    private static final String KEY_CHECKED_NOTIFICATIONS = "checked_notifications";

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static ApiService getApiService(Context context) {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                String token = getAccessToken(context);

                Log.d(TAG, "Using token: " + (token != null ? "****" + token.substring(Math.max(0, token.length() - 4)) : "NULL"));

                Request.Builder requestBuilder = original.newBuilder();
                if (token != null) {
                    requestBuilder.header("Authorization", "Bearer " + token);
                }
                requestBuilder.header("Content-Type", "application/json")
                        .header("Accept", "application/json");
                Request request = requestBuilder.build();
                return chain.proceed(request);
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    public static void setAccessToken(String accessToken, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply();
        Log.d(TAG, "Access token stored");
    }

    public static String getAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(KEY_ACCESS_TOKEN, null);
        Log.d(TAG, "Retrieved token: " + (token != null ? "****" + token.substring(Math.max(0, token.length() - 4)) : "NULL"));
        return token;
    }

    public static void clearAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_ACCESS_TOKEN).apply();
        SharedPreferences notificationPrefs = context.getSharedPreferences(PREF_NOTIFICATION, Context.MODE_PRIVATE);
        notificationPrefs.edit().remove(KEY_CHECKED_NOTIFICATIONS).apply();
        Log.d(TAG, "Access token and checked notifications cleared");
    }

    public interface ApiService {
        @GET("users/{userId}")
        @Headers({"Content-Type: application/json", "Accept: application/json"})
        Call<UserResponse> getUser(@Path("userId") int userId);

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

        @POST("api/users/{userId}/like")
        Call<LikeResponse> toggleLike(@Path("userId") int userId);

        @GET("api/notifications")
        Call<NotificationResponse> getNotifications(@Header("Authorization") String authToken);

        @GET("api/posts/{id}")
        Call<Post> getPostById(@Path("id") int postId); // Renamed parameter to postId for clarity

        @GET("api/posts/by_issue/{issue_id}")
        Call<Post> getPostByIssueId(@Path("issue_id") long issueId); // New endpoint

        @POST("api/notifications/{id}/read")
        Call<Void> markNotificationAsRead(@Path("id") long notificationId);

        @POST("api/notifications/read-all")
        Call<Void> markAllNotificationsAsRead();


    }
}