package com.example.awaz.service;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import com.example.awaz.model.SignupRequest;
import com.example.awaz.model.SignupResponse;

public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.1.70:8000/";
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    public interface ApiService {
        @POST("api/users") // Full endpoint is now api/users
        @Headers({"Content-Type: application/json", "Accept: application/json"})
        Call<SignupResponse> signup(@Body SignupRequest signupRequest);
    }
}