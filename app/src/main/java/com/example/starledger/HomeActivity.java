package com.example.starledger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.starledger.adapter.SectionsPagerAdaper;
import com.example.starledger.bean.User;
import com.example.starledger.fragments.Fragment_Chart;
import com.example.starledger.fragments.Fragment_Home;
import com.example.starledger.fragments.Fragment_Setting;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private TabLayout myTab;
    private ViewPager2 myPager2;
    private List<String> titles = new ArrayList<>();
    private List<Integer> icons = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private DatabaseReference userDatabase;
    public static AlertDialog loadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase Database reference
        userDatabase = FirebaseDatabase.getInstance().getReference("users");

        getLogin();

        myTab = findViewById(R.id.tab);
        myPager2 = findViewById(R.id.viewpager2);

        // Add titles
        titles.add("Home");
        titles.add("Chart");
        titles.add("Me");

        icons.add(R.drawable.baseline_home_24);
        icons.add(R.drawable.baseline_article_24);
        icons.add(R.drawable.baseline_account_circle_24);

        // Add Fragments
        fragments.add(new Fragment_Home());
        fragments.add(new Fragment_Chart());
        fragments.add(new Fragment_Setting());

        myTab.setTabMode(TabLayout.MODE_FIXED);

        // Instantiate adapter
        SectionsPagerAdaper sectionsPagerAdapter = new SectionsPagerAdaper(getSupportFragmentManager(), getLifecycle(), fragments);

        // Set adapter
        myPager2.setAdapter(sectionsPagerAdapter);
        myPager2.setUserInputEnabled(false);

        // Associate TabLayout and ViewPager2
        new TabLayoutMediator(myTab, myPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles.get(position));
                tab.setIcon(icons.get(position));
            }
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if (id == R.id.create_notice) {
            // Execute create reminder event
            AddTransactionActivity.transaction = null;
            Intent intent = new Intent(HomeActivity.this, AddTransactionActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getLogin() {
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String account = sp.getString("ac", "");
        String password = sp.getString("pw", "");

        if (account.isEmpty() || password.isEmpty()) {
            return;
        }

        userDatabase.child(account).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getPassword().equals(password)) {
                        // Login
                        MainData.user = user;
                        // Daily points
                        SharedPreferences sp1 = getSharedPreferences("jf_", MODE_PRIVATE);
                        if (sp1.getBoolean(MainData.todayDateFormat.format(new Date()), true)) {
                            MainData.user.setGold(MainData.user.getGold() + 100);
                            userDatabase.child(account).setValue(MainData.user);
                            SharedPreferences.Editor editor = sp1.edit();
                            editor.putBoolean(MainData.todayDateFormat.format(new Date()), false);
                            editor.apply();
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Login status expired", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error loading user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
