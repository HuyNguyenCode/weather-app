package com.ninegroup.weather.ui;

import com.github.mikephil.charting.components.AxisBase;

import java.text.DecimalFormat;

public class XAxisValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {
    private final DecimalFormat format = new DecimalFormat("###,##0.0");

    // Override for LineChart or ScatterChart
//    @Override
//    public String getPointLabel(Entry entry) {
//        if (entry == null) {
//            return "";
//        }
//        return format.format(entry.getY());
//    }
//
//    // Override for BarChart
//    @Override
//    public String getBarLabel(BarEntry barEntry) {
//        if (barEntry == null) {
//            return "";
//        }
//        return format.format(barEntry.getY());
//    }
//
//    // Override for custom formatting of XAxis or YAxis labels
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return format.format(value);
    }

    // ... Override other methods for the other chart types
}
