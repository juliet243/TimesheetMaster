package com.example.assignmentpart2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.EditText;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Reports extends AppCompatActivity {

    EditText minGoal, maxGoal, category;
    Integer totalHour, min1, max1;
    BarData barData;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    ReportsData reportsData;
    BarChart barChart;
    String date, categories;

    AlertDialog alertDialog;

    private EditText datePickerEditText;
    private DatePickerDialog datePickerDialog;
    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        minGoal = (EditText) findViewById(R.id.edtxtMinGoal);
        maxGoal = (EditText) findViewById(R.id.edtxtMaxGoal);
        category = (EditText) findViewById(R.id.edtxtCateName);
        barChart = (BarChart) findViewById(R.id.bar_chart);
        datePickerEditText = findViewById(R.id.datePickerEditText);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("Categories");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot minSnapshot = categorySnapshot.child("minGoal");
                    DataSnapshot maxSnapshot = categorySnapshot.child("maxGoal");
                    DataSnapshot catSnapshot = categorySnapshot.child("name");

                    if (minSnapshot.exists() && maxSnapshot.exists() && catSnapshot.exists()) {
                        Object minObj = minSnapshot.getValue();
                        Object maxObj = maxSnapshot.getValue();
                        String catObj = catSnapshot.getKey();

                        if (minObj instanceof String && maxObj instanceof String) {
                            String min = (String) minObj;
                            String max = (String) maxObj;

                            if (!TextUtils.isEmpty(min) && !TextUtils.isEmpty(max) && !TextUtils.isEmpty(catObj)) {
                                String id = myRef.push().getKey();

                                categories = catObj;
                                min1 = Integer.parseInt(min);
                                max1 = Integer.parseInt(max);
                                totalHour = min1 + max1;

                                reportsData = new ReportsData(totalHour, min1, max1, date, categories);
                                myRef.child(Objects.requireNonNull(id)).setValue(reportsData);
                            }
                        }
                    }
                }
                retrieveData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
                System.out.println("DatabaseError: " + databaseError.getMessage());
            }
        });


        // Set initial date on the EditText
        Calendar calendar = Calendar.getInstance();
        datePickerEditText.setText(getFormattedDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)));

        // Set up DatePickerDialog
        datePickerEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });




    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int dayOfMonth, int month, int year) {
                // Update the date on the EditText
                datePickerEditText.setText(getFormattedDate(dayOfMonth, month, year));

                String selectedDate  = dayOfMonth + "/" + (month + 1) + "/" + year;
                Toast.makeText(Reports.this, "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();

                // Retrieve data for the selected period
                // Call a method to update the chart with the selected period's data
                updateChart(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private String getFormattedDate(int dayOfMonth, int month, int year) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(dayOfMonth, month, year);
        return sdf.format(selectedDate.getTime());
    }

    private void retrieveData() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Integer> totalHoursList = new ArrayList<>();
                List<Integer> minimumHoursList = new ArrayList<>();
                List<Integer> maximumHoursList = new ArrayList<>();
                List<String> categoryList = new ArrayList<>();


                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // DataValue class objects representing the data
                    reportsData = dataSnapshot.getValue(ReportsData.class);
                    DataSnapshot catSnapshot = dataSnapshot.child("name");


                    // Extract the required values from the GraphData object
                    int total =  reportsData.getTotalHour();
                    int minimum = reportsData.getMinGoal();
                    int maximum = reportsData.getMaxGoal();
                    String catList = reportsData.getCategory();

                    int hour = minimum + maximum;

                    totalHoursList.add(hour);
                    minimumHoursList.add(minimum);
                    maximumHoursList.add(maximum);
                    categoryList.add(catList);

                    updateChartData(totalHoursList, minimumHoursList, maximumHoursList, categoryList);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to retrieve data: ${error.message}");
            }
        });


    }

    private void updateChart(String selectedDate) {

        Query query = myRef.orderByChild("date").equalTo(selectedDate);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Integer> totalHoursList = new ArrayList<>();
                List<Integer> minimumHoursList = new ArrayList<>();
                List<Integer> maximumHoursList = new ArrayList<>();
                List<String> categoryList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // DataValue class objects representing the data
                    reportsData = dataSnapshot.getValue(ReportsData.class);
                    DataSnapshot catSnapshot = dataSnapshot.child("name");


                    // Extract the required values from the GraphData object
                    int total = Objects.requireNonNull(reportsData).getTotalHour();
                    int minimum = reportsData.getMinGoal();
                    int maximum = reportsData.getMaxGoal();
                    String catList = reportsData.getCategory();
                    Object test = catSnapshot.getValue();
                    catList = test.toString();


                    int hour = minimum + maximum;

                    totalHoursList.add(hour);
                    minimumHoursList.add(minimum);
                    maximumHoursList.add(maximum);
                    categoryList.add(catList);
                }

                updateChartData(totalHoursList, minimumHoursList, maximumHoursList, categoryList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to retrieve data: ${error.message}");
            }
        });
    }

    private void updateChartData(List<Integer> totalHoursList, List<Integer> minimumHoursList, List<Integer> maximumHoursList, List<String> categoryList) {
        ArrayList<BarEntry> stackEntries = new ArrayList<>();
        for (int i = 0; i < totalHoursList.size(); i++) {
            stackEntries.add(new BarEntry(i, new float[]{totalHoursList.get(i), minimumHoursList.get(i), maximumHoursList.get(i)}));
        }

        BarDataSet stackDataSet = new BarDataSet(stackEntries, "Report");
        stackDataSet.setStackLabels(new String[]{"Total Hours", "Min Hours", "Max Hours"});
        stackDataSet.setColors(Color.RED, Color.GREEN, Color.BLUE); // Set colors for each dataset

        barData = new BarData(stackDataSet);
        barData.setBarWidth(0.9f); // set custom bar width
        XAxis xAxisUp = barChart.getXAxis();
        ArrayList<String> WeekOne = new ArrayList<>();
        WeekOne.add("Mon");
        WeekOne.add("Tue");
        WeekOne.add("Wed");
        WeekOne.add("Thur");
        WeekOne.add("Fri");
        WeekOne.add("Sat");
        WeekOne.add("Sun");
        xAxisUp.setValueFormatter(new IndexAxisValueFormatter(WeekOne));

        barChart.setData(barData);
        barChart.animateY(2000);
        barChart.setDrawValueAboveBar(true);
        barChart.setFitBars(true);
        barChart.invalidate(); // refresh the chart

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int x = barChart.getBarData().getDataSetForEntry(e).getEntryIndex((BarEntry) e);

                String displayCategory = categoryList.get(x);
                String minHourGoal = minimumHoursList.get(x).toString();
                String maxHourGoal = maximumHoursList.get(x).toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(Reports.this);
                builder.setCancelable(true);
                View nView = LayoutInflater.from(Reports.this).inflate(R.layout.category_display_layout, null);

                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView cat = nView.findViewById(R.id.bar_category);
                TextView min = nView.findViewById(R.id.minGoals);
                TextView max = nView.findViewById(R.id.maxGoals);

                Toast.makeText(Reports.this, "Category: " + displayCategory + "Min:" + minHourGoal + "Max:" + maxHourGoal, Toast.LENGTH_SHORT).show();

                cat.setText(displayCategory);
                min.setText(minHourGoal);
                max.setText(maxHourGoal);
                builder.setView(nView);
                alertDialog = builder.create();
                alertDialog.show();

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

}