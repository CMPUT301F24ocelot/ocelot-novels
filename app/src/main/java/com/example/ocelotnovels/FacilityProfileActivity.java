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

import java.util.HashMap;
import java.util.Map;
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


        // Set up save button funct[ionality
        facilitySaveButton.setOnClickListener(v -> saveFacilityProfile());
    }


    private void saveFacilityProfile() {
        // Retrieve and validate data from input fields
        String name = facilityName.getText().toString().trim();
        String email = facilityEmail.getText().toString().trim();
        String phone = facilityPhone.getText().toString().trim();
        String location = facilityLocation.getText().toString().trim();
        String description = facilityDescription.getText().toString().trim();
        String facilityId = UUID.randomUUID().toString();

        if (TextUtils.isEmpty(name) || name.length() > 100) {
            showToast("Facility name must be between 1 and 100 characters.");
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address.");
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.length() != 10 || !TextUtils.isDigitsOnly(phone)) {
            showToast("Phone number must be 10 digits.");
            return;
        }

        if (TextUtils.isEmpty(location)) {
            showToast("Location cannot be empty.");
            return;
        }

        if (TextUtils.isEmpty(description) || description.length() < 20) {
            showToast("Description should be at least 20 characters.");
            return;
        }

        // Create a Facility instance
        Facility facility = new Facility(ownerId, name, email, phone, location, description);
        facility.setFacilityId(facilityId);

        // Convert Facility object to a Map for Firestore
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityId", facility.getFacilityId());
        facilityData.put("ownerId", facility.getOwnerId());
        facilityData.put("facilityName", facility.getFacilityName());
        facilityData.put("facilityEmail", facility.getFacilityEmail());
        facilityData.put("facilityPhone", facility.getFacilityPhone());
        facilityData.put("facilityLocation", facility.getFacilityLocation());
        facilityData.put("facilityDescription", facility.getFacilityDescription());

        db.collection("facilities").document(facility.getFacilityId())
                .set(facilityData)
                .addOnSuccessListener(aVoid -> showToast("Facility profile saved successfully."))
                .addOnFailureListener(e -> showToast("Failed to save facility profile: "));
    }

    private void showToast(String message) {
        Toast.makeText(FacilityProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}

