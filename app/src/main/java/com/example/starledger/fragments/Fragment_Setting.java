package com.example.starledger.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.starledger.LoginActivity;
import com.example.starledger.MainData;
import com.example.starledger.R;
import com.example.starledger.SettingActivity;
import com.example.starledger.bean.User;
import com.example.starledger.sqlite.TransactionDao;
import com.example.starledger.sqlite.UserDao;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Fragment_Setting extends Fragment {

    private View settingView;
    private Context context;
    private LinearLayout ll_msg, ll_back, ll_style;
    private TextView tv_jb, tv_jf, tv_name, tv_qm, tv_pm;
    private ImageView iv_tx;
    private TransactionDao transactionDao;
    private DatabaseReference databaseReference;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingView = inflater.inflate(R.layout.module_fragment_setting, container, false);
        context = settingView.getContext();
        transactionDao = new TransactionDao(context);
        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        initView();

        iv_tx.setOnClickListener(view -> startActivity(new Intent(context, LoginActivity.class)));

        ll_msg.setOnClickListener(v -> changePassword());
        ll_back.setOnClickListener(v -> logOut());
        tv_qm.setOnClickListener(v -> setSignature());
        ll_style.setOnClickListener(view -> {
            Intent intent = new Intent(context, SettingActivity.class);
            startActivity(intent);
        });

        return settingView;
    }



    private void logOut() {
        // Log out
        SharedPreferences sp = context.getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        startActivity(new Intent(context, LoginActivity.class));
        getActivity().finish();
    }

    private void changePassword() {
        if (MainData.user == null) {
            Toast.makeText(context, "Please log in to use this feature", Toast.LENGTH_SHORT).show();
            return;
        }
        // Change password
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.twindows, null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);
        alertDialog.show();
        EditText et_pw1 = view.findViewById(R.id.et_id);
        EditText et_pw2 = view.findViewById(R.id.et_name);

        view.findViewById(R.id.button3).setOnClickListener(v -> alertDialog.dismiss());
        view.findViewById(R.id.button4).setOnClickListener(v -> {
            String pw1 = et_pw1.getText().toString().trim();
            String pw2 = et_pw2.getText().toString().trim();

            if (pw1.isEmpty() || pw2.isEmpty()) {
                Toast.makeText(context, "Please enter content", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pw1.equals(pw2)) {
                // Change password
                User user = MainData.user;
                user.setPassword(pw1);
                databaseReference.child(user.getAccount()).setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        logOut();
                    } else {
                        Toast.makeText(context, "Password change failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Passwords do not match
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }

            alertDialog.dismiss();
        });
    }

    private void setSignature() {
        if (MainData.user == null) {
            Toast.makeText(context, "Please log in to use this feature", Toast.LENGTH_SHORT).show();
            return;
        }
        // Set signature
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.qm_windows, null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setView(view);
        alertDialog.show();
        EditText et_qm = view.findViewById(R.id.et_qm);
        et_qm.setText(tv_qm.getText().toString());

        view.findViewById(R.id.button3).setOnClickListener(v -> alertDialog.dismiss());
        view.findViewById(R.id.button4).setOnClickListener(v -> {
            String qm = et_qm.getText().toString().trim();

            if (qm.isEmpty()) {
                Toast.makeText(context, "Please enter content", Toast.LENGTH_SHORT).show();
                return;
            }
            User user = MainData.user;
            user.setGxmsg(qm);
            databaseReference.child(user.getAccount()).setValue(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Signature updated successfully", Toast.LENGTH_SHORT).show();
                    tv_qm.setText(qm);
                    MainData.user = user;
                } else {
                    Toast.makeText(context, "Signature update failed", Toast.LENGTH_SHORT).show();
                }
            });

            alertDialog.dismiss();
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        ll_style = settingView.findViewById(R.id.ll_style);
        ll_msg = settingView.findViewById(R.id.ll_msg);
        ll_back = settingView.findViewById(R.id.ll_back);
        tv_jb = settingView.findViewById(R.id.tv_jb);
        tv_jf = settingView.findViewById(R.id.tv_jf);
        tv_name = settingView.findViewById(R.id.tv_name);
        tv_qm = settingView.findViewById(R.id.tv_qm);
        tv_pm = settingView.findViewById(R.id.tv_pm);
        iv_tx = settingView.findViewById(R.id.iv_tx);

        if (MainData.user != null) {
            tv_name.setText(MainData.user.getAccount());
            tv_qm.setText(MainData.user.getGxmsg().isEmpty() ? "[Please set a personal signature]" : MainData.user.getGxmsg());
            tv_jf.setText(String.valueOf(MainData.user.getGold()));

            // 获取并显示今日消费和本月消费
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

            double todayExpense = transactionDao.getTotalExpenseByDate(MainData.user.getId(), today);
            double monthExpense = transactionDao.getTotalExpenseByMonth(MainData.user.getId(), currentMonth);

            if (todayExpense==0.0)
                tv_jb.setText("0");
            else
                tv_jb.setText(String.format("%.2f", todayExpense));

            if (monthExpense==0.0)
                tv_pm.setText("0");
            else
                tv_pm.setText(String.format("%.2f", monthExpense));
        } else {
            tv_jb.setText("0");
            tv_pm.setText("0");
        }
    }
}
