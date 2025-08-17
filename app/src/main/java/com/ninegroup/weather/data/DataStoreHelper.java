package com.ninegroup.weather.data;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;

import java.util.Map;

import io.reactivex.rxjava3.core.Single;

public class DataStoreHelper {
    Activity activity;
    RxDataStore<Preferences> dataStoreRX;
    Preferences pref_error = new Preferences() {
        @Nullable
        @Override
        public <T> T get(@NonNull Key<T> key) {
            return null;
        }

        @Override
        public <T> boolean contains(@NonNull Key<T> key) {
            return false;
        }

        @NonNull
        @Override
        public Map<Key<?>, Object> asMap() {
            return null;
        }
    };

    public DataStoreHelper(Activity activity, RxDataStore<Preferences> dataStoreRX) {
        this.activity = activity;
        this.dataStoreRX = dataStoreRX;
    }

    public boolean putStringValue(String key, String value) {
        boolean returnValue;
        Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(key);
        Single<Preferences> updateResult = dataStoreRX.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(PREF_KEY, value);
            return Single.just(mutablePreferences);
        }).onErrorReturnItem(pref_error);
        returnValue = updateResult.blockingGet() != pref_error;
        return returnValue;
    }

    public String getStringValue(String key) {
        Preferences.Key<String> PREF_KEY = PreferencesKeys.stringKey(key);
        Single<String> value = dataStoreRX.data().firstOrError().map(prefs -> prefs.get(PREF_KEY)).onErrorReturnItem("null");
        return value.blockingGet();
    }

    public boolean putBooleanValue(String key, boolean value) {
        boolean returnValue;
        Preferences.Key<Boolean> PREF_KEY = PreferencesKeys.booleanKey(key);
//        Flowable<Boolean> exampleCounterFlow =
//                dataStoreRX.data().map(prefs -> prefs.get(PREF_KEY));
        Single<Preferences> updateResult = dataStoreRX.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            //Boolean currentBool = prefsIn.get(PREF_KEY);
            mutablePreferences.set(PREF_KEY, value);
            return Single.just(mutablePreferences);
        }).onErrorReturnItem(pref_error);
        returnValue = updateResult.blockingGet() != pref_error;
        return returnValue;
    }

    public Boolean getBooleanValue(String key) {
        Preferences.Key<Boolean> PREF_KEY = PreferencesKeys.booleanKey(key);
        Single<Boolean> value = dataStoreRX.data().firstOrError().map(prefs -> prefs.get(PREF_KEY)).onErrorReturnItem(null);
        return value.blockingGet();
    }
}
