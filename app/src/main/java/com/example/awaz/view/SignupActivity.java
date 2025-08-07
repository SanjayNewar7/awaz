package com.example.awaz.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.awaz.R;
import com.example.awaz.controller.AuthController;
import com.example.awaz.model.SignupRequest;

public class SignupActivity extends AppCompatActivity {
    private AuthController controller;
    private EditText editFirstName, editLastName, editDistrict, editWard, editPhone,
            editEmail, editPassword, editConfirmPassword, editCitizenshipNumber, editCity, editAreaName,
            editUsername;
    private TextView passwordStrength;
    private RadioGroup genderGroup;
    private CheckBox checkAgree;
    private Button btnCreateAccount, btnUploadFront, btnUploadBack;
    private ImageView citizenshipFrontPreview, citizenshipBackPreview;
    private TextView viewPolicy;
    private ActivityResultLauncher<String> pickFrontImage, pickBackImage;
    private Uri frontImageUri, backImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize controller
        controller = new AuthController(this);

        // Initialize views
        ImageView back = findViewById(R.id.btnBack);
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editDistrict = findViewById(R.id.editDistrict);
        editWard = findViewById(R.id.editWard);
        editPhone = findViewById(R.id.editPhone);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        passwordStrength = findViewById(R.id.passwordStrength);
        editCitizenshipNumber = findViewById(R.id.editCitizenshipNumber);
        editCity = findViewById(R.id.editCity);
        editAreaName = findViewById(R.id.editAreaName);
        editUsername = findViewById(R.id.editUsername);
        genderGroup = findViewById(R.id.genderGroup);
        checkAgree = findViewById(R.id.checkAgree);
        viewPolicy = findViewById(R.id.viewPolicy);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        citizenshipFrontPreview = findViewById(R.id.citizenshipFrontPreview);
        citizenshipBackPreview = findViewById(R.id.citizenshipBackPreview);
        btnUploadFront = findViewById(R.id.btnUploadFront);
        btnUploadBack = findViewById(R.id.btnUploadBack);

        if (btnCreateAccount == null) {
            Toast.makeText(this, "Button not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set back button listener
        back.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Initialize image pickers
        pickFrontImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        frontImageUri = uri;
                        citizenshipFrontPreview.setImageURI(uri);
                        Toast.makeText(this, "Front photo uploaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No image selected for front", Toast.LENGTH_SHORT).show();
                    }
                });

        pickBackImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        backImageUri = uri;
                        citizenshipBackPreview.setImageURI(uri);
                        Toast.makeText(this, "Back photo uploaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No image selected for back", Toast.LENGTH_SHORT).show();
                    }
                });

        // Set upload button listeners
        btnUploadFront.setOnClickListener(v -> pickFrontImage.launch("image/*"));
        btnUploadBack.setOnClickListener(v -> pickBackImage.launch("image/*"));

        // Real-time password validation and strength indicator
        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                updatePasswordStrength(password);

                if (!editConfirmPassword.getText().toString().isEmpty()) {
                    if (!password.equals(editConfirmPassword.getText().toString())) {
                        editConfirmPassword.setError("Passwords do not match");
                    } else {
                        editConfirmPassword.setError(null);
                    }
                }
            }
        });

        editConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!editPassword.getText().toString().isEmpty() &&
                        !s.toString().equals(editPassword.getText().toString())) {
                    editConfirmPassword.setError("Passwords do not match");
                } else {
                    editConfirmPassword.setError(null);
                }
            }
        });

        // View Policy link click listener
        viewPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, PolicyActivity.class);
            startActivity(intent);
        });

        // Set create account button listener
        btnCreateAccount.setOnClickListener(v -> {
            if (controller.validateSignupFields(editFirstName, editLastName, editDistrict,
                    editWard, editCity, editAreaName, editPhone, editEmail, editPassword,
                    editConfirmPassword, editCitizenshipNumber, editUsername, checkAgree.isChecked(),
                    genderGroup.getCheckedRadioButtonId())) {
                if (frontImageUri == null || backImageUri == null) {
                    Toast.makeText(this, "Please upload both citizenship photos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert images to Base64
                String frontImageBase64 = controller.convertImageToBase64(frontImageUri);
                String backImageBase64 = controller.convertImageToBase64(backImageUri);

                if (frontImageBase64 == null || backImageBase64 == null) {
                    Toast.makeText(this, "Error converting images to Base64", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedGender = findViewById(genderGroup.getCheckedRadioButtonId());
                String gender = selectedGender.getText().toString().substring(0, 1).toUpperCase() +
                        selectedGender.getText().toString().substring(1).toLowerCase();

                SignupRequest signupRequest = new SignupRequest(
                        editUsername.getText().toString().trim(),
                        editFirstName.getText().toString().trim(),
                        editLastName.getText().toString().trim(),
                        editDistrict.getText().toString().trim(),
                        editWard.getText().toString().trim(),
                        editCity.getText().toString().trim(),
                        editAreaName.getText().toString().trim(),
                        editPhone.getText().toString().trim(),
                        editEmail.getText().toString().trim(),
                        editPassword.getText().toString().trim(),
                        editConfirmPassword.getText().toString().trim(),
                        editCitizenshipNumber.getText().toString().trim(),
                        frontImageBase64,
                        backImageBase64,
                        checkAgree.isChecked(),
                        gender,
                        false // Default is_verified to false, as in web dashboard
                );

                controller.signup(signupRequest);

                // Clear form on successful submission (handled in AuthController)
            } else {
                Toast.makeText(this, "Please fix validation errors", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePasswordStrength(String password) {
        int strengthScore = 0;

        // Criteria checks (matching web dashboard)
        if (password.length() >= 8) strengthScore++;
        if (password.matches(".*[A-Z].*")) strengthScore++;
        if (password.matches(".*[a-z].*")) strengthScore++;
        if (password.matches(".*\\d.*")) strengthScore++;
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) strengthScore++;

        // Update TextView based on strength score
        switch (strengthScore) {
            case 0:
            case 1:
                passwordStrength.setText("Password Strength: Weak");
                passwordStrength.setTextColor(getResources().getColor(R.color.red_accent));
                break;
            case 2:
            case 3:
                passwordStrength.setText("Password Strength: Medium");
                passwordStrength.setTextColor(getResources().getColor(R.color.yellow));
                break;
            case 4:
            case 5:
                passwordStrength.setText("Password Strength: Strong");
                passwordStrength.setTextColor(getResources().getColor(R.color.green));
                break;
        }
    }

    // Method to clear form after successful registration
    private void clearForm() {
        editFirstName.setText("");
        editLastName.setText("");
        editDistrict.setText("");
        editWard.setText("");
        editCity.setText("");
        editAreaName.setText("");
        editPhone.setText("");
        editEmail.setText("");
        editPassword.setText("");
        editConfirmPassword.setText("");
        editCitizenshipNumber.setText("");
        editUsername.setText("");
        checkAgree.setChecked(false);
        genderGroup.clearCheck();
        frontImageUri = null;
        backImageUri = null;
        citizenshipFrontPreview.setImageResource(R.drawable.gallery_icon);
        citizenshipBackPreview.setImageResource(R.drawable.gallery_icon);
        passwordStrength.setText("Password Strength: Weak");
        passwordStrength.setTextColor(getResources().getColor(R.color.gray_dark));
    }
}