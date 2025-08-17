package com.ninegroup.weather.api.client;

import android.util.Log;

import com.ninegroup.weather.api.ApiService;
import com.ninegroup.weather.api.User;
import com.ninegroup.weather.ui.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserClient {
    private final String accessToken = MainActivity.accessToken;
    private final ApiService apiService = ApiClient.getClient(accessToken).create(ApiService.class);
    private User user;
    public static Boolean isUserRunning = true;
    public static Boolean isSuccess = false;

    public void getUser() {
        apiService.getUser(accessToken).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    if (user != null) {
                        Log.i("USER API CALL", user.id);
                        Log.i("USER API CALL", user.username);
                        Log.i("USER API CALL", user.firstName);
                        Log.i("USER API CALL", user.lastName);
//                        Gson gson = new Gson();
//                        String json = gson.toJson(asset.attributes);
//                        handleAssetResponse(json);
                        isSuccess = true;
                        isUserRunning = false;
                    }
                } else {
                    Log.e("USER API CALL", "API call unsuccessful! Your access token maybe expired or you don't have enough permissions.");
                    isSuccess = false;
                    isUserRunning = false;
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("TOKEN API CALL", t.getMessage().toString());
                isSuccess = false;
                isUserRunning = false;
            }
        });
    }
}
