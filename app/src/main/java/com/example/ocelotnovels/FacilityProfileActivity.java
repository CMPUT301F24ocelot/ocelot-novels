package com.example.ocelotnovels;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class FacilityProfileActivity extends AppCompatActivity {

    private EditText facilityName, facilityEmail, facilityPhone, facilityLocation, facilityDescription;
    private ImageView facilityProfileImage;
    private Button facilitySaveButton;
    private FirebaseUtils facilityFirebaseUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_profile_activity);

        // Initialize FirebaseUtils
        facilityFirebaseUtils = new FirebaseUtils(this);

        // Initialize views
        facilityName = findViewById(R.id.organizer_facility_name);
        facilityEmail = findViewById(R.id.organizer_email);
        facilityPhone = findViewById(R.id.organizer_phone);
        facilityLocation = findViewById(R.id.organizer_location);
        facilityDescription = findViewById(R.id.organizer_description);
        facilityProfileImage = findViewById(R.id.organizer_profile_image);
        facilitySaveButton = findViewById(R.id.organizer_save_button);

        // Load user data into views (if any)
        loadFacilityData();

        // Set up save button functionality
        facilitySaveButton.setOnClickListener(v -> saveFacilityProfile());
    }

    private void loadFacilityData() {
       facilityName.setText("Facility");
        facilityEmail.setText("email");
        facilityPhone.setText("Phone no.");
        facilityLocation.setText("Location");
        facilityDescription.setText("Description");
    }

    private void saveFacilityProfile() {
        // Retrieve data from the input fields
        String name = facilityName.getText().toString().trim();
        String email = facilityEmail.getText().toString().trim();
        String phone = facilityPhone.getText().toString().trim();
        String location = facilityLocation.getText().toString().trim();
        String description = facilityDescription.getText().toString().trim();

        // Create a Map to store the data
        Map<String, Object> facilityProfileData = new HashMap<>();
        facilityProfileData.put("facilityName", name);
        facilityProfileData.put("facilityEmail", email);
        facilityProfileData.put("facilityPhone", phone);
        facilityProfileData.put("facilityLocation", location);
        facilityProfileData.put("facilityDescription", description);

        // Get the DocumentReference from FirebaseUtils and save the data
        DocumentReference docRef = facilityFirebaseUtils.getDocument();
        docRef.set(facilityProfileData)
                .addOnSuccessListener(aVoid -> {
                    // Notify the user that the data was saved
                    Toast.makeText(this, "Facility profile saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Notify the user in case of a failure
                    Toast.makeText(this, "Failed to save facility profile", Toast.LENGTH_SHORT).show();
                });
    }
}
