package com.ninegroup.weather.api.client;

import android.util.Log;

import androidx.collection.ArrayMap;

import com.ninegroup.weather.api.ApiService;
import com.ninegroup.weather.api.Datapoint;
import com.ninegroup.weather.ui.MainActivity;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatapointClient {
    private String accessToken = MainActivity.accessToken;
    private ApiService apiService = ApiClient.getClient(accessToken).create(ApiService.class);
    //private DatapointRequest body;
    public static List<Datapoint> datapointList;
    public static Boolean isDatapointRunning = true;
    public static Boolean isSuccess = false;

    public void getDatapoint(String assetId, String attributeName, String fromTimestamp, String toTimestamp) {
        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("type", "lttb");
        jsonParams.put("fromTimestamp", fromTimestamp);
        jsonParams.put("toTimestamp", toTimestamp);
        jsonParams.put("amountOfPoints", 100);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                (new JSONObject(jsonParams)).toString());

        apiService.getDatapoint(assetId, attributeName, body)
                .enqueue(new Callback<List<Datapoint>>() {
                    @Override
                    public void onResponse(Call<List<Datapoint>> call, Response<List<Datapoint>> response) {
                        if (response.isSuccessful()) {
                            datapointList = response.body();
                            if (datapointList != null && datapointList.size() != 0) {
                                System.out.println(datapointList);
                            }
                            isSuccess = true;
                            isDatapointRunning = false;
                        } else {
                            Log.e("DATAPOINT API CALL", "API call unsuccessful! Your access token maybe expired or you don't have enough permissions.");
                            isSuccess = false;
                            isDatapointRunning = false;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Datapoint>> call, Throwable t) {
                        Log.e("DATAPOINT API CALL", t.getMessage().toString());
                        isSuccess = false;
                        isDatapointRunning = false;
                    }
                });
    }
}
