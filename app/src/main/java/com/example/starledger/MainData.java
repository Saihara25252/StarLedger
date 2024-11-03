package com.example.starledger;


import com.example.starledger.bean.Transaction;
import com.example.starledger.bean.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainData {
    public static final SimpleDateFormat todayDateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
    public static final SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
    public static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
    public static String create_time=todayDateFormat.format(new Date());
    public static User user;

    public static int transactionCount;
    public static List<Transaction> transactionBeans = new ArrayList<>();
    // New static variables to hold auto-recording settings
    public static boolean isAutoRecordSMS = false;
    public static boolean isAutoRecordNotifications = false;
}
