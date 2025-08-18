package com.example.awaz.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.example.awaz.controller.AuthController;
import com.example.awaz.controller.IssueController;
import com.example.awaz.model.IssueRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaiseIssueActivity extends AppCompatActivity {

    private EditText editIssueHeading, editIssueDescription, editAreaName;
    private Spinner spinnerReportType, spinnerDistrict, spinnerRegionType, spinnerWard;
    private ImageView issuePhoto1Preview, issuePhoto2Preview;
    private Button btnUploadPhoto1, btnUploadPhoto2, btnSubmitIssue;
    private ActivityResultLauncher<String> pickPhoto1, pickPhoto2;
    private Uri photo1Uri, photo2Uri;
    private IssueController issueController;
    private AuthController authController;
    private Map<String, Map<String, Integer>> districtRegionWardMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raiseissue_activity);

        issueController = new IssueController(this);
        authController = new AuthController(this);

        ImageView back = findViewById(R.id.backArrow);
        editIssueHeading = findViewById(R.id.editIssueHeading);
        editIssueDescription = findViewById(R.id.editIssueDescription);
        editAreaName = findViewById(R.id.editAreaName);
        spinnerReportType = findViewById(R.id.spinnerReportType);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerRegionType = findViewById(R.id.spinnerRegionType);
        spinnerWard = findViewById(R.id.spinnerWard);
        issuePhoto1Preview = findViewById(R.id.issuePhoto1Preview);
        issuePhoto2Preview = findViewById(R.id.issuePhoto2Preview);
        btnUploadPhoto1 = findViewById(R.id.btnUploadPhoto1);
        btnUploadPhoto2 = findViewById(R.id.btnUploadPhoto2);
        btnSubmitIssue = findViewById(R.id.btnSubmitIssue);

        if (back == null || editIssueHeading == null || editIssueDescription == null ||
                editAreaName == null || spinnerReportType == null || spinnerDistrict == null ||
                spinnerRegionType == null || spinnerWard == null || issuePhoto1Preview == null ||
                issuePhoto2Preview == null || btnUploadPhoto1 == null || btnUploadPhoto2 == null ||
                btnSubmitIssue == null) {
            Toast.makeText(this, "Error: Some UI elements are missing in layout", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        back.setOnClickListener(v -> {
            Intent intent = new Intent(RaiseIssueActivity.this, HomeMainActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize district to region type to ward map
        initializeDistrictRegionWardMap();

        try {
            // Set up report type spinner
            ArrayAdapter<CharSequence> reportTypeAdapter = ArrayAdapter.createFromResource(this,
                    R.array.report_types, android.R.layout.simple_spinner_item);
            reportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerReportType.setAdapter(reportTypeAdapter);

            // Set up district spinner with sorted districts
            List<String> districts = new ArrayList<>();
            districts.add("Select District");
            districts.addAll(districtRegionWardMap.keySet());
            Collections.sort(districts.subList(1, districts.size())); // Sort districts alphabetically, excluding "Select District"
            ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, districts);
            districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDistrict.setAdapter(districtAdapter);

            // Set up region type spinner
            ArrayAdapter<CharSequence> regionTypeAdapter = ArrayAdapter.createFromResource(this,
                    R.array.region_types, android.R.layout.simple_spinner_item);
            regionTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRegionType.setAdapter(regionTypeAdapter);

            // Set up ward spinner
            ArrayAdapter<CharSequence> wardAdapter = ArrayAdapter.createFromResource(this,
                    R.array.wards, android.R.layout.simple_spinner_item);
            wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerWard.setAdapter(wardAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading spinner data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

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

        btnUploadPhoto1.setOnClickListener(v -> pickPhoto1.launch("image/*"));
        btnUploadPhoto2.setOnClickListener(v -> pickPhoto2.launch("image/*"));

        // District selection listener to update region type spinner
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrict = parent.getItemAtPosition(position).toString();
                updateRegionTypeSpinner(selectedDistrict);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateRegionTypeSpinner("Select District");
            }
        });

        // Region type selection listener to update ward spinner
        spinnerRegionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrict = spinnerDistrict.getSelectedItem().toString();
                String selectedRegionType = parent.getItemAtPosition(position).toString();
                updateWardSpinner(selectedDistrict, selectedRegionType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateWardSpinner("Select District", "Select Region Type");
            }
        });

        // In your btnSubmitIssue onClickListener, replace with this:
        btnSubmitIssue.setOnClickListener(v -> {
            String heading = editIssueHeading.getText().toString().trim();
            String description = editIssueDescription.getText().toString().trim();
            String reportType = spinnerReportType.getSelectedItem().toString();
            String district = spinnerDistrict.getSelectedItem().toString();
            String ward = spinnerWard.getSelectedItem().toString();
            String areaName = editAreaName.getText().toString().trim();
            String location = spinnerRegionType.getSelectedItem().toString();

            // Basic validation
            if (heading.isEmpty()) {
                editIssueHeading.setError("Heading is required");
                return;
            }
            if (description.isEmpty()) {
                editIssueDescription.setError("Description is required");
                return;
            }
            if (areaName.isEmpty()) {
                editAreaName.setError("Area name is required");
                return;
            }
            if (reportType.equals("Select Report Type")) {
                Toast.makeText(this, "Please select a report type", Toast.LENGTH_SHORT).show();
                return;
            }
            if (district.equals("Select District")) {
                Toast.makeText(this, "Please select a district", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ward.equals("Select Ward")) {
                Toast.makeText(this, "Please select a ward", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert images to base64 if available
            String photo1 = null;
            String photo2 = null;
            try {
                if (photo1Uri != null) {
                    photo1 = authController.convertImageToBase64(photo1Uri);
                }
                if (photo2Uri != null) {
                    photo2 = authController.convertImageToBase64(photo2Uri);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error processing images", Toast.LENGTH_SHORT).show();
                return;
            }

            IssueRequest issueRequest = new IssueRequest(
                    heading, description, reportType,
                    district, ward, areaName, location,
                    photo1, photo2
            );

            new IssueController(RaiseIssueActivity.this).submitIssue(issueRequest);
        });
    }

    private void initializeDistrictRegionWardMap() {
        districtRegionWardMap = new HashMap<>();

        // Achham
        districtRegionWardMap.put("Achham", new HashMap<String, Integer>() {{
            put("Kamalbazar Municipality", 10);
            put("Mangalsen Municipality", 13);
            put("Panchadewal Binayak Municipality", 9);
            put("Sanphebagar Municipality", 14);
            put("Bannigadhi Jayagadh Rural Municipality", 6);
            put("Chaurpati Rural Municipality", 7);
            put("Dhakari Rural Municipality", 7);
            put("Mellekh Rural Municipality", 8);
            put("Ramaroshan Rural Municipality", 7);
            put("Turmakhand Rural Municipality", 10);
        }});

        // Arghakhanchi
        districtRegionWardMap.put("Arghakhanchi", new HashMap<String, Integer>() {{
            put("Bhumekasthan Municipality", 10);
            put("Sandhikharka Municipality", 12);
            put("Sitganga Municipality", 14);
            put("Chhatradev Rural Municipality", 8);
            put("Malarani Rural Municipality", 9);
            put("Panini Rural Municipality", 8);
        }});

        // Baglung
        districtRegionWardMap.put("Baglung", new HashMap<String, Integer>() {{
            put("Baglung Municipality", 14);
            put("Dhorpatan Municipality", 9);
            put("Galkot Municipality", 11);
            put("Jaimuni Municipality", 10);
            put("Badigad Rural Municipality", 10);
            put("Bareng Rural Municipality", 5);
            put("Kathekhola Rural Municipality", 8);
            put("Nisikhola Rural Municipality", 7);
            put("Taman Khola Rural Municipality", 6);
            put("Tara Khola Rural Municipality", 5);
        }});

        // Baitadi
        districtRegionWardMap.put("Baitadi", new HashMap<String, Integer>() {{
            put("Dasharathchanda Municipality", 11);
            put("Melauli Municipality", 9);
            put("Patan Municipality", 10);
            put("Purchaudi Municipality", 10);
            put("Dilasaini Rural Municipality", 7);
            put("Dogadakedar Rural Municipality", 7);
            put("Pancheshwar Rural Municipality", 6);
            put("Shivanath Rural Municipality", 6);
            put("Sigas Rural Municipality", 9);
            put("Surnaya Rural Municipality", 8);
        }});

        // Bajhang
        districtRegionWardMap.put("Bajhang", new HashMap<String, Integer>() {{
            put("Bungal Municipality", 11);
            put("Jayaprithvi Municipality", 11);
            put("Bitthadchir Rural Municipality", 9);
            put("Chabispathivera Rural Municipality", 7);
            put("Durgathali Rural Municipality", 7);
            put("Kanda Rural Municipality", 5);
            put("Kedarseu Rural Municipality", 9);
            put("Khaptadchhanna Rural Municipality", 7);
            put("Masta Rural Municipality", 7);
            put("Saipal Rural Municipality", 5);
            put("Surma Rural Municipality", 5);
            put("Talkot Rural Municipality", 7);
        }});

        // Bajura
        districtRegionWardMap.put("Bajura", new HashMap<String, Integer>() {{
            put("Badimalika Municipality", 9);
            put("Budhiganga Municipality", 10);
            put("Budhinanda Municipality", 10);
            put("Tribeni Municipality", 9);
            put("Chhededaha Rural Municipality", 7);
            put("Gaumul Rural Municipality", 6);
            put("Himali Rural Municipality", 7);
            put("Jagannath Rural Municipality", 6);
            put("Swamikartik Khapar Rural Municipality", 5);
        }});

        // Banke
        districtRegionWardMap.put("Banke", new HashMap<String, Integer>() {{
            put("Kohalpur Municipality", 15);
            put("Nepalgunj Sub-Metropolitan City", 23);
            put("Baijanath Rural Municipality", 8);
            put("Duduwa Rural Municipality", 6);
            put("Janaki Rural Municipality", 6);
            put("Khajura Rural Municipality", 8);
            put("Narainapur Rural Municipality", 6);
            put("Rapti Sonari Rural Municipality", 9);
        }});

        // Bara
        districtRegionWardMap.put("Bara", new HashMap<String, Integer>() {{
            put("Jitpur Simara Sub-Metropolitan City", 24);
            put("Kalaiya Sub-Metropolitan City", 27);
            put("Mahagadhimai Municipality", 11);
            put("Nijgadh Municipality", 13);
            put("Simraungadh Municipality", 11);
            put("Adarsh Kotwal Rural Municipality", 7);
            put("Baragadhi Rural Municipality", 9);
            put("Bishrampur Rural Municipality", 7);
            put("Devtal Rural Municipality", 7);
            put("Karaiyamai Rural Municipality", 8);
            put("Pachrauta Rural Municipality", 9);
            put("Parwanipur Rural Municipality", 5);
            put("Pheta Rural Municipality", 7);
            put("Prasauni Rural Municipality", 7);
            put("Suwarna Rural Municipality", 7);
        }});

        // Bardiya
        districtRegionWardMap.put("Bardiya", new HashMap<String, Integer>() {{
            put("Bansagadhi Municipality", 9);
            put("Barbardiya Municipality", 11);
            put("Gulariya Municipality", 12);
            put("Madhuwan Municipality", 9);
            put("Rajapur Municipality", 10);
            put("Thakurbaba Municipality", 9);
            put("Badhaiyatal Rural Municipality", 9);
            put("Geruwa Rural Municipality", 6);
        }});

        // Bhaktapur
        districtRegionWardMap.put("Bhaktapur", new HashMap<String, Integer>() {{
            put("Bhaktapur Municipality", 10);
            put("Changunarayan Municipality", 9);
            put("Madhyapur Thimi Municipality", 9);
            put("Suryabinayak Municipality", 10);
        }});

        // Bhojpur
        districtRegionWardMap.put("Bhojpur", new HashMap<String, Integer>() {{
            put("Bhojpur Municipality", 12);
            put("Shadananda Municipality", 14);
            put("Aamchowk Rural Municipality", 10);
            put("Arun Rural Municipality", 7);
            put("Hatuwagadhi Rural Municipality", 9);
            put("Pauwadungma Rural Municipality", 6);
            put("Ramprasad Rai Rural Municipality", 8);
            put("Salpasilichho Rural Municipality", 6);
            put("Tyamke Maiyunm Rural Municipality", 9);
        }});

        // Chitwan
        districtRegionWardMap.put("Chitwan", new HashMap<String, Integer>() {{
            put("Bharatpur Metropolitan City", 29);
            put("Kalika Municipality", 11);
            put("Khairahani Municipality", 13);
            put("Madi Municipality", 9);
            put("Rapti Municipality", 13);
            put("Ratnanagar Municipality", 16);
            put("Ichchhakamana Rural Municipality", 7);
        }});

        // Dadeldhura
        districtRegionWardMap.put("Dadeldhura", new HashMap<String, Integer>() {{
            put("Amargadhi Municipality", 11);
            put("Parashuram Municipality", 12);
            put("Aalital Rural Municipality", 8);
            put("Ajaymeru Rural Municipality", 6);
            put("Bhageshwar Rural Municipality", 5);
            put("Ganayapdhura Rural Municipality", 5);
            put("Nawadurga Rural Municipality", 5);
        }});

        // Dailekh
        districtRegionWardMap.put("Dailekh", new HashMap<String, Integer>() {{
            put("Aathabis Municipality", 8);
            put("Chamunda Bindrasaini Municipality", 9);
            put("Dullu Municipality", 13);
            put("Narayan Municipality", 11);
            put("Bhagawatimai Rural Municipality", 7);
            put("Bhairabi Rural Municipality", 7);
            put("Dungeshwar Rural Municipality", 6);
            put("Gurans Rural Municipality", 8);
            put("Mahabu Rural Municipality", 6);
            put("Naumule Rural Municipality", 6);
            put("Thantikandh Rural Municipality", 6);
        }});

        // Dang Deukhuri
        districtRegionWardMap.put("Dang Deukhuri", new HashMap<String, Integer>() {{
            put("Ghorahi Sub-Metropolitan City", 19);
            put("Tulsipur Sub-Metropolitan City", 19);
            put("Lamahi Municipality", 9);
            put("Banglachuli Rural Municipality", 8);
            put("Dangisharan Rural Municipality", 7);
            put("Gadhawa Rural Municipality", 8);
            put("Rajpur Rural Municipality", 7);
            put("Rapti Rural Municipality", 9);
            put("Shantinagar Rural Municipality", 7);
            put("Babai Rural Municipality", 7);
        }});

        // Darchula
        districtRegionWardMap.put("Darchula", new HashMap<String, Integer>() {{
            put("Mahakali Municipality", 9);
            put("Shailyashikhar Municipality", 9);
            put("Apihimal Rural Municipality", 6);
            put("Byas Rural Municipality", 6);
            put("Dunhu Rural Municipality", 5);
            put("Lekam Rural Municipality", 6);
            put("Malikarjun Rural Municipality", 8);
            put("Marma Rural Municipality", 6);
            put("Naugad Rural Municipality", 6);
        }});

        // Dhankuta
        districtRegionWardMap.put("Dhankuta", new HashMap<String, Integer>() {{
            put("Dhankuta Municipality", 10);
            put("Mahalaxmi Municipality", 9);
            put("Pakhribas Municipality", 10);
            put("Chaubise Rural Municipality", 8);
            put("Chhathar Jorpati Rural Municipality", 6);
            put("Sangurigadhi Rural Municipality", 10);
            put("Shahidbhumi Rural Municipality", 7);
        }});

        // Dhanusa
        districtRegionWardMap.put("Dhanusa", new HashMap<String, Integer>() {{
            put("Janakpurdham Sub-Metropolitan City", 25);
            put("Bideha Municipality", 9);
            put("Chhireshwarnath Municipality", 10);
            put("Dhanusadham Municipality", 9);
            put("Ganeshman Charnath Municipality", 11);
            put("Hansapur Municipality", 9);
            put("Kamala Municipality", 9);
            put("Mithila Municipality", 11);
            put("Nagarain Municipality", 9);
            put("Sahidnagar Municipality", 9);
            put("Sabaila Municipality", 13);
            put("Aaurahi Rural Municipality", 6);
            put("Bateshwor Rural Municipality", 5);
            put("Dhanauji Rural Municipality", 5);
            put("Janaknandini Rural Municipality", 6);
            put("Laxminiya Rural Municipality", 7);
            put("Mithila Bihari Rural Municipality", 10);
            put("Mukhiyapatti Musaharmiya Rural Municipality", 6);
        }});

        // Dhading
        districtRegionWardMap.put("Dhading", new HashMap<String, Integer>() {{
            put("Dhunibesi Municipality", 9);
            put("Nilakantha Municipality", 14);
            put("Benighat Rorang Rural Municipality", 10);
            put("Gajuri Rural Municipality", 8);
            put("Galchhi Rural Municipality", 8);
            put("Gangajamuna Rural Municipality", 7);
            put("Jwalamukhi Rural Municipality", 7);
            put("Khaniyabas Rural Municipality", 5);
            put("Netrawati Rural Municipality", 5);
            put("Rubi Valley Rural Municipality", 6);
            put("Siddhalek Rural Municipality", 7);
            put("Thakre Rural Municipality", 11);
            put("Tripura Sundari Rural Municipality", 7);
        }});

        // Dolakha
        districtRegionWardMap.put("Dolakha", new HashMap<String, Integer>() {{
            put("Bhimeshwor Municipality", 9);
            put("Jiri Municipality", 9);
            put("Baiteshwor Rural Municipality", 8);
            put("Bigu Rural Municipality", 8);
            put("Gaurishankar Rural Municipality", 9);
            put("Kalinchok Rural Municipality", 9);
            put("Melung Rural Municipality", 7);
            put("Sailung Rural Municipality", 8);
            put("Tamakoshi Rural Municipality", 7);
        }});

        // Dolpa
        districtRegionWardMap.put("Dolpa", new HashMap<String, Integer>() {{
            put("Thuli Bheri Municipality", 11);
            put("Tripurasundari Municipality", 11);
            put("Chharka Tangsong Rural Municipality", 6);
            put("Dolpo Buddha Rural Municipality", 6);
            put("Jagadulla Rural Municipality", 6);
            put("Kaike Rural Municipality", 7);
            put("Mugum Karmarong Rural Municipality", 9);
            put("Shey Phoksundo Rural Municipality", 9);
        }});

        // Doti
        districtRegionWardMap.put("Doti", new HashMap<String, Integer>() {{
            put("Dipayal Silgadi Municipality", 9);
            put("Shikhar Municipality", 11);
            put("Adarsha Rural Municipality", 7);
            put("Badikedar Rural Municipality", 5);
            put("Bogatan Phudsil Rural Municipality", 7);
            put("Jorayal Rural Municipality", 6);
            put("K.I. Singh Rural Municipality", 7);
            put("Purbichauki Rural Municipality", 7);
            put("Sayal Rural Municipality", 6);
        }});

        // Eastern Rukum
        districtRegionWardMap.put("Eastern Rukum", new HashMap<String, Integer>() {{
            put("Bhume Rural Municipality", 9);
            put("Putha Uttarganga Rural Municipality", 14);
            put("Sisne Rural Municipality", 8);
        }});

        // Gorkha
        districtRegionWardMap.put("Gorkha", new HashMap<String, Integer>() {{
            put("Gorkha Municipality", 14);
            put("Palungtar Municipality", 10);
            put("Ajirkot Rural Municipality", 5);
            put("Arughat Rural Municipality", 10);
            put("Bhimsenthapa Rural Municipality", 8);
            put("Chum Nubri Rural Municipality", 7);
            put("Dharche Rural Municipality", 7);
            put("Gandaki Rural Municipality", 8);
            put("Sahid Lakhan Rural Municipality", 9);
            put("Siranchok Rural Municipality", 8);
            put("Sulikot Rural Municipality", 8);
        }});

        // Gulmi
        districtRegionWardMap.put("Gulmi", new HashMap<String, Integer>() {{
            put("Musikot Municipality", 9);
            put("Resunga Municipality", 14);
            put("Chandrakot Rural Municipality", 8);
            put("Chatrakot Rural Municipality", 6);
            put("Dhurkot Rural Municipality", 7);
            put("Gulmi Darbar Rural Municipality", 7);
            put("Isma Rural Municipality", 6);
            put("Kaligandaki Rural Municipality", 7);
            put("Madane Rural Municipality", 7);
            put("Malika Rural Municipality", 8);
            put("Ruru Rural Municipality", 7);
            put("Satyawati Rural Municipality", 8);
        }});

        // Humla
        districtRegionWardMap.put("Humla", new HashMap<String, Integer>() {{
            put("Simkot Rural Municipality", 8);
            put("Adanchuli Rural Municipality", 6);
            put("Chankheli Rural Municipality", 6);
            put("Kharpunath Rural Municipality", 5);
            put("Namkha Rural Municipality", 6);
            put("Sarkegad Rural Municipality", 8);
            put("Tajakot Rural Municipality", 5);
        }});

        // Ilam
        districtRegionWardMap.put("Ilam", new HashMap<String, Integer>() {{
            put("Deumai Municipality", 9);
            put("Ilam Municipality", 12);
            put("Mai Municipality", 10);
            put("Suryodaya Municipality", 14);
            put("Chulachuli Rural Municipality", 6);
            put("Fakphokthum Rural Municipality", 7);
            put("Mai Jogmai Rural Municipality", 6);
            put("Mangsebung Rural Municipality", 6);
            put("Rong Rural Municipality", 6);
            put("Sandakpur Rural Municipality", 5);
        }});

        // Jajarkot
        districtRegionWardMap.put("Jajarkot", new HashMap<String, Integer>() {{
            put("Bheri Municipality", 13);
            put("Chhedagad Municipality", 13);
            put("Nalagad Municipality", 11);
            put("Barekot Rural Municipality", 9);
            put("Junichande Rural Municipality", 11);
            put("Kuse Rural Municipality", 9);
            put("Shivalaya Rural Municipality", 9);
        }});

        // Jhapa
        districtRegionWardMap.put("Jhapa", new HashMap<String, Integer>() {{
            put("Arjundhara Municipality", 11);
            put("Bhadrapur Municipality", 10);
            put("Birtamod Municipality", 10);
            put("Damak Municipality", 10);
            put("Gauradaha Municipality", 9);
            put("Kankai Municipality", 9);
            put("Mechinagar Municipality", 15);
            put("Shivasataxi Municipality", 11);
            put("Barhadashi Rural Municipality", 7);
            put("Buddhashanti Rural Municipality", 7);
            put("Gaurigunj Rural Municipality", 6);
            put("Haldibari Rural Municipality", 5);
            put("Jhapa Rural Municipality", 7);
            put("Kachanakawal Rural Municipality", 7);
            put("Kamal Rural Municipality", 7);
        }});

        // Jumla
        districtRegionWardMap.put("Jumla", new HashMap<String, Integer>() {{
            put("Chandannath Municipality", 10);
            put("Guthichaur Rural Municipality", 5);
            put("Hima Rural Municipality", 7);
            put("Kanakasundari Rural Municipality", 8);
            put("Patrasi Rural Municipality", 6);
            put("Sinja Rural Municipality", 6);
            put("Tatopani Rural Municipality", 8);
            put("Tila Rural Municipality", 9);
        }});

        // Kailali
        districtRegionWardMap.put("Kailali", new HashMap<String, Integer>() {{
            put("Dhangadhi Sub-Metropolitan City", 19);
            put("Bhajani Municipality", 9);
            put("Ghodaghodi Municipality", 12);
            put("Godawari Municipality", 12);
            put("Gauriganga Municipality", 11);
            put("Lamkichuha Municipality", 10);
            put("Tikapur Municipality", 9);
            put("Bardagoriya Rural Municipality", 6);
            put("Janaki Rural Municipality", 9);
            put("Joshipur Rural Municipality", 7);
            put("Kailari Rural Municipality", 9);
            put("Knowles Rural Municipality", 7);
            put("Mohanyal Rural Municipality", 7);
        }});

        // Kalikot
        districtRegionWardMap.put("Kalikot", new HashMap<String, Integer>() {{
            put("Khandachakra Municipality", 11);
            put("Raskot Municipality", 9);
            put("Tilagufa Municipality", 11);
            put("Kalika Rural Municipality", 8);
            put("Mahawai Rural Municipality", 7);
            put("Naraharinath Rural Municipality", 9);
            put("Pachaljharana Rural Municipality", 9);
            put("Palata Rural Municipality", 9);
            put("Sanni Tribeni Rural Municipality", 9);
        }});

        // Kanchanpur
        districtRegionWardMap.put("Kanchanpur", new HashMap<String, Integer>() {{
            put("Bedkot Municipality", 10);
            put("Belauri Municipality", 10);
            put("Bhimdatta Municipality", 19);
            put("Krishnapur Municipality", 9);
            put("Mahakali Municipality", 10);
            put("Punarbas Municipality", 11);
            put("Shuklaphanta Municipality", 12);
            put("Beldandi Rural Municipality", 7);
            put("Laljhadi Rural Municipality", 6);
        }});

        // Kapilvastu
        districtRegionWardMap.put("Kapilvastu", new HashMap<String, Integer>() {{
            put("Buddhabhumi Municipality", 10);
            put("Kapilvastu Municipality", 12);
            put("Krishnanagar Municipality", 12);
            put("Maharajgunj Municipality", 11);
            put("Shivaraj Municipality", 11);
            put("Banganga Municipality", 11);
            put("Bijaynagar Rural Municipality", 7);
            put("Mayadevi Rural Municipality", 8);
            put("Suddhodhan Rural Municipality", 6);
            put("Yashodhara Rural Municipality", 8);
        }});

        // Kaski
        districtRegionWardMap.put("Kaski", new HashMap<String, Integer>() {{
            put("Pokhara Metropolitan City", 33);
            put("Annapurna Rural Municipality", 11);
            put("Machhapuchhre Rural Municipality", 9);
            put("Madi Rural Municipality", 12);
            put("Rupa Rural Municipality", 7);
        }});

        // Kathmandu
        districtRegionWardMap.put("Kathmandu", new HashMap<String, Integer>() {{
            put("Kathmandu Metropolitan City", 32);
            put("Budhanilkantha Municipality", 13);
            put("Chandragiri Municipality", 15);
            put("Gokarneshwor Municipality", 9);
            put("Kageshwori Manohara Municipality", 9);
            put("Kirtipur Municipality", 10);
            put("Nagarjun Municipality", 10);
            put("Shankharapur Municipality", 9);
            put("Tarakeshwor Municipality", 10);
            put("Tokha Municipality", 11);
            put("Dakshinkali Rural Municipality", 7);
        }});

        // Kavrepalanchok
        districtRegionWardMap.put("Kavrepalanchok", new HashMap<String, Integer>() {{
            put("Banepa Municipality", 14);
            put("Dhulikhel Municipality", 12);
            put("Mandandeupur Municipality", 12);
            put("Namobuddha Municipality", 10);
            put("Panauti Municipality", 12);
            put("Panchkhal Municipality", 13);
            put("Bethanchowk Rural Municipality", 6);
            put("Bhumlu Rural Municipality", 10);
            put("Chaurideurali Rural Municipality", 9);
            put("Khanikhola Rural Municipality", 7);
            put("Mahabharat Rural Municipality", 8);
            put("Roshi Rural Municipality", 12);
            put("Temal Rural Municipality", 9);
        }});

        // Khotang
        districtRegionWardMap.put("Khotang", new HashMap<String, Integer>() {{
            put("Diktel Rupakot Majhuwagadhi Municipality", 15);
            put("Halesi Tuwachung Municipality", 11);
            put("Aiselukharka Rural Municipality", 7);
            put("Barahapokhari Rural Municipality", 6);
            put("Diprung Chuichumma Rural Municipality", 7);
            put("Jantedhunga Rural Municipality", 6);
            put("Kepilasgadhi Rural Municipality", 7);
            put("Khotehang Rural Municipality", 9);
            put("Rawa Besi Rural Municipality", 6);
            put("Sakela Rural Municipality", 5);
        }});

        // Lalitpur
        districtRegionWardMap.put("Lalitpur", new HashMap<String, Integer>() {{
            put("Lalitpur Metropolitan City", 29);
            put("Godawari Municipality", 14);
            put("Mahalaxmi Municipality", 10);
            put("Bagmati Rural Municipality", 7);
            put("Konjyosom Rural Municipality", 5);
            put("Mahankal Rural Municipality", 6);
        }});

        // Lamjung
        districtRegionWardMap.put("Lamjung", new HashMap<String, Integer>() {{
            put("Besisahar Municipality", 11);
            put("Madhya Nepal Municipality", 10);
            put("Rainas Municipality", 10);
            put("Sundarbazar Municipality", 11);
            put("Dordi Rural Municipality", 9);
            put("Dudhpokhari Rural Municipality", 6);
            put("Kwholasothar Rural Municipality", 9);
            put("Marsyangdi Rural Municipality", 9);
        }});

        // Mahottari
        districtRegionWardMap.put("Mahottari", new HashMap<String, Integer>() {{
            put("Aurahi Municipality", 9);
            put("Balwa Municipality", 11);
            put("Bardibas Municipality", 14);
            put("Bhangaha Municipality", 9);
            put("Gaushala Municipality", 12);
            put("Jaleswor Municipality", 14);
            put("Manra Siswa Municipality", 10);
            put("Matihani Municipality", 9);
            put("Ramgopalpur Municipality", 9);
            put("Ekdara Rural Municipality", 6);
            put("Loharpatti Rural Municipality", 9);
            put("Mahottari Rural Municipality", 6);
            put("Pipara Rural Municipality", 7);
            put("Samsi Rural Municipality", 7);
            put("Sonama Rural Municipality", 8);
        }});

        // Makwanpur
        districtRegionWardMap.put("Makwanpur", new HashMap<String, Integer>() {{
            put("Hetauda Sub-Metropolitan City", 19);
            put("Thaha Municipality", 12);
            put("Bagmati Rural Municipality", 9);
            put("Bakaiya Rural Municipality", 12);
            put("Bhimphedi Rural Municipality", 9);
            put("Indrasarowar Rural Municipality", 5);
            put("Kailash Rural Municipality", 10);
            put("Makawanpurgadhi Rural Municipality", 8);
            put("Manahari Rural Municipality", 9);
            put("Raksirang Rural Municipality", 9);
        }});

        // Manang
        districtRegionWardMap.put("Manang", new HashMap<String, Integer>() {{
            put("Chame Rural Municipality", 5);
            put("Narphu Rural Municipality", 5);
            put("Nashong Rural Municipality", 9);
            put("Nesyang Rural Municipality", 9);
        }});

        // Morang
        districtRegionWardMap.put("Morang", new HashMap<String, Integer>() {{
            put("Biratnagar Metropolitan City", 19);
            put("Belbari Municipality", 11);
            put("Letang Municipality", 9);
            put("Pathari Sanischare Municipality", 10);
            put("Rangeli Municipality", 9);
            put("Ratuwamai Municipality", 10);
            put("Sundarharaicha Municipality", 12);
            put("Sunwarshi Municipality", 9);
            put("Urlabari Municipality", 9);
            put("Budhiganga Rural Municipality", 7);
            put("Dhanapalthan Rural Municipality", 7);
            put("Gramthan Rural Municipality", 7);
            put("Jahada Rural Municipality", 7);
            put("Kanepokhari Rural Municipality", 7);
            put("Katahari Rural Municipality", 7);
            put("Kerabari Rural Municipality", 10);
            put("Miklajung Rural Municipality", 9);
        }});

        // Mugu
        districtRegionWardMap.put("Mugu", new HashMap<String, Integer>() {{
            put("Chhayanath Rara Municipality", 14);
            put("Khatyad Rural Municipality", 11);
            put("Mugum Karmarong Rural Municipality", 9);
            put("Soru Rural Municipality", 11);
        }});

        // Mustang
        districtRegionWardMap.put("Mustang", new HashMap<String, Integer>() {{
            put("Baragung Muktichhetra Rural Municipality", 5);
            put("Dalome Rural Municipality", 5);
            put("Gharapjhong Rural Municipality", 5);
            put("Lomanthang Rural Municipality", 5);
            put("Thasang Rural Municipality", 5);
        }});

        // Myagdi
        districtRegionWardMap.put("Myagdi", new HashMap<String, Integer>() {{
            put("Beni Municipality", 10);
            put("Annapurna Rural Municipality", 8);
            put("Dhaulagiri Rural Municipality", 7);
            put("Malika Rural Municipality", 7);
            put("Mangala Rural Municipality", 5);
            put("Raghuganga Rural Municipality", 8);
        }});

        // Nawalpur
        districtRegionWardMap.put("Nawalpur", new HashMap<String, Integer>() {{
            put("Devchuli Municipality", 17);
            put("Gaidakot Municipality", 18);
            put("Kawasoti Municipality", 17);
            put("Madhyabindu Municipality", 15);
            put("Binayi Triveni Rural Municipality", 6);
            put("Bulingtar Rural Municipality", 6);
            put("Hupsekot Rural Municipality", 6);
            put("Baudikali Rural Municipality", 7);
        }});

        // Nuwakot
        districtRegionWardMap.put("Nuwakot", new HashMap<String, Integer>() {{
            put("Belkotgadhi Municipality", 13);
            put("Bidur Municipality", 13);
            put("Dupcheshwar Rural Municipality", 7);
            put("Kakani Rural Municipality", 8);
            put("Kispang Rural Municipality", 5);
            put("Likhu Rural Municipality", 6);
            put("Meghang Rural Municipality", 6);
            put("Panchakanya Rural Municipality", 5);
            put("Shivapuri Rural Municipality", 8);
            put("Suryagadhi Rural Municipality", 5);
            put("Tadi Rural Municipality", 6);
            put("Tarakeshwor Rural Municipality", 6);
        }});

        // Okhaldhunga
        districtRegionWardMap.put("Okhaldhunga", new HashMap<String, Integer>() {{
            put("Siddhicharan Municipality", 12);
            put("Champadevi Rural Municipality", 10);
            put("Chisankhugadhi Rural Municipality", 8);
            put("Khijidemba Rural Municipality", 9);
            put("Likhu Rural Municipality", 9);
            put("Manebhanjyang Rural Municipality", 9);
            put("Molung Rural Municipality", 8);
            put("Sunkoshi Rural Municipality", 10);
        }});

        // Palpa
        districtRegionWardMap.put("Palpa", new HashMap<String, Integer>() {{
            put("Rampur Municipality", 10);
            put("Tansen Municipality", 14);
            put("Bagnaskali Rural Municipality", 9);
            put("Mathagadhi Rural Municipality", 8);
            put("Nisdi Rural Municipality", 7);
            put("Purbakhola Rural Municipality", 6);
            put("Rainadevi Chhahara Rural Municipality", 8);
            put("Rambha Rural Municipality", 5);
            put("Ribdikot Rural Municipality", 8);
            put("Tinau Rural Municipality", 6);
        }});

        // Panchthar
        districtRegionWardMap.put("Panchthar", new HashMap<String, Integer>() {{
            put("Phidim Municipality", 14);
            put("Falelung Rural Municipality", 8);
            put("Falgunanda Rural Municipality", 7);
            put("Hilihang Rural Municipality", 7);
            put("Kummayak Rural Municipality", 5);
            put("Miklajung Rural Municipality", 8);
            put("Tumbewa Rural Municipality", 5);
            put("Yangwarak Rural Municipality", 6);
        }});

        // Parasi
        districtRegionWardMap.put("Parasi", new HashMap<String, Integer>() {{
            put("Bardaghat Municipality", 16);
            put("Ramgram Municipality", 18);
            put("Sunwal Municipality", 13);
            put("Palhi Nandan Rural Municipality", 6);
            put("Pratappur Rural Municipality", 9);
            put("Sarawal Rural Municipality", 7);
            put("Susta Rural Municipality", 5);
        }});

        // Parbat
        districtRegionWardMap.put("Parbat", new HashMap<String, Integer>() {{
            put("Kushma Municipality", 14);
            put("Phalebas Municipality", 11);
            put("Bihadi Rural Municipality", 6);
            put("Jaljala Rural Municipality", 8);
            put("Mahashila Rural Municipality", 6);
            put("Modi Rural Municipality", 8);
            put("Paiyun Rural Municipality", 7);
        }});

        // Parsa
        districtRegionWardMap.put("Parsa", new HashMap<String, Integer>() {{
            put("Birgunj Metropolitan City", 32);
            put("Bahudaramai Municipality", 9);
            put("Parsagadhi Municipality", 9);
            put("Pokhariya Municipality", 10);
            put("Bindabasini Rural Municipality", 5);
            put("Chhipaharmai Rural Municipality", 5);
            put("Dhobini Rural Municipality", 5);
            put("Jagarnathpur Rural Municipality", 6);
            put("Jirabhawani Rural Municipality", 5);
            put("Kalikamai Rural Municipality", 5);
            put("Pakaha Mainpur Rural Municipality", 5);
            put("Paterwa Sugauli Rural Municipality", 5);
            put("Sakhuwa Prasauni Rural Municipality", 6);
            put("Thori Rural Municipality", 5);
        }});

        // Pyuthan
        districtRegionWardMap.put("Pyuthan", new HashMap<String, Integer>() {{
            put("Pyuthan Municipality", 10);
            put("Sworgadwari Municipality", 11);
            put("Aairawati Rural Municipality", 6);
            put("Gaumukhi Rural Municipality", 7);
            put("Jhimruk Rural Municipality", 8);
            put("Mallarani Rural Municipality", 5);
            put("Mandavi Rural Municipality", 5);
            put("Naubahini Rural Municipality", 6);
            put("Sarumarani Rural Municipality", 6);
        }});

        // Ramechhap
        districtRegionWardMap.put("Ramechhap", new HashMap<String, Integer>() {{
            put("Manthali Municipality", 14);
            put("Ramechhap Municipality", 9);
            put("Doramba Rural Municipality", 7);
            put("Gokulganga Rural Municipality", 6);
            put("Khandadevi Rural Municipality", 9);
            put("Likhu Tamakoshi Rural Municipality", 7);
            put("Sunapati Rural Municipality", 5);
            put("Umakunda Rural Municipality", 7);
        }});

        // Rasuwa
        districtRegionWardMap.put("Rasuwa", new HashMap<String, Integer>() {{
            put("Gosaikunda Rural Municipality", 6);
            put("Kalika Rural Municipality", 5);
            put("Naukunda Rural Municipality", 6);
            put("Parbatikunda Rural Municipality", 5);
            put("Uttargaya Rural Municipality", 5);
        }});

        // Rautahat
        districtRegionWardMap.put("Rautahat", new HashMap<String, Integer>() {{
            put("Baudhimai Municipality", 9);
            put("Brindaban Municipality", 9);
            put("Chandrapur Municipality", 10);
            put("Dewahi Gonahi Municipality", 9);
            put("Gadhimai Municipality", 9);
            put("Garuda Municipality", 9);
            put("Gaur Municipality", 9);
            put("Gujara Municipality", 9);
            put("Ishanath Municipality", 9);
            put("Madhav Narayan Municipality", 9);
            put("Maulapur Municipality", 9);
            put("Paroha Municipality", 9);
            put("Phatuwa Bijayapur Municipality", 11);
            put("Rajdevi Municipality", 9);
            put("Rajpur Municipality", 9);
            put("Katahariya Rural Municipality", 9);
            put("Pacharauta Rural Municipality", 9);
            put("Yemunamai Rural Municipality", 6);
        }});

        // Rolpa
        districtRegionWardMap.put("Rolpa", new HashMap<String, Integer>() {{
            put("Rolpa Municipality", 10);
            put("Gangadev Rural Municipality", 7);
            put("Lungri Rural Municipality", 7);
            put("Madi Rural Municipality", 7);
            put("Runtigadhi Rural Municipality", 9);
            put("Sukidaha Rural Municipality", 7);
            put("Sunchhahari Rural Municipality", 7);
            put("Suwarnabati Rural Municipality", 5);
            put("Thawang Rural Municipality", 5);
            put("Tribeni Rural Municipality", 7);
        }});

        // Rupandehi
        districtRegionWardMap.put("Rupandehi", new HashMap<String, Integer>() {{
            put("Butwal Sub-Metropolitan City", 19);
            put("Devdaha Municipality", 12);
            put("Lumbini Sanskritik Municipality", 13);
            put("Sainamaina Municipality", 11);
            put("Siddharthanagar Municipality", 13);
            put("Tillotama Municipality", 17);
            put("Gaidahawa Rural Municipality", 9);
            put("Kanchan Rural Municipality", 5);
            put("Kothimai Rural Municipality", 9);
            put("Marchawarimai Rural Municipality", 7);
            put("Mayadevi Rural Municipality", 8);
            put("Omsatiya Rural Municipality", 6);
            put("Rohini Rural Municipality", 7);
            put("Sammarimai Rural Municipality", 7);
            put("Siyari Rural Municipality", 7);
            put("Suddhodhan Rural Municipality", 7);
        }});

        // Salyan
        districtRegionWardMap.put("Salyan", new HashMap<String, Integer>() {{
            put("Bagchaur Municipality", 12);
            put("Bangad Municipality", 10);
            put("Sharada Municipality", 15);
            put("Chhatreshwori Rural Municipality", 7);
            put("Darma Rural Municipality", 6);
            put("Kalimati Rural Municipality", 7);
            put("Kapurkot Rural Municipality", 6);
            put("Kumakh Rural Municipality", 7);
            put("Siddha Kumakh Rural Municipality", 5);
            put("Tribeni Rural Municipality", 6);
        }});

        // Sankhuwasabha
        districtRegionWardMap.put("Sankhuwasabha", new HashMap<String, Integer>() {{
            put("Chainpur Municipality", 11);
            put("Dharmadevi Municipality", 9);
            put("Khandbari Municipality", 11);
            put("Madi Municipality", 9);
            put("Panchakhapan Municipality", 9);
            put("Bhotkhola Rural Municipality", 5);
            put("Chichila Rural Municipality", 5);
            put("Makalu Rural Municipality", 6);
            put("Sabhapokhari Rural Municipality", 6);
            put("Silichong Rural Municipality", 5);
        }});

        // Saptari
        districtRegionWardMap.put("Saptari", new HashMap<String, Integer>() {{
            put("Bode Barsain Municipality", 10);
            put("Dakneshwori Municipality", 10);
            put("Hanumannagar Kankalini Municipality", 14);
            put("Kanchanrup Municipality", 12);
            put("Khadak Municipality", 11);
            put("Rajbiraj Municipality", 16);
            put("Saptakoshi Municipality", 11);
            put("Shambhunath Municipality", 12);
            put("Surunga Municipality", 11);
            put("Agnisair Krishna Savaran Rural Municipality", 6);
            put("Balwa Rural Municipality", 7);
            put("Bishnupur Rural Municipality", 7);
            put("Chhinnamasta Rural Municipality", 7);
            put("Mahadeva Rural Municipality", 6);
            put("Rajgadh Rural Municipality", 6);
            put("Rupani Rural Municipality", 6);
            put("Tilathi Koiladi Rural Municipality", 8);
            put("Tirhut Rural Municipality", 7);
        }});

        // Sarlahi
        districtRegionWardMap.put("Sarlahi", new HashMap<String, Integer>() {{
            put("Bagmati Municipality", 12);
            put("Balara Municipality", 11);
            put("Barahathawa Municipality", 18);
            put("Godaita Municipality", 12);
            put("Hariwan Municipality", 11);
            put("Ishworpur Municipality", 16);
            put("Kabilasi Municipality", 10);
            put("Lalbandi Municipality", 17);
            put("Malangawa Municipality", 12);
            put("Basbariya Rural Municipality", 6);
            put("Bishnu Rural Municipality", 8);
            put("Brahmapuri Rural Municipality", 7);
            put("Chakraghatta Rural Municipality", 9);
            put("Chandranagar Rural Municipality", 7);
            put("Dhankaul Rural Municipality", 7);
            put("Haripurwa Rural Municipality", 9);
            put("Kaudena Rural Municipality", 7);
            put("Parsa Rural Municipality", 6);
            put("Ramnagar Rural Municipality", 7);
            put("Ramnagar Bahaurpur Rural Municipality", 7);
        }});

        // Sindhuli
        districtRegionWardMap.put("Sindhuli", new HashMap<String, Integer>() {{
            put("Dudhouli Municipality", 14);
            put("Kamalamai Municipality", 14);
            put("Fikkal Rural Municipality", 6);
            put("Ghyanglekh Rural Municipality", 5);
            put("Hariharpurgadhi Rural Municipality", 8);
            put("Marin Rural Municipality", 8);
            put("Sunkoshi Rural Municipality", 7);
            put("Tinpatan Rural Municipality", 11);
            put("Golanjor Rural Municipality", 7);
        }});

        // Sindhupalchok
        districtRegionWardMap.put("Sindhupalchok", new HashMap<String, Integer>() {{
            put("Barhabise Municipality", 9);
            put("Chautara Sangachokgadhi Municipality", 14);
            put("Melamchi Municipality", 13);
            put("Balefi Rural Municipality", 8);
            put("Bhotekoshi Rural Municipality", 5);
            put("Helambu Rural Municipality", 7);
            put("Indrawati Rural Municipality", 12);
            put("Jugal Rural Municipality", 7);
            put("Lisankhu Pakhar Rural Municipality", 7);
            put("Panchpokhari Thangpal Rural Municipality", 8);
            put("Sunkoshi Rural Municipality", 7);
            put("Tripurasundari Rural Municipality", 6);
        }});

        // Solukhumbu
        districtRegionWardMap.put("Solukhumbu", new HashMap<String, Integer>() {{
            put("Solududhkunda Municipality", 11);
            put("Khumbu Pasanglahmu Rural Municipality", 5);
            put("Likhu Pike Rural Municipality", 5);
            put("Mahakulung Rural Municipality", 5);
            put("Mapya Dudhkoshi Rural Municipality", 7);
            put("Necha Salyan Rural Municipality", 5);
            put("Sotang Rural Municipality", 5);
            put("Thulung Dudhkoshi Rural Municipality", 9);
        }});

        // Sunsari
        districtRegionWardMap.put("Sunsari", new HashMap<String, Integer>() {{
            put("Dharan Sub-Metropolitan City", 20);
            put("Itahari Sub-Metropolitan City", 20);
            put("Barahachhetra Municipality", 11);
            put("Duhabi Municipality", 12);
            put("Inaruwa Municipality", 10);
            put("Ramdhuni Municipality", 9);
            put("Barju Rural Municipality", 6);
            put("Bhokraha Narsingh Rural Municipality", 8);
            put("Dewangunj Rural Municipality", 7);
            put("Gadhi Rural Municipality", 6);
            put("Harinagar Rural Municipality", 7);
            put("Koshi Rural Municipality", 8);
        }});

        // Surkhet
        districtRegionWardMap.put("Surkhet", new HashMap<String, Integer>() {{
            put("Birendranagar Municipality", 16);
            put("Bheriganga Municipality", 13);
            put("Gurbhakot Municipality", 9);
            put("Lekbeshi Municipality", 10);
            put("Panchpuri Municipality", 11);
            put("Barahatal Rural Municipality", 10);
            put("Chaukune Rural Municipality", 10);
            put("Chingad Rural Municipality", 6);
            put("Simta Rural Municipality", 9);
        }});

        // Syangja
        districtRegionWardMap.put("Syangja", new HashMap<String, Integer>() {{
            put("Bhirkot Municipality", 9);
            put("Chapakot Municipality", 10);
            put("Galyang Municipality", 11);
            put("Putalibazar Municipality", 13);
            put("Waling Municipality", 14);
            put("Aandhikhola Rural Municipality", 6);
            put("Arjun Chaupari Rural Municipality", 6);
            put("Bheerkot Rural Municipality", 5);
            put("Biruwa Rural Municipality", 8);
            put("Harinas Rural Municipality", 7);
            put("Kaligandaki Rural Municipality", 7);
        }});

        // Tanahun
        districtRegionWardMap.put("Tanahun", new HashMap<String, Integer>() {{
            put("Bhanu Municipality", 13);
            put("Bhimad Municipality", 9);
            put("Byas Municipality", 14);
            put("Shuklagandaki Municipality", 12);
            put("Anbukhaireni Rural Municipality", 6);
            put("Bandipur Rural Municipality", 6);
            put("Devghat Rural Municipality", 5);
            put("Ghiring Rural Municipality", 5);
            put("Myagde Rural Municipality", 7);
            put("Rishing Rural Municipality", 8);
        }});

        // Taplejung
        districtRegionWardMap.put("Taplejung", new HashMap<String, Integer>() {{
            put("Phungling Municipality", 11);
            put("Aathrai Tribeni Rural Municipality", 8);
            put("Maiwakhola Rural Municipality", 6);
            put("Meringden Rural Municipality", 6);
            put("Mikwakhola Rural Municipality", 5);
            put("Pathibhara Yangwarak Rural Municipality", 6);
            put("Sidingba Rural Municipality", 7);
            put("Sirijangha Rural Municipality", 8);
        }});

        // Terhathum
        districtRegionWardMap.put("Terhathum", new HashMap<String, Integer>() {{
            put("Myanglung Municipality", 10);
            put("Laligurans Municipality", 9);
            put("Aathrai Rural Municipality", 7);
            put("Chhathar Rural Municipality", 6);
            put("Menchhayayem Rural Municipality", 6);
            put("Phedap Rural Municipality", 5);
        }});

        // Udayapur
        districtRegionWardMap.put("Udayapur", new HashMap<String, Integer>() {{
            put("Triyuga Municipality", 16);
            put("Katari Municipality", 14);
            put("Chaudandigadhi Municipality", 10);
            put("Belaka Municipality", 9);
            put("Rautamai Rural Municipality", 8);
            put("Tapli Rural Municipality", 5);
            put("Udayapurgadhi Rural Municipality", 8);
            put("Limchungbung Rural Municipality", 5);
        }});

        // Western Rukum
        districtRegionWardMap.put("Western Rukum", new HashMap<String, Integer>() {{
            put("Musikot Municipality", 14);
            put("Chaurjahari Municipality", 14);
            put("Aathbiskot Municipality", 14);
            put("Banphikot Rural Municipality", 10);
            put("Sani Bheri Rural Municipality", 11);
            put("Tribeni Rural Municipality", 10);
        }});
    }

    private void updateRegionTypeSpinner(String district) {
        List<String> regionTypes = new ArrayList<>();
        regionTypes.add("Select Region Type");

        if (!district.equals("Select District") && districtRegionWardMap.containsKey(district)) {
            regionTypes.addAll(districtRegionWardMap.get(district).keySet());
            Collections.sort(regionTypes.subList(1, regionTypes.size())); // Sort alphabetically, excluding "Select Region Type"
        }

        ArrayAdapter<String> regionTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, regionTypes);
        regionTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegionType.setAdapter(regionTypeAdapter);
        spinnerRegionType.setSelection(0); // Reset to "Select Region Type"
    }

    private void updateWardSpinner(String district, String regionType) {
        List<String> wards = new ArrayList<>();
        wards.add("Select Ward");

        if (!district.equals("Select District") && !regionType.equals("Select Region Type") &&
                districtRegionWardMap.containsKey(district) &&
                districtRegionWardMap.get(district).containsKey(regionType)) {
            int wardCount = districtRegionWardMap.get(district).get(regionType);
            for (int i = 1; i <= wardCount; i++) {
                wards.add(String.valueOf(i));
            }
        }

        ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, wards);
        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWard.setAdapter(wardAdapter);
        spinnerWard.setSelection(0); // Reset to "Select Ward"
    }
}