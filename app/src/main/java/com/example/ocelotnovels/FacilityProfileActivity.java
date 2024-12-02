/**
 * FacilityProfileActivity
 *
 * This activity handles the creation, viewing, and updating of facility profiles
 * for a community event management application. Users can:
 * - View and edit facility details such as name, email, phone, location, and description.
 * - Upload, view, and remove a profile picture for the facility.
 * - Save changes to Firebase Firestore and Firebase Storage.
 *
 * Key Functionalities:
 * 1. Load facility data from Firestore.
 * 2. Allow users to update profile details.
 * 3. Handle uploading and removing facility profile pictures.
 * 4. Validate user inputs before saving data.
 *
 * Dependencies:
 * - Firebase Firestore and Firebase Storage for backend data management.
 * - Glide for image loading and caching.
 * - Android standard libraries for UI and interaction.
 */

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
import com.example.ocelotnovels.view.Organizer.OrganizerMainActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FacilityProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText facilityName, facilityEmail, facilityPhone, facilityLocation, facilityDescription;
    private ImageView facilityProfileImage;
    private Button uploadProfilePicButton, removeProfilePicButton, saveButton;
    private String facilityProfilePicUrl;
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
        facilityId = FirebaseUtils.getInstance(this).getFacilityId(this); // Unique ID for facility

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

                        // Load profile picture URL
                        String profilePicUrl = documentSnapshot.getString("facilityPicUrl");
                        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(profilePicUrl)
                                    .placeholder(R.drawable.ic_image_placeholder) // Placeholder while loading
                                    .error(R.drawable.ic_image_placeholder)      // Fallback image on error
                                    .into(facilityProfileImage);
                        } else {
                            // Default picture logic
                            facilityProfileImage.setImageResource(R.drawable.ic_image_placeholder);
                        }
                    } else {
                        Toast.makeText(this, "Facility data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load facility data.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
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
                Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .into(facilityProfileImage);

                // Upload the picture and handle the URL
                uploadFacilityPictureToFirebase(bitmap, url -> {
                    facilityProfilePicUrl = url; // Save the URL for later use
                    updateFacilityProfilePicUrl(facilityProfilePicUrl); // Update Firestore with the new URL
                });
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Failed to load image.");
            }
        }
    }

    /**
     * Uploads the selected facility picture to Firebase Storage.
     *
     * @param bitmap The selected picture.
     */
    private void uploadFacilityPictureToFirebase(Bitmap bitmap, OnPictureUploadedListener listener) {
        String pictureId = UUID.randomUUID().toString(); // Unique identifier for the picture
        StorageReference profilePicRef = storageRef.child("images/facilities/" + pictureId + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        profilePicRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            listener.onPictureUploaded(uri.toString());
                            showToast("Profile picture uploaded successfully!");
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            showToast("Failed to retrieve image URL.");
                        }))
                .addOnFailureListener(e -> {
                    e.printStackTrace();

                });
    }


    /**
     * Updates the facility's profile picture URL in Firestore.
     *
     * @param url The picture URL.
     */
    private void updateFacilityProfilePicUrl(String url) {
        db.collection("facilities").document(facilityId).update("facilityPicUrl", url)
                .addOnCompleteListener(task -> Toast.makeText(this, "Facility picture updated!", Toast.LENGTH_SHORT).show());
                //.addOnFailureListener(e -> Toast.makeText(this, "Failed to update facility picture.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Removes the facility's profile picture from Firestore and resets the ImageView.
     */
    /**
     * Removes the facility's profile picture from Firebase Storage and Firestore, and resets the ImageView.
     */
    private void removeFacilityPicture() {
        // Check if there is a profile picture URL to remove
        if (facilityProfilePicUrl != null && !facilityProfilePicUrl.isEmpty()) {
            // Extract the path from the URL and create a reference to the storage
            StorageReference profilePicRef = FirebaseStorage.getInstance().getReferenceFromUrl(facilityProfilePicUrl);

            // Delete the image from Firebase Storage
            profilePicRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // Reset the ImageView to the placeholder
                        facilityProfileImage.setImageResource(R.drawable.ic_image_placeholder);

                        // Clear the local profile picture URL variable
                        facilityProfilePicUrl = null;

                        // Remove the URL from Firestore
                        db.collection("facilities").document(facilityId)
                                .update("facilityPicUrl", null)
                                .addOnSuccessListener(task -> {
                                    showToast("Facility picture removed successfully.");
                                })
                                .addOnFailureListener(e -> {
                                    showToast("Failed to update Firestore.");
                                });
                    })
                    .addOnFailureListener(e -> {
                        showToast("Failed to remove facility picture from storage.");
                    });
        } else {
            // If no picture URL is set, reset the ImageView
            facilityProfileImage.setImageResource(R.drawable.ic_image_placeholder);
            showToast("No profile picture to remove.");
        }
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
        String currentDeviceId = FirebaseUtils.getInstance(this).getDeviceId(this);

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

        // Prepare facility data
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityId", facilityId);
        facilityData.put("ownerId", currentDeviceId);
        facilityData.put("facilityName", name);
        facilityData.put("facilityEmail", email);
        facilityData.put("facilityPhone", phone);
        facilityData.put("facilityLocation", location);
        facilityData.put("facilityDescription", description);

        // Check if a profile picture is uploaded or already exists
        if (facilityProfileImage.getDrawable() != null) {
            facilityProfileImage.setDrawingCacheEnabled(true);
            facilityProfileImage.buildDrawingCache();
            Bitmap bitmap = facilityProfileImage.getDrawingCache();

            if (bitmap != null) {
                uploadFacilityPictureToFirebase(bitmap, url -> {
                    facilityData.put("facilityPicUrl", url);
                    saveFacilityDataToFirestore(facilityData);
                });
            } else {
                // Save without a picture if no bitmap is available
                saveFacilityDataToFirestore(facilityData);
            }
        } else {
            saveFacilityDataToFirestore(facilityData);
        }
    }

    private void saveFacilityDataToFirestore(Map<String, Object> facilityData) {
        db.collection("facilities").document(facilityId)
                .set(facilityData)
                .addOnSuccessListener(aVoid -> {
                    showToast("Facility profile saved successfully.");

                    // Navigate to OrganizerMainActivity
                    Intent intent = new Intent(FacilityProfileActivity.this, OrganizerMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear back stack
                    startActivity(intent);
                    finish(); // Close FacilityProfileActivity to prevent going back to it
                })
                .addOnFailureListener(e -> showToast("Failed to save facility profile."));
    }



    private interface OnPictureUploadedListener {
        void onPictureUploaded(String url);
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