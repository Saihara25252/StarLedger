package com.example.starledger;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.starledger.bean.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingActivity extends AppCompatActivity {

    private EditText etSpendingLimit;
    private Button btnSaveLimit;
    private Switch switchAutoRecordSMS;
    private Switch switchAutoRecordNotifications;

    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize UI elements
        etSpendingLimit = findViewById(R.id.et_spending_limit);
        btnSaveLimit = findViewById(R.id.btn_save_limit);
        switchAutoRecordSMS = findViewById(R.id.switch_auto_record_sms);
        switchAutoRecordNotifications = findViewById(R.id.switch_auto_record_notifications);

        // Load current settings
        loadSettings();

        btnSaveLimit.setOnClickListener(view -> saveSpendingLimit());

        switchAutoRecordSMS.setOnCheckedChangeListener((buttonView, isChecked) -> saveSwitchState("auto_record_sms", isChecked));
        switchAutoRecordNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> saveSwitchState("auto_record_notifications", isChecked));
    }

    private void loadSettings() {
        User user = MainData.user;
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        etSpendingLimit.setText(String.valueOf(user.getLimit()));
        switchAutoRecordSMS.setChecked(sharedPreferences.getBoolean("auto_record_sms", false));
        switchAutoRecordNotifications.setChecked(sharedPreferences.getBoolean("auto_record_notifications", false));
    }

    private void saveSpendingLimit() {
        String limitStr = etSpendingLimit.getText().toString().trim();
        if (limitStr.isEmpty()) {
            Toast.makeText(this, "Please enter a spending limit", Toast.LENGTH_SHORT).show();
            return;
        }

        int limit = Integer.parseInt(limitStr);
        User user = MainData.user;
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        user.setLimit(limit);

        // Update limit in Firebase
        databaseReference.child(user.getAccount()).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Spending limit updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update spending limit", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSwitchState(String key, boolean state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, state);
        editor.apply();
        // Update MainData with new switch states
        if (key.equals("auto_record_sms")) {
            MainData.isAutoRecordSMS = state;
        } else if (key.equals("auto_record_notifications")) {
            MainData.isAutoRecordNotifications = state;
        }
    }
}
