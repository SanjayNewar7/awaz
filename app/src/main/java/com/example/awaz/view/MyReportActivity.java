package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.awaz.R;
import com.example.awaz.adapter.ReportAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyReportActivity extends AppCompatActivity {

    private static final String TAG = "MyReportActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_report);

        // Initialize views
        RecyclerView recyclerViewReports = findViewById(R.id.recyclerViewReports);
        Spinner spinnerReportTypes = findViewById(R.id.spinnerReportTypes);
        ImageView backArrow = findViewById(R.id.backArrow);

        ImageView back = findViewById(R.id.backArrow);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(MyReportActivity.this, HomeMainActivity.class);
            startActivity(intent);
        });

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewReports.setLayoutManager(layoutManager);
        recyclerViewReports.setHasFixedSize(true);

        // Dummy data for RecyclerView
        List<ReportAdapter.Report> reportList = new ArrayList<>();
        reportList.add(new ReportAdapter.Report("Pothole on Main Road", "June 23, 2025", "Pending"));
        reportList.add(new ReportAdapter.Report("Water Leakage Issue", "June 20, 2025", "In Progress"));
        reportList.add(new ReportAdapter.Report("Garbage Overflow", "June 18, 2025", "Resolved"));
        reportList.add(new ReportAdapter.Report("Street Light Outage", "June 15, 2025", "Closed"));
        reportList.add(new ReportAdapter.Report("Road Crack Repair", "June 10, 2025", "Pending"));

        Log.d(TAG, "Report list size: " + reportList.size()); // Debug log

        // Initialize adapter
        ReportAdapter adapter = new ReportAdapter(reportList);
        recyclerViewReports.setAdapter(adapter);

        // Set up Spinner
        ArrayAdapter<CharSequence> reportTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.report_types, android.R.layout.simple_spinner_item);
        reportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReportTypes.setAdapter(reportTypeAdapter);

        // Toggle no reports section
        findViewById(R.id.noReportsSection).setVisibility(reportList.isEmpty() ? View.VISIBLE : View.GONE);

        // Back button listener
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Navigate back
            }
        });
    }
}