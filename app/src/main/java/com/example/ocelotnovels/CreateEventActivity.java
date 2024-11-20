package com.example.ocelotnovels;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ocelotnovels.utils.QRCodeUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {

    private EditText eventTitleEditText;
    private EditText eventDescriptionEditText;
    private EditText eventLocationEditText;
    private EditText dueDateEditText;
    private EditText capacityEditText;
    private Switch geolocationSwitch;
    private Switch limitWaitlistSwitch;
    private Button createButton;
    private Button cancelButton;
    private FirebaseFirestore db;
    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        eventTitleEditText = findViewById(R.id.event_title);
        eventDescriptionEditText = findViewById(R.id.event_description);
        eventLocationEditText = findViewById(R.id.event_location);
        dueDateEditText = findViewById(R.id.event_due_date);
        geolocationSwitch = findViewById(R.id.geolocation_switch);
        limitWaitlistSwitch = findViewById(R.id.limit_waitlist_switch);
        createButton = findViewById(R.id.create_button);
        cancelButton = findViewById(R.id.cancel_button);
        qrCodeImageView = findViewById(R.id.qr_code_image);
        capacityEditText = findViewById(R.id.event_capacity);

        // Set listeners for the switches
        limitWaitlistSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Disable the capacity field
                capacityEditText.setVisibility(View.GONE);
                capacityEditText.setText(""); // Clear the capacity input
            } else {
                // Enable the capacity field
                capacityEditText.setVisibility(View.VISIBLE);
            }
        });

        // Set listeners for the buttons
        createButton.setOnClickListener(v -> saveEventData());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void saveEventData() {
        String eventTitle = eventTitleEditText.getText().toString().trim();
        String eventDescription = eventDescriptionEditText.getText().toString().trim();
        String eventLocation = eventLocationEditText.getText().toString().trim();
        String dueDate = dueDateEditText.getText().toString().trim();
        boolean isGeolocationEnabled = geolocationSwitch.isChecked();
        boolean isLimitWaitlistEnabled = limitWaitlistSwitch.isChecked();

        // Validate input
        if (TextUtils.isEmpty(eventTitle) || TextUtils.isEmpty(eventDescription) ||
                TextUtils.isEmpty(eventLocation) || TextUtils.isEmpty(dueDate)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate capacity if "Limit Waitlist" is off
        String capacity = null;
        if (!isLimitWaitlistEnabled) {
            if (TextUtils.isEmpty(capacityEditText.getText().toString().trim())) {
                Toast.makeText(this, "Please enter a capacity", Toast.LENGTH_SHORT).show();
                return;
            }
            capacity = capacityEditText.getText().toString().trim();
        }

        // Generate a unique event ID
        String eventId = UUID.randomUUID().toString();

        // Create a map to store the event data
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", eventId);
        eventData.put("name", eventTitle);
        eventData.put("description", eventDescription);
        eventData.put("location", eventLocation);
        eventData.put("date", dueDate);
        eventData.put("geolocationEnabled", isGeolocationEnabled);
        eventData.put("limitWaitlistEnabled", isLimitWaitlistEnabled);

        if (!isLimitWaitlistEnabled) {
            eventData.put("capacity", Integer.parseInt(capacity)); // Add capacity to the database
        }

        // Generate QR Code and hash
        try {
            String qrData = eventId + "|" + eventTitle + "|" + eventLocation;
            Bitmap qrCodeBitmap = QRCodeUtils.generateQrCode(qrData, 500, 500);
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
            qrCodeImageView.setVisibility(View.VISIBLE);

            // Generate hash for the QR code data
            String qrHash = generateHash(qrData);

            // Add QR code hash to event data
            eventData.put("qrHash", qrHash);

            // Save event data to Firestore
            db.collection("events").document(eventId)
                    .set(eventData)
                    .addOnSuccessListener(aVoid -> {
                        // Navigate to QrCodeActivity
                        Intent intent = new Intent(CreateEventActivity.this, QrCodeActivity.class);
                        intent.putExtra("eventTitle", eventTitle);
                        intent.putExtra("eventDescription", eventDescription);
                        intent.putExtra("eventDeadline", dueDate);
                        intent.putExtra("eventLocation", eventLocation);
                        intent.putExtra("qrHash", qrHash);
                        intent.putExtra("qrCodeBitmap", qrCodeBitmap);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        showToast("Failed to create event.");
                        Log.e("Firestore Error", e.getMessage());
                    });

        } catch (WriterException | NoSuchAlgorithmException e) {
            Log.e("QR Code Error", "Error generating QR Code or Hash: " + e.getMessage());
            showToast("Error generating QR Code or Hash.");
        }
    }

    private String generateHash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hashBuilder = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hashBuilder.append('0');
            hashBuilder.append(hex);
        }
        return hashBuilder.toString();
    }

    private void showToast(String message) {
        Toast.makeText(CreateEventActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}