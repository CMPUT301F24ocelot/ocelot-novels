package com.example.ocelotnovels;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ocelotnovels.model.Facility;
import com.example.ocelotnovels.view.Organizer.OrganizerMainActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Activity for creating and updating facility profiles.
 */
public class FacilityProfileActivity extends AppCompatActivity {

    private EditText facilityName, facilityEmail, facilityPhone, facilityLocation, facilityDescription;
    private ImageView facilityProfileImage;
    private Button facilitySaveButton, facilityCancelButton;
    private FirebaseFirestore db;

    /**
     * Called when the activity is created. Initializes the UI components and sets up button click listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this contains the data it most recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_profile_activity);

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
        facilityCancelButton = findViewById(R.id.organizer_cancel_button);

        // Set up save button functionality
        facilitySaveButton.setOnClickListener(v -> saveFacilityProfile());

        // Set up cancel button functionality
        facilityCancelButton.setOnClickListener(v -> navigateBack());
    }

    /**
     * Navigates back to the OrganizerMainActivity.
     */
    private void navigateBack() {
        Intent intent = new Intent(FacilityProfileActivity.this, OrganizerMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Validates the user input and saves the facility profile to Firestore.
     */
    private void saveFacilityProfile() {
        // Retrieve and validate data from input fields
        String name = facilityName.getText().toString().trim();
        String email = facilityEmail.getText().toString().trim();
        String phone = facilityPhone.getText().toString().trim();
        String location = facilityLocation.getText().toString().trim();
        String description = facilityDescription.getText().toString().trim();

        // Assuming ownerId is derived from the current user's ID (replace with actual logic)
        String ownerId = "sampleOwnerId";  // Replace this with actual owner ID logic
        String facilityId = UUID.randomUUID().toString();

        // Validate input fields
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

        // Convert Facility object to a Map for Firestore
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityId", facility.getFacilityId());
        facilityData.put("ownerId", facility.getOwnerId());
        facilityData.put("facilityName", facility.getFacilityName());
        facilityData.put("facilityEmail", facility.getFacilityEmail());
        facilityData.put("facilityPhone", facility.getFacilityPhone());
        facilityData.put("facilityLocation", facility.getFacilityLocation());
        facilityData.put("facilityDescription", facility.getFacilityDescription());

        // Save to Firestore under "facilities" collection
        db.collection("facilities").document(facility.getFacilityId())
                .set(facilityData)
                .addOnSuccessListener(aVoid -> {
                    showToast("Facility profile saved successfully.");
                    navigateBack(); // Navigate back after saving
                })
                .addOnFailureListener(e -> showToast("Failed to save facility profile."));
    }

    /**
     * Displays a toast message to the user.
     *
     * @param message The message to be displayed.
     */
    private void showToast(String message) {
        Toast.makeText(FacilityProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
