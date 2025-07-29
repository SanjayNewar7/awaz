package com.example.awaz.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;

public class PersonalInformationActivity extends AppCompatActivity {

    private ImageView backArrow;
    private EditText addressEditText, contactNumberEditText, emailEditText, bioEditText;
    private Button cancelButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_information);

        // Initialize views
        backArrow = findViewById(R.id.backArrow);
        addressEditText = findViewById(R.id.addressEditText);
        contactNumberEditText = findViewById(R.id.contactNumberEditText);
        emailEditText = findViewById(R.id.emailEditText);
        bioEditText = findViewById(R.id.bioEditText);
        cancelButton = findViewById(R.id.cancelButton);
        saveButton = findViewById(R.id.saveButton);

        // Back arrow click listener
        backArrow.setOnClickListener(view -> {
            Intent intent = new Intent(PersonalInformationActivity.this, AllSettingActivity.class);
            startActivity(intent);
            finish(); // Close current activity
        });

        // Cancel button click listener
        cancelButton.setOnClickListener(view -> {
            finish(); // Discard changes and go back
        });

        // Save button click listener
        saveButton.setOnClickListener(view -> {
            if (validateInputs()) {
                // Simulate saving to a database or preferences
                Toast.makeText(this, "Changes saved successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PersonalInformationActivity.this, AllSettingActivity.class);
                startActivity(intent);
                finish(); // Close current activity
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String address = addressEditText.getText().toString().trim();
        String contact = contactNumberEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String bio = bioEditText.getText().toString().trim();

        if (address.isEmpty()) {
            addressEditText.setError("Address is required");
            isValid = false;
        }
        if (contact.isEmpty() || !contact.matches("^\\d{10}$")) {
            contactNumberEditText.setError("Valid 10-digit contact number is required");
            isValid = false;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email is required");
            isValid = false;
        }
        if (bio.length() > 500) { // Approx 100 words
            bioEditText.setError("Bio exceeds 100-word limit (500 characters)");
            isValid = false;
        }

        return isValid;
    }
}