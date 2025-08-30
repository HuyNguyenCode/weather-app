package com.ninegroup.weather.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ninegroup.weather.R;
import com.ninegroup.weather.api.client.AssetClient;
import com.ninegroup.weather.data.DataStoreHelper;
import com.ninegroup.weather.data.DataStoreSingleton;
import com.ninegroup.weather.databinding.ActivityMainBinding;
import com.ninegroup.weather.network.ConnectionReceiver;

public class MainActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {
    RxDataStore<Preferences> dataStoreRX;
    public static DataStoreHelper dataStoreHelper;
    private ActivityMainBinding binding;
    private static final String TAG_STORE_NAME = "settings";
    private String isRemember;
    public static boolean isConnected;
    public static String accessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);

        dataStoreRX = new RxPreferenceDataStoreBuilder(this, TAG_STORE_NAME).build();
        DataStoreSingleton dataStoreSingleton = DataStoreSingleton.getInstance();
        if (dataStoreSingleton.getDataStore() == null) {
            dataStoreRX = new RxPreferenceDataStoreBuilder(this, TAG_STORE_NAME).build();
        } else {
            dataStoreRX = dataStoreSingleton.getDataStore();
        }
        dataStoreSingleton.setDataStore(dataStoreRX);

        dataStoreHelper = new DataStoreHelper(this, dataStoreRX);

        super.onCreate(savedInstanceState);
        // Set the layout file as the content view.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        accessToken = dataStoreHelper.getStringValue("access_token");
        Log.i("Access Token", accessToken);

        isRemember = dataStoreHelper.getStringValue("remember_login");
        if (isRemember.equals("0"))
            dataStoreHelper.putStringValue("access_token", null);

//        if (accessToken == null || accessToken.equals("null")) {
//            Log.i("Main Activity", "Started WelcomeActivity");
//            HomeFragment.handler.removeCallbacks(HomeFragment.updateUI);
//            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
//        }
        else {
            Log.i("Main Activity", "Getting Asset information");
            AssetClient assetClient = new AssetClient();
            assetClient.getAsset();
            assetClient.getExtraAsset();
            assetClient.getAirQualityAsset();

            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            NavController navController = navHostFragment.getNavController();
            AppBarConfiguration appBarConfiguration =
                    new AppBarConfiguration.Builder(R.id.homeFragment, R.id.monitoringFragment, R.id.mapFragment, R.id.userInfoFragment).build();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            NavigationUI.setupWithNavController(bottomNav, navController);
        }
    }

    public void checkConnection() {

        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();

        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");

        // register receiver
        registerReceiver(new ConnectionReceiver(), intentFilter, Context.RECEIVER_NOT_EXPORTED);

        // Initialize listener
        ConnectionReceiver.Listener = this;

        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onNetworkChange(boolean isConnected) {

    }

    static void changeLanguage(String lang) {
        // Implement your app language change logic here
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(lang));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
