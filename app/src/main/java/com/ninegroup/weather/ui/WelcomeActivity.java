package com.ninegroup.weather.ui;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.ninegroup.weather.R;
import com.ninegroup.weather.data.DataStoreHelper;
import com.ninegroup.weather.data.DataStoreSingleton;
import com.ninegroup.weather.databinding.ActivityWelcomeBinding;
import com.ninegroup.weather.network.ConnectionReceiver;

public class WelcomeActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {
    RxDataStore<Preferences> dataStoreRX;
    public static DataStoreHelper dataStoreHelper;
    private final static String TAG_STORE_NAME = "settings";
    private ActivityWelcomeBinding binding;
    public static boolean isConnected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);

        DataStoreSingleton dataStoreSingleton = DataStoreSingleton.getInstance();
        if (dataStoreSingleton.getDataStore() == null) {
            dataStoreRX = new RxPreferenceDataStoreBuilder(this, TAG_STORE_NAME).build();
        } else {
            dataStoreRX = dataStoreSingleton.getDataStore();
        }
        dataStoreSingleton.setDataStore(dataStoreRX);
        dataStoreHelper = new DataStoreHelper(this, dataStoreRX);

        //dataStoreHelper.putBooleanValue("remember_login", true);
        //Log.i("remember login", "status: " + dataStoreHelper.getBooleanValue("remember_login"));

        super.onCreate(savedInstanceState);
        // Set the layout file as the content view.
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        checkConnection();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_welcome);
        NavController navController = navHostFragment.getNavController();
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
}
