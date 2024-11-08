package com.example.ocelotnovels;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ocelotnovels.model.Facility;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class FacilityProfileActivity extends AppCompatActivity {

    private EditText facilityName, facilityEmail, facilityPhone, facilityLocation, facilityDescription, ownerId;
    private ImageView facilityProfileImage;
    private Button facilitySaveButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_profile_activity);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        facilityName = findViewById(R.id.organizer_facility_name);
        facilityEmail = findViewById(R.id.organizer_email);
        facilityPhone = findViewById(R.id.organizer_phone);
        facilityLocation = findViewById(R.id.organizer_location);
        facilityDescription = findViewById(R.id.organizer_description);
        facilityProfileImage = findViewById(R.id.organizer_profile_image);
        facilitySaveButton = findViewById(R.id.organizer_save_button);

        // Load any existing data for the facility, if available
        loadFacilityData();

        // Set up save button functionality
        facilitySaveButton.setOnClickListener(v -> saveFacilityProfile());
    }

    private void loadFacilityData() {
        // Placeholder data, replace with actual data retrieval
        facilityName.setText("Default Facility Name");
        facilityEmail.setText("email@example.com");
        facilityPhone.setText("1234567890");
        facilityLocation.setText("Default Location");
        facilityDescription.setText("Default description");
    }

    private void saveFacilityProfile() {
        // Retrieve and validate data from input fields
        String name = facilityName.getText().toString().trim();
        String email = facilityEmail.getText().toString().trim();
        String phone = facilityPhone.getText().toString().trim();
        String location = facilityLocation.getText().toString().trim();
        String description = facilityDescription.getText().toString().trim();
        String facilityId = UUID.randomUUID().toString();
    }}