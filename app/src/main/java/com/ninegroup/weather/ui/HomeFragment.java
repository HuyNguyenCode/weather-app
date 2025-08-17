package com.ninegroup.weather.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ninegroup.weather.R;
import com.ninegroup.weather.api.client.AssetClient;
import com.ninegroup.weather.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private String dateTime;
    private String sunrise;
    private String sunset;
    private Integer windSpeed;
    private Handler handler;
    private Runnable updateUI;

    private void initVariables() {
        handler = new Handler();
        updateUI = new Runnable() {
            @Override
            public void run() {
                Log.i("UpdateUI", "UpdateUI process is running");
                //Log.d("WebView","is loading: " + WebViewClient.isRunning);

                if (!AssetClient.isAssetRunning && AssetClient.isSuccess) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    Date dateObj = new java.util.Date(AssetClient.sunrise * 1000); // Multiply by 1000
                    sunrise = dateFormat.format(dateObj);
                    Date dateObj1 = new java.util.Date(AssetClient.sunset * 1000); // Multiply by 1000
                    sunset = dateFormat.format(dateObj1);

//                    ZonedDateTime dateTime = Instant.ofEpochMilli(AssetClient.sunrise).
//                            atZone(ZoneId.systemDefault());
//                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle("HH:mm").withLocale(Locale.forLanguageTag("en"));
//                    String formattedDate = dateTime.format(dateFormatter);

                    double wind = AssetClient.windSpeed * 3.6;
                    windSpeed = Math.toIntExact(Math.round(wind));
                    updateUI();
                    Log.i("UpdateUI", "UpdateUI process is stopped");
                    handler.removeCallbacks(updateUI);
                }
                else {
                    Log.i("UpdateUI", "UpdateUI process is running again");
                    handler.postDelayed(updateUI, 200);
                }
            }
        };
    }

    public void updateUI () {
        binding.place.setText(AssetClient.place);
        binding.homeTopBar.setTitle(AssetClient.place);
        binding.temperature.setText(getString(R.string.temperature_value, Math.round(AssetClient.temperature)));
        binding.todayCategorySubtitle.setText(dateTime);
        //binding.uvValue.setText(AssetClient.uvIndex);
        //binding.weatherStatusImageView.setImageDrawable();
        //binding.uvStatus.setText(assetController.uvModel.getValue());
        binding.humidityValue.setText(getString(R.string.humidity_value, AssetClient.humidity));
        binding.rainValue.setText(getString(R.string.rain_value, Math.round(AssetClient.rainfall)));
        binding.windValue.setText(getString(R.string.wind_value, windSpeed));
        binding.sunriseValue.setText(getString(R.string.sunrise_value, sunrise));
        binding.sunsetValue.setText(getString(R.string.sunset_value, sunset));
        binding.airIndex.setText(getString(R.string.aqi_value, AssetClient.AQI));
        binding.pm25Value.setText(getString(R.string.pm25_value, Math.round(AssetClient.pm25)));
        binding.pm10Value.setText(getString(R.string.pm10_value, Math.round(AssetClient.pm10)));
        binding.co2Value.setText(getString(R.string.co2_value, Math.round(AssetClient.co2)));
        binding.noValue.setText(getString(R.string.n_o_value, Math.round(AssetClient.n_o)));
        binding.no2Value.setText(getString(R.string.no2_value, Math.round(AssetClient.no2)));
        binding.o3Value.setText(getString(R.string.o3_value, Math.round(AssetClient.o3)));
        binding.so2Value.setText(getString(R.string.so2_value, Math.round(AssetClient.so2)));
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVariables();

        Calendar calendar;
        SimpleDateFormat simpleDateFormat;
        // get the Long type value of the current system date
        Long dateValueInLong = System.currentTimeMillis();

        // different format type to format the
        // current date and time of the system
        // format type 1
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("EEEE, dd LLLL yyyy");
        dateTime = simpleDateFormat.format(calendar.getTime()).toString();

        handler.postDelayed(updateUI, 200);
    }

    @Override
    public void onResume() {
        super.onResume();
//        updateUI();
        handler.postDelayed(updateUI, 200);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateUI);
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(updateUI);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
