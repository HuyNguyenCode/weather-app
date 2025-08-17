package com.ninegroup.weather.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.ninegroup.weather.R;
import com.ninegroup.weather.api.Datapoint;
import com.ninegroup.weather.api.client.DatapointClient;
import com.ninegroup.weather.databinding.FragmentMonitoringBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonitoringFragment extends Fragment {
    private FragmentMonitoringBinding binding;
    private final static String DATE_RANGE_TAG = "DateRangePicker";
    private final static String TIME_TAG = "TimePicker";
    private MaterialTimePicker timePicker;
    private MaterialDatePicker<Pair<Long, Long>> dateRangePicker;
    private final SimpleDateFormat dateParse = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    //private final DateFormat dateParse1 = DateFormat.getDateInstance();
    private Handler handler;
    private Runnable updateUI;
    private Runnable timedOut;
    private Integer currentSensor = 1; // 1 = Weather sensor, 2 = Air Quality sensor
    private String timePicked;
    private String assetId = null;
    private String attribute = null;
    private Long fromTimestamp = null;
    private Long toTimestamp = null;

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMonitoringBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prepareBackgroundThreads();
        setUpLineChart();
        setUpDateRangePicker();
        setUpTimePicker();
        ((MainActivity) getActivity()).checkConnection();

        binding.datetimeAutoComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });

        binding.sensorTextAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    assetId = "5zI6XqkQVSfdgOrZ1MyWEf";
                    String[] items = getResources().getStringArray(R.array.weather_attributes);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_dropdown_item_1line, items);
                    binding.attributeTextAutoComplete.setAdapter(adapter);
                    currentSensor = 1; // Weather sensor with its attributes
                }
                else if (position == 1) {
                    assetId = "6Wo9Lv1Oa1zQleuRVfADP4";
                    String[] items = getResources().getStringArray(R.array.air_quality_attributes);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_dropdown_item_1line, items);
                    binding.attributeTextAutoComplete.setAdapter(adapter);
                    currentSensor = 2; // Air Quality sensor with its attributes
                }

                Log.i("Sensor Selected", "position: " + position + ", value: " + assetId);
            }
        });

        binding.attributeTextAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentSensor == 1) {
                    if (position == 0)
                        attribute = "temperature";
                    else if (position == 1)
                        attribute = "rainfall";
                    else if (position == 2)
                        attribute = "windSpeed";
                    else
                        attribute = "humidity";
                } else {
                    if (position == 0)
                        attribute = "AQI";
                    else if (position == 1)
                        attribute = "AQI_predict";
                    else if (position == 2)
                        attribute = "CO2";
                    else if (position == 3)
                        attribute = "CO2_average";
                    else if (position == 4)
                        attribute = "NO";
                    else if (position == 5)
                        attribute = "NO2";
                    else if (position == 6)
                        attribute = "O3";
                    else if (position == 7)
                        attribute = "PM10";
                    else if (position == 8)
                        attribute = "PM25";
                    else
                        attribute = "SO2";
                }

                Log.i("Attribute Selected", "position: " + position + ", value: " + attribute);
            }
        });

        binding.viewChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).checkConnection();
                if (MainActivity.isConnected) {
                    if (binding.sensorTextAutoComplete.getText() != null && binding.attributeTextAutoComplete.getText() != null
                            && binding.datetimeAutoComplete.getText() != null)
                        if (assetId != null && attribute != null && fromTimestamp != null && toTimestamp != null) {
                            DatapointClient datapointClient = new DatapointClient();
                            datapointClient.getDatapoint(assetId, attribute, fromTimestamp.toString(), toTimestamp.toString());

                            handler.postDelayed(updateUI, 1000);
                            handler.postDelayed(timedOut, 30000);
                        }
                }
                else
                    Toast.makeText(getContext(), "No network connection available! Please connect to a network with Internet access!",
                            Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prepareBackgroundThreads() {
        handler = new Handler();
        updateUI = new Runnable() {
            @Override
            public void run() {
                Log.i("UpdateUI", "UpdateUI process is running");

                if (!DatapointClient.isDatapointRunning && DatapointClient.isSuccess) {
                    List<Datapoint> datapoints = DatapointClient.datapointList;
                    if (datapoints.size() != 0) {
                        List<Entry> entries = new ArrayList<Entry>();
                        for (Datapoint data : datapoints) {
                            // turn your data into Entry objects
                            entries.add(new Entry(data.getX(), data.getY()));
                        }
                        LineDataSet dataSet = new LineDataSet(entries, "Temperature"); // add entries to dataset
                        //dataSet.setColors(new int[]{R.color.chart_1, R.color.chart_2, R.color.chart_3,
                                //R.color.chart_4, R.color.chart_5, R.color.chart_6, R.color.chart_7}, getContext());
                        //dataSet.setValueTextColor(...); // styling, ...
                        dataSet.setLineWidth(1.5f);
                        dataSet.setColor(com.google.android.material.R.color.material_dynamic_primary70);
                        dataSet.setCircleColor(com.google.android.material.R.color.material_dynamic_primary70);
                        LineData lineData = new LineData(dataSet);
                        binding.lineChart.setData(lineData);
                        binding.lineChart.invalidate(); // refresh
                    }
                    else
                        Toast.makeText(getContext(), "No history data available. Try extend the date range!",
                                Toast.LENGTH_SHORT).show();

                    Log.i("UpdateUI", "UpdateUI process is stopped");
                    handler.removeCallbacks(timedOut);
                    handler.removeCallbacks(updateUI);
                }
                else {
                    Log.i("UpdateUI", "UpdateUI process is running again");
                    handler.postDelayed(updateUI, 1000);
                }
            }
        };

        timedOut = new Runnable() {
            @Override
            public void run() {
                Log.i("Timed out", "UpdateUI process is stopped due to timed out");
                handler.removeCallbacks(updateUI);
            }
        };
    }

    private void setUpLineChart() {
        XAxis xAxis = binding.lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(5, false);

        YAxis yAxisLeft = binding.lineChart.getAxisLeft();
        YAxis yAxisRight = binding.lineChart.getAxisRight();
        yAxisRight.setEnabled(false);
        yAxisLeft.setLabelCount(5,false);

        Legend legend = binding.lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);

        //binding.lineChart.xAxis.valueFormatter = xA();
        binding.lineChart.setHardwareAccelerationEnabled(true);
        binding.lineChart.setNoDataText(getString(R.string.chart_no_data));
        //binding.lineChart.setMaxVisibleValueCount(6);
    }

    private void setUpTimePicker() {
        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText(getString(R.string.select_time))
                .build();

        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicked = timePicker.getHour() + ":" + timePicker.getMinute();
                Log.i("Time", timePicked);
                openDateRangePicker();
            }
        });
    }

    private void setUpDateRangePicker() {
        dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(getString(R.string.select_date_range))
                .setSelection(
                        new Pair<>(
                                MaterialDatePicker.thisMonthInUtcMilliseconds(),
                                MaterialDatePicker.todayInUtcMilliseconds()
                        )
                )
                .build();

        dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                //binding.datetimeAutoComplete.setText(dateRangePicker.getHeaderText());
                updateDateRange(selection.first, selection.second);
            }
        });

        dateRangePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
    }

    private void openDateRangePicker() {
        dateRangePicker.show(getChildFragmentManager(), DATE_RANGE_TAG);
    }

    private void openTimePicker() {
        timePicker.show(getChildFragmentManager(), TIME_TAG);
    }

    private void updateDateRange(Long startDate, Long endDate) {
        String startDateUnparsed = Instant.ofEpochMilli(startDate).
                atZone(ZoneId.systemDefault()).toLocalDate().toString()
                + " " + timePicked;
        String endDateUnparsed = Instant.ofEpochMilli(endDate).
                atZone(ZoneId.systemDefault()).toLocalDate().toString()
                + " " + timePicked;
        String dateTime = startDateUnparsed + " → " + endDateUnparsed;
        binding.datetimeAutoComplete.setText(dateTime);
        Log.i("DateTime", dateTime);

        convertToUnixTimestamp(startDateUnparsed, endDateUnparsed);
    }

    private void convertToUnixTimestamp(String startDate, String endDate) {
        Date startDateParsed = null;
        Date endDateParsed = null;
        try {
            startDateParsed = dateParse.parse(startDate);
            endDateParsed = dateParse.parse(endDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        fromTimestamp = startDateParsed.getTime();
        toTimestamp = endDateParsed.getTime();
        Log.i("Start Timestamp", fromTimestamp.toString());
        Log.i("End Timestamp", toTimestamp.toString());
    }

    private String convertToLocalTime(Long dateTime) {
        String date = Instant.ofEpochMilli(dateTime).
                atZone(ZoneId.systemDefault()).toLocalDate().toString();
        return date;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        handler.removeCallbacks(updateUI);
    }
}
