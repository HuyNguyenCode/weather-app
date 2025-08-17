package com.ninegroup.weather.api.client;

import android.util.Log;

import com.ninegroup.weather.api.ApiService;
import com.ninegroup.weather.api.Token;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokenClient {
    private ApiService apiService = ApiClient.getClientNoToken().create(ApiService.class);
    private Token token;
    public static String accessToken = null;
    public static Boolean isTokenRunning = true;
    public static Boolean isSuccess = false;

    public void getToken(String username, String password) {
        apiService.getToken("openremote", username, password, "password")
                .enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    token = response.body();
                    if (token != null) {
                        Log.i("TOKEN API CALL", token.getAccessToken());
                        Log.i("TOKEN API CALL", token.getTokenType());
                        accessToken = token.getAccessToken();
                        isSuccess = true;
                        isTokenRunning = false;
                    }
                } else {
                    Log.e("TOKEN API CALL", "API call unsuccessful! Your access token maybe expired or you don't have enough permissions.");
                    isSuccess = false;
                    isTokenRunning = false;
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e("TOKEN API CALL", t.getMessage().toString());
                isSuccess = false;
                isTokenRunning = false;
            }
        });
    }
}
