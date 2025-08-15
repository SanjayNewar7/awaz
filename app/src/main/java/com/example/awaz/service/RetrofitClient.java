package com.example.awaz.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.awaz.model.IssueRequest;
import com.example.awaz.model.IssueResponse;
import com.example.awaz.model.LoginRequest;
import com.example.awaz.model.LoginResponse;
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
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private static final String BASE_URL = "http://192.168.1.70:8000/";
    private static Retrofit retrofit = null;
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    public static ApiService getApiService(Context context) {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                String token = getAccessToken(context);

                Log.d(TAG, "Using token: " + (token != null ? "****" + token.substring(Math.max(0, token.length() - 4)) : "NULL"));

                // In RetrofitClient.java, ensure you're sending the token correctly:
                Request.Builder requestBuilder = original.newBuilder();
                if (token != null) {
                    // Sanctum works with both formats, but standard is "Bearer [token]"
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
        Log.d(TAG, "Access token cleared");
    }

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
    }
}