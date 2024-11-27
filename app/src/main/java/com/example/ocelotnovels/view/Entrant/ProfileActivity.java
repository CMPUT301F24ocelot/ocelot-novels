package com.example.ocelotnovels.view.Entrant;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ocelotnovels.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.*;

import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private EditText nameEditText, emailEditText, phoneEditText;
    private ImageView profileImageView;
    private Button uploadProfilePicButton, removeProfilePicButton, saveButton;
    private Switch notificationsSwitch;

    private FirebaseFirestore db;
    private FirebaseUtils firebaseUtils;
    private StorageReference storageRef;
    private String deviceId;


    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_profile_activity);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        firebaseUtils = new FirebaseUtils(this);
        deviceId = firebaseUtils.getDeviceId(this);

        // Initialize Views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        profileImageView = findViewById(R.id.profileImageView);
        uploadProfilePicButton = findViewById(R.id.uploadProfilePicButton);
        removeProfilePicButton = findViewById(R.id.removeProfilePicButton);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        saveButton = findViewById(R.id.saveButton);

        // Load User Data
        loadUserData();

        // Set Listeners
        uploadProfilePicButton.setOnClickListener(v -> selectProfilePicture());
        removeProfilePicButton.setOnClickListener(v -> removeProfilePicture());
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleNotifications(isChecked);
        });
        saveButton.setOnClickListener(v -> saveChanges());
    }

    private void loadUserData() {
        db.collection("users").document(deviceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nameEditText.setText(documentSnapshot.getString("name"));
                        emailEditText.setText(documentSnapshot.getString("email"));
                        phoneEditText.setText(documentSnapshot.getString("phone"));
                        Boolean notificationsEnabled = documentSnapshot.getBoolean("notificationsEnabled");
                        if (notificationsEnabled != null) {
                            notificationsSwitch.setChecked(notificationsEnabled);
                        }
                        String profilePicUrl = documentSnapshot.getString("profilePicUrl");
//                        if (profilePicUrl != null) {
//                            // Load profile picture from URL
//                            // Use a library like Picasso or Glide
//                            Glide.with(this).load(profilePicUrl).into(profileImageView);
//                        }
                    }
                });
    }

    private void toggleNotifications(boolean isEnabled) {
        db.collection("users").document(deviceId)
                .update("notificationsEnabled", isEnabled)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = isEnabled ? "Notifications enabled!" : "Notifications disabled!";
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update notification preference.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void selectProfilePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
                uploadProfilePictureToFirebase(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfilePictureToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference profilePicRef = storageRef.child("profile_pictures/" + deviceId + ".jpg");
        profilePicRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> updateUserProfilePicUrl(uri.toString())));
    }

    private void updateUserProfilePicUrl(String url) {
        db.collection("users").document(deviceId).update("profilePicUrl", url)
                .addOnCompleteListener(task -> Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show());
    }

    private void removeProfilePicture() {
        db.collection("users").document(deviceId).update("profilePicUrl", null)
                .addOnCompleteListener(task -> {
                    profileImageView.setImageResource(R.drawable.ic_image_placeholder);
                    Toast.makeText(this, "Profile picture removed!", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveChanges() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", nameEditText.getText().toString().trim());
        updates.put("email", emailEditText.getText().toString().trim());
        updates.put("phone", phoneEditText.getText().toString().trim());

        db.collection("users").document(deviceId).update(updates)
                .addOnCompleteListener(task -> Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show());
    }
}

