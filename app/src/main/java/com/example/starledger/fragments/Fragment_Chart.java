package com.example.starledger.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.starledger.MainData;
import com.example.starledger.R;
import com.example.starledger.sqlite.TransactionDao;
import com.example.starledger.widget.ZeroValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Fragment_Chart extends Fragment {

    private View ChartView;
    private Context context;
    private BarChart barChart;
    private PieChart pieChart;
    private TransactionDao transactionDao;
    private Spinner yearSpinner;
    private Spinner typeSpinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ChartView = inflater.inflate(R.layout.module_fragment_chart, container, false);
        context = ChartView.getContext();

        transactionDao = new TransactionDao(context);
        barChart = ChartView.findViewById(R.id.barChart);
        pieChart = ChartView.findViewById(R.id.pieChart);
        yearSpinner = ChartView.findViewById(R.id.yearSpinner);
        typeSpinner = ChartView.findViewById(R.id.typeSpinner);

        initYearSpinner();
        initTypeSpinner();
        initData(Calendar.getInstance().get(Calendar.YEAR)); // Initialize with the current year

        return ChartView;
    }

    private void initYearSpinner() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> years = new ArrayList<>();
        for (int i = currentYear; i >= 2000; i--) {
            years.add(String.valueOf(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedYear = Integer.parseInt(years.get(position));
                initData(selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void initTypeSpinner() {
        List<String> types = new ArrayList<>();
        types.add("Today's Income");
        types.add("Today's Expense");
        types.add("This Month's Income");
        types.add("This Month's Expense");
        types.add("This Year's Income");
        types.add("This Year's Expense");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = types.get(position);
                updatePieChart(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void initData(int year) {
        List<BarEntry> expenseEntries = new ArrayList<>();
        List<BarEntry> incomeEntries = new ArrayList<>();

        for (int month = 0; month < 12; month++) { // Use 0-11 for months to correctly align with bar chart
            double totalSpent = transactionDao.getTotalSpentByMonth(year, month + 1, MainData.user.getId());
            double totalIncome = transactionDao.getTotalIncomeByMonth(year, month + 1, MainData.user.getId());

            expenseEntries.add(new BarEntry(month, (float) totalSpent));
            incomeEntries.add(new BarEntry(month, (float) totalIncome));
        }

        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expenses");
        expenseDataSet.setColor(0xFFe69138);

        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(0xFF4CAF50);

        BarData barData = new BarData(expenseDataSet, incomeDataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);

        float groupSpace = 0.2f;
        float barSpace = 0.05f;
        float barWidth = 0.35f;
        barData.setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinimum(1);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * 12);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.setDrawValueAboveBar(true); // 确保值在柱状图上方显示
        barChart.getBarData().setValueFormatter(new ZeroValueFormatter());


        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getMonths()));
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setGranularityEnabled(true);

        barChart.getAxisRight().setEnabled(false);
        barChart.invalidate(); // refresh
    }

    private void updatePieChart(String type) {
        List<PieEntry> entries = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String today = dateFormat.format(new Date());
        String currentMonth = monthFormat.format(new Date());
        String currentYear = yearFormat.format(new Date());

        switch (type) {
            case "Today's Income":
                entries = transactionDao.getIncomeCategoryByDate(MainData.user.getId(), today);
                break;
            case "Today's Expense":
                entries = transactionDao.getExpenseCategoryByDate(MainData.user.getId(), today);
                break;
            case "This Month's Income":
                entries = transactionDao.getIncomeCategoryByMonth(MainData.user.getId(), currentMonth);
                break;
            case "This Month's Expense":
                entries = transactionDao.getExpenseCategoryByMonth(MainData.user.getId(), currentMonth);
                break;
            case "This Year's Income":
                entries = transactionDao.getIncomeCategoryByYear(MainData.user.getId(), currentYear);
                break;
            case "This Year's Expense":
                entries = transactionDao.getExpenseCategoryByYear(MainData.user.getId(), currentYear);
                break;
        }

        PieDataSet dataSet = new PieDataSet(entries, type);
        dataSet.setColors(getColors(entries.size())); // Set different colors for each category
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate(); // refresh
    }

    private List<Integer> getColors(int size) {
        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            colors.add(Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
        }
        return colors;
    }

    private List<String> getMonths() {
        List<String> months = new ArrayList<>();
        months.add("Jan");
        months.add("Feb");
        months.add("Mar");
        months.add("Apr");
        months.add("May");
        months.add("Jun");
        months.add("Jul");
        months.add("Aug");
        months.add("Sep");
        months.add("Oct");
        months.add("Nov");
        months.add("Dec");
        return months;
    }
}
