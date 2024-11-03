package com.example.starledger;

import static com.example.starledger.MainData.TAG_DATETIME_FRAGMENT;
import static com.example.starledger.MainData.create_time;
import static com.example.starledger.MainData.myDateFormat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.example.starledger.bean.Transaction;
import com.example.starledger.sqlite.TransactionDao;
import com.google.android.material.textfield.TextInputEditText;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class AddTransactionActivity extends AppCompatActivity {
    private SegmentedButtonGroup segmentedButtonGroup;
    private SwitchDateTimeDialogFragment dateTimeFragment;
    private TextView textView;
    private TextInputEditText et_msg;
    private TextInputEditText et_category;
    private TextInputEditText et_description;
    private int state = 0;

    public static Transaction transaction = null;
    private TransactionDao transactionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        textView = findViewById(R.id.textView);
        et_msg = findViewById(R.id.et_msg);
        et_category = findViewById(R.id.et_category);
        et_description = findViewById(R.id.et_description);
        textView.setText(create_time);

        transactionDao = new TransactionDao(this);

        if (transaction != null) {
            et_msg.setText(String.valueOf(transaction.getAmount()));
            et_category.setText(transaction.getCategory());
            et_description.setText(transaction.getDescription());
        }

        segmentedButtonGroup = findViewById(R.id.buttonGroup_lordOfTheRings);
        segmentedButtonGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(final int position) {
                state = position;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_finish) {
            String amountText = et_msg.getText().toString().trim();
            String category = et_category.getText().toString().trim();
            String description = et_description.getText().toString().trim();

            if (amountText.isEmpty() || category.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return true;
            }

            double amount = Double.parseDouble(amountText);
            if (state==0)
                amount = 0-amount;

            if (transaction != null) {
                transaction.setAmount(amount);
                transaction.setCategory(category);
                transaction.setDescription(description);
                transaction.setDate(create_time);
                transactionDao.updateTransaction(transaction);
                Toast.makeText(this, "Transaction updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Transaction newTransaction = new Transaction(MainData.user.getId(), amount, category, description, create_time);
                transactionDao.addTransaction(newTransaction);
                Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_SHORT).show();
            }

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
