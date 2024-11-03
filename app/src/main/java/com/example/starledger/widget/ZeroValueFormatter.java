package com.example.starledger.widget;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class ZeroValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        // 如果值为0，不显示任何内容
        if (value == 0) {
            return "";
        }
        // 否则，显示实际的值
        return super.getFormattedValue(value);
    }
}
