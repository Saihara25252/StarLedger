package com.example.starledger;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.starledger.bean.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    private LinearLayout ll_login, ll_register;
    private Button btn_login_register, btn_login_login, btn_register_register, btn_register_back;
    private EditText et_login_account, et_register_account, et_login_password, et_register_password1, et_register_password2, et_register_email;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Transparent status bar
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Transparent navigation bar
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        initView();
        checkAutoLogin();
        initClick();
    }

    private void initView() {
        ll_login = findViewById(R.id.login);
        ll_register = findViewById(R.id.register);
        btn_login_register = findViewById(R.id.btn_login_register);
        btn_login_login = findViewById(R.id.btn_login_login);
        btn_register_register = findViewById(R.id.btn_register_register);
        btn_register_back = findViewById(R.id.btn_register_back);

        et_login_account = findViewById(R.id.et_login_account);
        et_register_account = findViewById(R.id.et_register_account);
        et_login_password = findViewById(R.id.et_login_password);
        et_register_password1 = findViewById(R.id.et_register_password1);
        et_register_password2 = findViewById(R.id.et_register_password2);
        et_register_email = findViewById(R.id.et_register_email);
    }

    private void initClick() {
        btn_login_register.setOnClickListener(view -> {
            ll_login.setVisibility(View.GONE);
            ll_register.setVisibility(View.VISIBLE);
        });
        btn_register_back.setOnClickListener(view -> {
            ll_register.setVisibility(View.GONE);
            ll_login.setVisibility(View.VISIBLE);
        });
        btn_login_login.setOnClickListener(view -> {
            String account = et_login_account.getText().toString().trim();
            String password = et_login_password.getText().toString().trim();
            loginUser(account, password);
        });
        btn_register_register.setOnClickListener(view -> {
            String account = et_register_account.getText().toString().trim();
            String password1 = et_register_password1.getText().toString().trim();
            String password2 = et_register_password2.getText().toString().trim();
            String email = et_register_email.getText().toString().trim();
            if (account.isEmpty() || password1.isEmpty() || password2.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please enter registration information", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password1.equals(password2)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            registerUser(account, password1, email);
        });
    }

    private void loginUser(String account, String password) {
        databaseReference.child(account).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getPassword().equals(password)) {
                        MainData.user = user;
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        save(account, password);
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Account not registered", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser(String account, String password, String email) {
        databaseReference.child(account).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(LoginActivity.this, "Account already registered", Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User(0, account, password, "", 0, 0, 200);
                    databaseReference.child(account).setValue(user);
                    Toast.makeText(LoginActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    ll_register.setVisibility(View.GONE);
                    ll_login.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void save(String ac, String pw) {
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putString("ac", ac);
        editor.putString("pw", pw);
        editor.apply();
    }

    private void checkAutoLogin() {
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String savedAccount = sp.getString("ac", null);
        String savedPassword = sp.getString("pw", null);

        if (savedAccount != null && savedPassword != null) {
            loginUser(savedAccount, savedPassword);
        }
    }
}
