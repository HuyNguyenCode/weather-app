package com.ninegroup.weather.api.client;

import android.util.Log;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.ninegroup.weather.api.ApiService;
import com.ninegroup.weather.api.Asset;
import com.ninegroup.weather.ui.MainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetClient {
    private final String accessToken = MainActivity.accessToken;
    private final ApiService apiService = ApiClient.getClient(accessToken).create(ApiService.class);
    private Asset asset;
    private Asset extraAsset;
    public static Integer weatherId;
    public static Double rainfall; // mm
    public static String manufacturer;
    public static String uvIndex;
    public static Double temperature; // celsius
    public static Double maxTemp;
    public static Double minTemp;
    public static Double feelsLike;
    public static Integer humidity; // percent
    public static Integer pressure; // hPa
    public static Double latitude;
    public static Double longitude;
    public static String place;
    public static Integer windDirection; // degree
    public static Double windSpeed; // meter/sec
    public static Integer clouds; // percent
    public static Long dt;
    public static Long sunrise;
    public static Long sunset;
    public static Integer visibility; // meter, max 10000
    public static Integer AQI;
    public static Integer AQIPredict;
    public static Double pm10;
    public static Double pm25;
    public static Double co2;
    public static Double n_o;
    public static Double no2;
    public static Double o3;
    public static Double so2;
    public static Boolean isAssetRunning = true;
    public static Boolean isSuccess = false;

    public void getAsset() {
        apiService.getAsset("5zI6XqkQVSfdgOrZ1MyWEf").enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                if (response.isSuccessful()) {
                    asset = response.body();
                    if (asset != null) {
                        Log.i("ASSET API CALL", asset.type);
                        Log.i("ASSET API CALL", asset.attributes.toString());
                        Gson gson = new Gson();
                        String json = gson.toJson(asset.attributes);
                        handleAssetResponse(json);
                        isSuccess = true;
                        isAssetRunning = false;
                    }
                } else {
                    Log.e("ASSET API CALL", "API call unsuccessful! Your access token maybe expired or you don't have enough permissions.");
                    isSuccess = false;
                    isAssetRunning = false;
                }
            }

            @Override
            public void onFailure(Call<Asset> call, Throwable t) {
                Log.e("ASSET API CALL", t.getMessage().toString());
                isSuccess = false;
                isAssetRunning = false;
            }
        });
    }

    public void getExtraAsset() {
        apiService.getAsset("4EqQeQ0L4YNWNNTzvTOqjy").enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                if (response.isSuccessful()) {
                    extraAsset = response.body();
                    if (extraAsset != null) {
                        Gson gson = new Gson();
                        String json = gson.toJson(extraAsset.attributes);
                        handleExtraAssetResponse(json);
                        isSuccess = true;
                        isAssetRunning = false;
                    }
                } else {
                    Log.e("EXTRA ASSET API CALL", "API call unsuccessful! Your access token maybe expired or you don't have enough permissions.");
                    isSuccess = false;
                    isAssetRunning = false;
                }
            }

            @Override
            public void onFailure(Call<Asset> call, Throwable t) {
                Log.e("EXTRA ASSET API CALL", t.getMessage().toString());
                isSuccess = false;
                isAssetRunning = false;
            }
        });
    }

    public void getAirQualityAsset() {
        apiService.getAsset("6Wo9Lv1Oa1zQleuRVfADP4").enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                if (response.isSuccessful()) {
                    extraAsset = response.body();
                    if (extraAsset != null) {
                        Gson gson = new Gson();
                        String json = gson.toJson(extraAsset.attributes);
                        handleAirQualityResponse(json);
                        isSuccess = true;
                        isAssetRunning = false;
                    }
                } else {
                    Log.e("EXTRA ASSET API CALL", "API call unsuccessful! Your access token maybe expired or you don't have enough permissions.");
                    isSuccess = false;
                    isAssetRunning = false;
                }
            }

            @Override
            public void onFailure(Call<Asset> call, Throwable t) {
                Log.e("EXTRA ASSET API CALL", t.getMessage().toString());
                isSuccess = false;
                isAssetRunning = false;
            }
        });
    }

    public void handleAssetResponse(String data) {
        rainfall = JsonPath.read(data, "$.rainfall.value");
        //uvIndex = JsonPath.read(data, "$.uVIndex.value").toString();
        manufacturer = JsonPath.read(data, "$.manufacturer.value").toString();
        temperature = JsonPath.read(data, "$.temperature.value");
        humidity = ((Double) JsonPath.read(data, "$.humidity.value")).intValue();
        latitude = JsonPath.read(data, "$.location.value.coordinates[0]");
        longitude = JsonPath.read(data, "$.location.value.coordinates[1]");
        place = JsonPath.read(data, "$.place.value").toString();
        windDirection = ((Double) JsonPath.read(data, "$.windDirection.value")).intValue();
        windSpeed = JsonPath.read(data, "$.windSpeed.value");

        Log.i("ASSET API CALL", "rainfall: " + rainfall);
        //Log.i("ASSET API CALL", "uVIndex: " + uvIndex);
        Log.i("ASSET API CALL", "manufacturer: " + manufacturer);
        Log.i("ASSET API CALL", "temperature: " + temperature);
        Log.i("ASSET API CALL", "humidity: " + humidity);
        //Log.i("ASSET API CALL", "location: " + location);
        Log.i("ASSET API CALL", "place: " + place);
        Log.i("ASSET API CALL", "latitude: " + latitude);
        Log.i("ASSET API CALL", "longitude: " + longitude);
        Log.i("ASSET API CALL", "windDirection: " + windDirection);
        Log.i("ASSET API CALL", "windSpeed: " + windSpeed);
    }

    public void handleExtraAssetResponse(String data) {
        pressure = ((Double) JsonPath.read(data, "$.data.value.main.pressure")).intValue();
        maxTemp = JsonPath.read(data, "$.data.value.main.temp_max");
        minTemp = JsonPath.read(data, "$.data.value.main.temp_min");
        feelsLike = JsonPath.read(data, "$.data.value.main.feels_like");
        weatherId = ((Double) JsonPath.read(data, "$.data.value.weather[0].id")).intValue();
        dt = ((Double) JsonPath.read(data, "$.data.value.dt")).longValue();
        sunrise = ((Double) JsonPath.read(data, "$.data.value.sys.sunrise")).longValue();
        sunset = ((Double) JsonPath.read(data, "$.data.value.sys.sunset")).longValue();
        clouds = ((Double) JsonPath.read(data, "$.data.value.clouds.all")).intValue();
        //latitude = JsonPath.read(data, "$.data.value.coord.lat");
        //longitude = JsonPath.read(data, "$.data.value.coord.lon");
        visibility = ((Double) JsonPath.read(data, "$.data.value.visibility")).intValue();

        Log.i("EXTRA ASSET", "pressure: " + pressure);
        Log.i("EXTRA ASSET", "maxTemp: " + maxTemp);
        Log.i("EXTRA ASSET", "minTemp: " + minTemp);
        Log.i("EXTRA ASSET", "feelsLike: " + feelsLike);
        Log.i("EXTRA ASSET", "weatherId: " + weatherId);
        //Log.i("EXTRA ASSET", "weatherId: " + weatherId);
        Log.i("EXTRA ASSET", "dateTime: " + dt);
        Log.i("EXTRA ASSET", "pressure: " + pressure);
        Log.i("EXTRA ASSET", "sunrise: " + sunrise);
        Log.i("EXTRA ASSET", "sunset: " + sunset);
        Log.i("EXTRA ASSET", "clouds: " + clouds);
        //Log.i("EXTRA ASSET", "lat: " + latitude);
        //Log.i("EXTRA ASSET", "long: " + longitude);
        Log.i("EXTRA ASSET", "visibility: " + visibility);
    }

    public void handleAirQualityResponse (String data) {
        AQI = ((Double) JsonPath.read(data, "$.AQI.value")).intValue();
        AQIPredict = ((Double) JsonPath.read(data, "$.AQI_predict.value")).intValue();
        pm10 = JsonPath.read(data, "$.PM10.value");
        pm25 = JsonPath.read(data, "$.PM25.value");
        co2 = JsonPath.read(data, "$.CO2.value");
        n_o = JsonPath.read(data, "$.NO.value");
        no2 = JsonPath.read(data, "$.NO2.value");
        o3 = JsonPath.read(data, "$.O3.value");
        so2 = JsonPath.read(data, "$.SO2.value");

        Log.i("AIR QUALITY", "AQI: " + AQI);
        Log.i("AIR QUALITY", "AQI Predict: " + AQIPredict);
        Log.i("AIR QUALITY", "PM10: " + pm10);
        Log.i("AIR QUALITY", "PM2.5: " + pm25);
        Log.i("AIR QUALITY", "CO2: " + co2);
        Log.i("AIR QUALITY", "NO: " + n_o);
        Log.i("AIR QUALITY", "NO2: " + no2);
        Log.i("AIR QUALITY", "O3: " + o3);
        Log.i("AIR QUALITY", "SO2: " + so2);
    }
}
