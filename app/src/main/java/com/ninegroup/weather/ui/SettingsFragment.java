package com.ninegroup.weather.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.ninegroup.weather.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if (key.equals("language")) {
            String lang = sharedPreferences.getString(key, "");
            Log.i("SharedPreferences", "Preference value was updated to: " + lang);
            switch (lang) {
                case "en":
                    MainActivity.changeLanguage("en");
                    break;
                case "vi":
                    MainActivity.changeLanguage("vi");
                    break;
                case "zh":
                    MainActivity.changeLanguage("zh");
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
