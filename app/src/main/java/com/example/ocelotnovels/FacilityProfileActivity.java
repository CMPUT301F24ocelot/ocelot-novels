package com.example.ocelotnovels;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ocelotnovels.model.Facility;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FacilityProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText facilityName, facilityEmail, facilityPhone, facilityLocation, facilityDescription;
    private ImageView facilityProfileImage;
    private Button uploadProfilePicButton, removeProfilePicButton, saveButton;

    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String facilityId; // Facility ID for Firestore document
    private String ownerId;    // Owner ID of the facility

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_profile_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Assuming ownerId is derived from the current user's ID (replace this with actual logic)
        ownerId = "sampleOwnerId"; // Replace with actual logic
        facilityId = FirebaseUtils.getInstance(this).getDeviceId(this); // Unique ID for facility

        // Initialize views
        facilityName = findViewById(R.id.organizer_facility_name);
        facilityEmail = findViewById(R.id.organizer_email);
        facilityPhone = findViewById(R.id.organizer_phone);
        facilityLocation = findViewById(R.id.organizer_location);
        facilityDescription = findViewById(R.id.organizer_description);
        facilityProfileImage = findViewById(R.id.organizer_profile_image);
        uploadProfilePicButton = findViewById(R.id.uploadFacilityProfilePicButton);
        removeProfilePicButton = findViewById(R.id.removeFacilityProfilePicButton);
        saveButton = findViewById(R.id.organizer_save_button);

        // Load Facility Data
        loadFacilityData();

        // Set listeners
        uploadProfilePicButton.setOnClickListener(v -> selectFacilityPicture());
        removeProfilePicButton.setOnClickListener(v -> removeFacilityPicture());
        saveButton.setOnClickListener(v -> saveFacilityProfile());
    }

    /**
     * Loads the facility data from Firestore.
     */
    private void loadFacilityData() {
        db.collection("facilities").document(facilityId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        facilityName.setText(documentSnapshot.getString("facilityName"));
                        facilityEmail.setText(documentSnapshot.getString("facilityEmail"));
                        facilityPhone.setText(documentSnapshot.getString("facilityPhone"));
                        facilityLocation.setText(documentSnapshot.getString("facilityLocation"));
                        facilityDescription.setText(documentSnapshot.getString("facilityDescription"));
                        String profilePicUrl = documentSnapshot.getString("facilityPicUrl");
                        if (profilePicUrl != null) {
                            Glide.with(this).load(profilePicUrl).into(facilityProfileImage);
                        }
                    }
                });
    }

    /**
     * Opens an intent to allow the user to select a picture from their device gallery.
     */
    private void selectFacilityPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                facilityProfileImage.setImageBitmap(bitmap);
                uploadFacilityPictureToFirebase(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Uploads the selected facility picture to Firebase Storage.
     *
     * @param bitmap The selected picture.
     */
    private void uploadFacilityPictureToFirebase(Bitmap bitmap) {
        String pictureId = facilityId + "_profile.jpg"; // Unique picture name based on facility ID
        StorageReference profilePicRef = storageRef.child("facility_pictures/" + pictureId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        profilePicRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> updateFacilityProfilePicUrl(uri.toString()))
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to get image URL.", Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload picture.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Updates the facility's profile picture URL in Firestore.
     *
     * @param url The picture URL.
     */
    private void updateFacilityProfilePicUrl(String url) {
        db.collection("facilities").document(facilityId).update("facilityPicUrl", url)
                .addOnCompleteListener(task -> Toast.makeText(this, "Facility picture updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update facility picture.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Removes the facility's profile picture from Firestore and resets the ImageView.
     */
    private void removeFacilityPicture() {
        db.collection("facilities").document(facilityId).update("facilityPicUrl", null)
                .addOnCompleteListener(task -> {
                    facilityProfileImage.setImageResource(R.drawable.ic_image_placeholder); // Reset to placeholder
                    Toast.makeText(this, "Facility picture removed!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to remove facility picture.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Validates and saves the facility profile to Firestore.
     */
    private void saveFacilityProfile() {
        String name = facilityName.getText().toString().trim();
        String email = facilityEmail.getText().toString().trim();
        String phone = facilityPhone.getText().toString().trim();
        String location = facilityLocation.getText().toString().trim();
        String description = facilityDescription.getText().toString().trim();

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

        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityId", facilityId);
        facilityData.put("ownerId", ownerId);
        facilityData.put("facilityName", name);
        facilityData.put("facilityEmail", email);
        facilityData.put("facilityPhone", phone);
        facilityData.put("facilityLocation", location);
        facilityData.put("facilityDescription", description);

        db.collection("facilities").document(facilityId).set(facilityData)
                .addOnSuccessListener(aVoid -> showToast("Facility profile saved successfully."))
                .addOnFailureListener(e -> showToast("Failed to save facility profile."));
    }

    /**
     * Displays a toast message to the user.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(FacilityProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}