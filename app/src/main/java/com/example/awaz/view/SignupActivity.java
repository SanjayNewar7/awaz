package com.example.awaz.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.awaz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText editFirstName, editLastName, editDistrict, editWard, editPhone, editEmail, editPassword, editCitizenshipNumber, editCity, editAreaName, editUsername;
    private RadioGroup genderGroup;
    private CheckBox checkAgree;
    private Button btnCreateAccount, btnUploadFront, btnUploadBack;
    private ImageView citizenshipFrontPreview, citizenshipBackPreview;
    private ActivityResultLauncher<String> pickFrontImage, pickBackImage;
    private Uri frontImageUri, backImageUri;
    private RequestQueue requestQueue;
    private static final String BASE_URL = "http://10.0.2.2:8000/api/users"; // Updated for emulator

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        Log.d(TAG, "Initializing views");
        ImageView back = findViewById(R.id.btnBack);
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editDistrict = findViewById(R.id.editDistrict);
        editWard = findViewById(R.id.editWard);
        editPhone = findViewById(R.id.editPhone);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editCitizenshipNumber = findViewById(R.id.editCitizenshipNumber);
        editCity = findViewById(R.id.editCity);
        editAreaName = findViewById(R.id.editAreaName);
        editUsername = findViewById(R.id.editUsername);
        genderGroup = findViewById(R.id.genderGroup);
        checkAgree = findViewById(R.id.checkAgree);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        citizenshipFrontPreview = findViewById(R.id.citizenshipFrontPreview);
        citizenshipBackPreview = findViewById(R.id.citizenshipBackPreview);
        btnUploadFront = findViewById(R.id.btnUploadFront);
        btnUploadBack = findViewById(R.id.btnUploadBack);

        if (btnCreateAccount == null) {
            Log.e(TAG, "btnCreateAccount is null!");
        } else {
            Log.d(TAG, "btnCreateAccount initialized");
        }

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);
        Log.d(TAG, "Volley request queue initialized");

        // Set back button listener
        back.setOnClickListener(view -> {
            Log.d(TAG, "Back button clicked");
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

        // Set create account button listener
        btnCreateAccount.setOnClickListener(v -> {
            Log.d(TAG, "Create Account button clicked");
            if (validateInputs()) {
                Log.d(TAG, "Validation passed");
                if (frontImageUri == null || backImageUri == null) {
                    Toast.makeText(this, "Please upload both citizenship photos", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Image validation failed");
                    return;
                }
                Log.d(TAG, "Calling registerUser");
                registerUser();
            } else {
                Log.d(TAG, "Validation failed");
                Toast.makeText(this, "Please fix validation errors", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        Log.d(TAG, "Validating inputs");
        boolean isValid = true;

        if (isEmpty(editFirstName, "First Name is required")) isValid = false;
        if (isEmpty(editLastName, "Last Name is required")) isValid = false;
        if (isEmpty(editDistrict, "District is required")) isValid = false;
        if (isEmpty(editWard, "Ward is required")) isValid = false;
        if (isEmpty(editCitizenshipNumber, "Citizenship Number is required")) isValid = false;
        if (isEmpty(editCity, "City is required")) isValid = false;
        if (isEmpty(editAreaName, "Area Name is required")) isValid = false;
        if (isEmpty(editUsername, "Username is required")) isValid = false;

        if (!validatePhone(editPhone)) isValid = false;
        if (!validatePassword(editPassword)) isValid = false;
        if (!validateEmail(editEmail)) isValid = false;

        if (genderGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (!checkAgree.isChecked()) {
            checkAgree.setTextColor(getResources().getColor(R.color.red_accent));
            Toast.makeText(this, "You must agree to the terms and conditions", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            checkAgree.setTextColor(getResources().getColor(R.color.gray_dark));
        }

        Log.d(TAG, "Validation result: " + isValid);
        return isValid;
    }

    private boolean isEmpty(EditText field, String errorHint) {
        if (field.getText().toString().trim().isEmpty()) {
            field.setBackgroundResource(R.drawable.edittext_error_background);
            field.setHint(errorHint);
            field.setHintTextColor(getResources().getColor(R.color.red_accent));
            Log.d(TAG, field.getId() + " is empty");
            return true;
        } else {
            field.setBackgroundResource(R.drawable.edittext_background);
            return false;
        }
    }

    private boolean validatePhone(EditText field) {
        String phone = field.getText().toString().trim();
        if (phone.isEmpty()) {
            field.setBackgroundResource(R.drawable.edittext_error_background);
            field.setHint("Phone number is required");
            field.setHintTextColor(getResources().getColor(R.color.red_accent));
            Log.d(TAG, "Phone validation failed: empty");
            return false;
        } else if (!phone.matches("^\\d{10}$")) {
            field.setBackgroundResource(R.drawable.edittext_error_background);
            field.setText("");
            field.setHint("Enter valid 10-digit phone");
            field.setHintTextColor(getResources().getColor(R.color.red_accent));
            Log.d(TAG, "Phone validation failed: invalid format");
            return false;
        } else {
            field.setBackgroundResource(R.drawable.edittext_background);
            return true;
        }
    }

    private boolean validatePassword(EditText field) {
        String password = field.getText().toString();
        if (password.isEmpty()) {
            field.setBackgroundResource(R.drawable.edittext_error_background);
            field.setHint("Password is required");
            field.setHintTextColor(getResources().getColor(R.color.red_accent));
            Log.d(TAG, "Password validation failed: empty");
            return false;
        } else if (password.length() < 8) { // Match API's min 8 characters
            field.setBackgroundResource(R.drawable.edittext_error_background);
            field.setText("");
            field.setHint("Password must be at least 8 characters");
            field.setHintTextColor(getResources().getColor(R.color.red_accent));
            Log.d(TAG, "Password validation failed: too short");
            return false;
        } else {
            field.setBackgroundResource(R.drawable.edittext_background);
            return true;
        }
    }

    private boolean validateEmail(EditText field) {
        String email = field.getText().toString().trim();
        if (email.isEmpty()) {
            field.setBackgroundResource(R.drawable.edittext_error_background);
            field.setHint("Email is required");
            field.setHintTextColor(getResources().getColor(R.color.red_accent));
            Log.d(TAG, "Email validation failed: empty");
            return false;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            field.setBackgroundResource(R.drawable.edittext_error_background);
            field.setText("");
            field.setHint("Enter valid email");
            field.setHintTextColor(getResources().getColor(R.color.red_accent));
            Log.d(TAG, "Email validation failed: invalid format");
            return false;
        } else {
            field.setBackgroundResource(R.drawable.edittext_background);
            return true;
        }
    }

    private String convertImageToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error converting image to Base64: " + e.getMessage());
            return null;
        }
    }

    private void registerUser() {
        Log.d(TAG, "Starting registerUser");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", editUsername.getText().toString().trim());
            jsonObject.put("first_name", editFirstName.getText().toString().trim());
            jsonObject.put("last_name", editLastName.getText().toString().trim());
            jsonObject.put("district", editDistrict.getText().toString().trim());
            jsonObject.put("ward", editWard.getText().toString().trim());
            jsonObject.put("city", editCity.getText().toString().trim());
            jsonObject.put("area_name", editAreaName.getText().toString().trim());
            jsonObject.put("phone_number", editPhone.getText().toString().trim());
            jsonObject.put("email", editEmail.getText().toString().trim());
            jsonObject.put("password_hash", editPassword.getText().toString().trim());
            jsonObject.put("citizenship_id_number", editCitizenshipNumber.getText().toString().trim());
            String frontImageBase64 = convertImageToBase64(frontImageUri);
            String backImageBase64 = convertImageToBase64(backImageUri);
            if (frontImageBase64 == null || backImageBase64 == null) {
                Toast.makeText(this, "Error converting images to Base64", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Image conversion failed");
                return;
            }
            jsonObject.put("citizenship_front_image", frontImageBase64);
            jsonObject.put("citizenship_back_image", backImageBase64);
            jsonObject.put("agreed_to_terms", checkAgree.isChecked());

            int selectedGenderId = genderGroup.getCheckedRadioButtonId();
            RadioButton selectedGender = findViewById(selectedGenderId);
            jsonObject.put("gender", selectedGender.getText().toString().substring(0, 1).toUpperCase() +
                    selectedGender.getText().toString().substring(1).toLowerCase());

            Log.d(TAG, "JSON Object: " + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSON Exception: " + e.getMessage());
            Toast.makeText(this, "Error preparing data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, jsonObject,
                response -> {
                    Log.d(TAG, "Server Response: " + response.toString());
                    Toast.makeText(SignupActivity.this, "Account created successfully!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Log.e(TAG, "Volley Error: " + error.toString());
                    String errorMessage = "Registration failed: " + error.getMessage();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject jsonError = new JSONObject(responseBody);
                            errorMessage = jsonError.toString(); // Show full error object
                            Log.e(TAG, "Server Error Response: " + responseBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error response: " + e.getMessage());
                        }
                    } else {
                        Log.e(TAG, "No network response data available");
                    }
                    Toast.makeText(SignupActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
        Log.d(TAG, "Volley POST request added to queue with increased timeout");
    }
}