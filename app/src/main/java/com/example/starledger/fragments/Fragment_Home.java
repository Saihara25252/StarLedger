package com.example.starledger.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.example.starledger.AddTransactionActivity;
import com.example.starledger.MainData;
import com.example.starledger.R;
import com.example.starledger.adapter.TransactionAdapter;
import com.example.starledger.adapter.SwipeToDeleteCallback;
import com.example.starledger.bean.Transaction;
import com.example.starledger.sqlite.TransactionDao;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Fragment_Home extends Fragment {
    private View mainView;
    private Context context;
    TextView mTextMonthDay;
    TextView mTextYear;
    TextView mTextLunar;
    TextView mTextCurrentDay;
    CalendarView mCalendarView;
    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;
    private RecyclerView listView;
    private RefreshLayout refreshLayout;
    private int isfinished = 0;
    private ArrayList<Transaction> transactionList = new ArrayList<>();
    private TransactionAdapter transactionAdapter;
    private TransactionDao transactionDao;
    private int year;
    private int month;
    private int day;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.module_fragment_main, container, false);
        context = mainView.getContext();
        transactionDao = new TransactionDao(context);

        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 获取当前日期
            LocalDate today = LocalDate.now();
            // 获取年、月、日
            year = today.getYear();
            month = today.getMonthValue();
            day = today.getDayOfMonth();
            initData(year, month, day);
        }
        initList();

        return mainView;
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        mTextMonthDay = mainView.findViewById(R.id.tv_month_day);
        listView = mainView.findViewById(R.id.recyclerView);
        mTextYear = mainView.findViewById(R.id.tv_year);
        mTextLunar = mainView.findViewById(R.id.tv_lunar);
        mRelativeTool = mainView.findViewById(R.id.rl_tool);
        mCalendarView = mainView.findViewById(R.id.calendarView);
        mTextCurrentDay = mainView.findViewById(R.id.tv_current_day);
        mCalendarLayout = mainView.findViewById(R.id.calendarLayout);
        refreshLayout = (RefreshLayout) mainView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(context));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initDataList();
                if (isfinished == 1)
                    refreshlayout.finishRefresh();
            }
        });
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.setModeBothMonthWeekView();
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                //mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        mainView.findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.setModeOnlyWeekView();
                } else {
                    mCalendarLayout.shrink();
                    mCalendarLayout.setModeOnlyWeekView();
                }
                mCalendarView.scrollToCurrent();
            }
        });

        mCalendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                //mTextLunar.setVisibility(View.VISIBLE);
                mTextYear.setVisibility(View.VISIBLE);
                mTextMonthDay.setText(getMonthName(calendar.getMonth()) + " " + calendar.getDay());
                mTextYear.setText(String.valueOf(calendar.getYear()));
                //mTextLunar.setText(calendar.getLunar());
                mYear = calendar.getYear();
                year = calendar.getYear();
                month = calendar.getMonth();
                day = calendar.getDay();
                MainData.create_time = intToDate(year, month, day);
                initData(calendar.getYear(), calendar.getMonth(), calendar.getDay());
            }
        });

        mCalendarView.setOnYearChangeListener(new CalendarView.OnYearChangeListener() {
            @Override
            public void onYearChange(int year) {
                mTextMonthDay.setText(String.valueOf(year));
            }
        });
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(getMonthName(mCalendarView.getCurMonth()) + " " + mCalendarView.getCurDay());
        //mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
    }

    private void initList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initData(year, month, day);
                        }
                    });
                    isfinished = 1;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }

    private void initDataList() {
        isfinished = 1;
    }

    protected void initData(int year, int month, int day) {

        Map<String, Calendar> map = new HashMap<>();
        final double dailyLimit = MainData.user.getLimit(); // 默认每日消费上限
        for (int i = 1; i <= getDaysInMonth(year, month); i++) {
            List<Transaction> transactions = transactionDao.getTransactionsByUserIdAndDate(MainData.user.getId(), intToDate(year, month, i));
            if (transactions.isEmpty()) {
                continue;
            }

            double totalSpent = 0.0;
            for (Transaction transaction : transactions) {
                if (transaction.getAmount() < 0) { // 只计算支出
                    totalSpent += -transaction.getAmount(); // 将支出金额取正
                }
            }

            double remainingLimit = dailyLimit - totalSpent;
            if (remainingLimit < 0) {
                remainingLimit = 0;
            }
            int percentage = (int) ((remainingLimit / dailyLimit) * 100);

            map.put(getSchemeCalendar(year, month, i, 0xFFe69138, "" + percentage).toString(),
                    getSchemeCalendar(year, month, i, 0xFFe69138, "" + percentage));
        }


        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(map);

        List<Transaction> transactionBeans = transactionDao.getTransactionsByUserIdAndDate(MainData.user.getId(), intToDate(year, month, day));

        MainData.transactionCount = transactionBeans.size();
        transactionList.clear();
        MainData.transactionBeans.clear();
        MainData.transactionBeans.addAll(transactionBeans);
        transactionList.addAll(transactionBeans);

        transactionAdapter = new TransactionAdapter(transactionList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        listView.setLayoutManager(linearLayoutManager);
        listView.setAdapter(transactionAdapter);

        transactionAdapter.setOnItemClickListener(new TransactionAdapter.ViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 列表点击
                AddTransactionActivity.transaction = transactionList.get(position);
                Intent intent = new Intent(context, AddTransactionActivity.class);
                startActivity(intent);
            }
        });
        ItemTouchHelper itemTouchHelper = getItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(listView);

    }

    private @NonNull ItemTouchHelper getItemTouchHelper() {
        SwipeToDeleteCallback swipeHandler = new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    new AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to delete this transaction?")
                            .setCancelable(false)
                            .setPositiveButton("Confirm", (dialog, which) -> {
                                try {
                                    transactionDao.deleteTransaction(transactionList.get(viewHolder.getPosition()).getId());

                                    transactionList.remove(viewHolder.getPosition());
                                    transactionAdapter.notifyItemChanged(viewHolder.getPosition());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                transactionAdapter.notifyItemChanged(viewHolder.getPosition());
                            })
                            .create().show();
                } else if (direction == ItemTouchHelper.LEFT) {
                    transactionAdapter.notifyItemChanged(viewHolder.getPosition());
                }
            }
        };

        // 添加左滑动删除触摸
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
        return itemTouchHelper;
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color); // 如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800, "假");
        calendar.addScheme(0xFF008800, "节");
        return calendar;
    }

    public static int getDaysInMonth(int year, int month) {
        YearMonth yearMonthObject;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            yearMonthObject = YearMonth.of(year, month);
            return yearMonthObject.lengthOfMonth();
        } else {
            return 0;
        }

    }

    public static String intToDate(int year, int month, int day) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 使用传入的年月日创建LocalDate对象
            LocalDate date = LocalDate.of(year, month, day);

            // 创建日期时间格式化器，指定日期格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // 使用格式化器将LocalDate对象格式化为字符串
            return date.format(formatter);
        }
        return "";
    }
    // Utility method to get month name from month number
    private String getMonthName(int month) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.time.format.TextStyle style = java.time.format.TextStyle.FULL_STANDALONE;
            java.time.Month monthEnum = java.time.Month.of(month);
            return monthEnum.getDisplayName(style, Locale.getDefault());
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.set(java.util.Calendar.MONTH, month - 1); // Calendar.MONTH is zero-based
            return dateFormat.format(calendar.getTime());
        }
    }
}
