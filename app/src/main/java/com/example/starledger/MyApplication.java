package com.example.starledger;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize global settings
        initializeGlobalSettings();
    }

    private void initializeGlobalSettings() {
        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Load automatic recording settings
        MainData.isAutoRecordSMS = sharedPreferences.getBoolean("auto_record_sms", false);
        MainData.isAutoRecordNotifications = sharedPreferences.getBoolean("auto_record_notifications", false);
    }
}
