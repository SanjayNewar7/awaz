package com.example.awaz.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RaiseIssueActivity extends AppCompatActivity {

    private EditText editIssueHeading, editIssueDescription, editAreaName, editLocation;
    private Spinner spinnerReportType, spinnerDistrict, spinnerWard;
    private ImageView issuePhoto1Preview, issuePhoto2Preview;
    private Button btnUploadPhoto1, btnUploadPhoto2, btnSubmitIssue;
    private ActivityResultLauncher<String> pickPhoto1, pickPhoto2;
    private Uri photo1Uri, photo2Uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raiseissue_activity);

        // Initialize views with null checks
        ImageView back = findViewById(R.id.backArrow);
        editIssueHeading = findViewById(R.id.editIssueHeading);
        editIssueDescription = findViewById(R.id.editIssueDescription);
        editAreaName = findViewById(R.id.editAreaName);
        editLocation = findViewById(R.id.editLocation);
        spinnerReportType = findViewById(R.id.spinnerReportType);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);
        issuePhoto1Preview = findViewById(R.id.issuePhoto1Preview);
        issuePhoto2Preview = findViewById(R.id.issuePhoto2Preview);
        btnUploadPhoto1 = findViewById(R.id.btnUploadPhoto1);
        btnUploadPhoto2 = findViewById(R.id.btnUploadPhoto2);
        btnSubmitIssue = findViewById(R.id.btnSubmitIssue);

        // Check if any view is null and log an error
        if (back == null || editIssueHeading == null || editIssueDescription == null ||
                editAreaName == null || editLocation == null || spinnerReportType == null ||
                spinnerDistrict == null || spinnerWard == null || issuePhoto1Preview == null ||
                issuePhoto2Preview == null || btnUploadPhoto1 == null || btnUploadPhoto2 == null ||
                btnSubmitIssue == null) {
            Toast.makeText(this, "Error: Some UI elements are missing in layout", Toast.LENGTH_LONG).show();
            finish(); // Close activity if layout is invalid
            return;
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RaiseIssueActivity.this, HomeMainActivity.class);
                startActivity(intent);
            }
        });

        // Set up Spinners with sample data
        try {
            ArrayAdapter<CharSequence> reportTypeAdapter = ArrayAdapter.createFromResource(this,
                    R.array.report_types, android.R.layout.simple_spinner_item);
            reportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerReportType.setAdapter(reportTypeAdapter);

            ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(this,
                    R.array.districts, android.R.layout.simple_spinner_item);
            districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDistrict.setAdapter(districtAdapter);

            ArrayAdapter<CharSequence> wardAdapter = ArrayAdapter.createFromResource(this,
                    R.array.wards, android.R.layout.simple_spinner_item);
            wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerWard.setAdapter(wardAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading spinner data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize image pickers
        pickPhoto1 = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        photo1Uri = uri;
                        issuePhoto1Preview.setImageURI(uri);
                        Toast.makeText(this, "Photo 1 uploaded", Toast.LENGTH_SHORT).show();
                    }
                });

        pickPhoto2 = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        photo2Uri = uri;
                        issuePhoto2Preview.setImageURI(uri);
                        Toast.makeText(this, "Photo 2 uploaded", Toast.LENGTH_SHORT).show();
                    }
                });

        // Set upload button listeners
        btnUploadPhoto1.setOnClickListener(v -> pickPhoto1.launch("image/*"));
        btnUploadPhoto2.setOnClickListener(v -> pickPhoto2.launch("image/*"));

        // Set submit button listener
        btnSubmitIssue.setOnClickListener(v -> {
            if (validateInputs()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Success");
                builder.setMessage("Report has been submitted successfully");
                builder.setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> { // Updated Handler
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }, 2000);

                String time = new SimpleDateFormat("hh:mm a z", Locale.getDefault()).format(new Date());
                Toast.makeText(this, "Issue submitted at " + time + " on June 23, 2025", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (editIssueHeading.getText().toString().trim().isEmpty()) {
            editIssueHeading.setError("Issue heading is required");
            isValid = false;
        }
        if (editIssueDescription.getText().toString().trim().isEmpty()) {
            editIssueDescription.setError("Description is required");
            isValid = false;
        }
        if (editAreaName.getText().toString().trim().isEmpty()) {
            editAreaName.setError("Area name is required");
            isValid = false;
        }
        if (editLocation.getText().toString().trim().isEmpty()) {
            editLocation.setError("Location is required");
            isValid = false;
        }
        if (spinnerReportType.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a report type", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (spinnerDistrict.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a district", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (spinnerWard.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a ward", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }
}