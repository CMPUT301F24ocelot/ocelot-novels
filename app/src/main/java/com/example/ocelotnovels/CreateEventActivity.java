package com.example.ocelotnovels;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ocelotnovels.utils.FirebaseUtils;
import com.example.ocelotnovels.utils.QRCodeUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Activity for creating a new event.
 * Allows the organizer to input event details, generate a QR code for the event,
 * and upload the details to Firestore and Firebase Storage.
 */
public class CreateEventActivity extends AppCompatActivity {

    private EditText eventTitleEditText;
    private EditText eventDescriptionEditText;
    private EditText eventLocationEditText;
    private EditText capacityEditText;
    private Button dueDateButton;
    private Switch geolocationSwitch;
    private Switch limitWaitlistSwitch;
    private Button createButton;
    private Button cancelButton;
    private FirebaseFirestore db;

    // QR code related views
    private ImageView qrCodeImageView;
    private TextView eventTitleText;
    private TextView eventDescriptionText;
    private TextView eventDeadlineText;
    private TextView eventStatusText;
    private Button backButton;

    // State variables
    private String selectedDate = "";
    private boolean isQrCodeDisplayed = false;

    // Lists for event data
    private List<String> waitList;
    private List<String> selectedList;
    private List<String> cancelledList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firestore and UI components
        db = FirebaseFirestore.getInstance();

        // Initialize views and lists
        initializeViews();

        // Set up listeners
        setupListeners();
    }

    private void initializeViews() {
        eventTitleEditText = findViewById(R.id.event_title);
        eventDescriptionEditText = findViewById(R.id.event_description);
        eventLocationEditText = findViewById(R.id.event_location);
        capacityEditText = findViewById(R.id.event_capacity);
        dueDateButton = findViewById(R.id.event_due_date);
        geolocationSwitch = findViewById(R.id.geolocation_switch);
        limitWaitlistSwitch = findViewById(R.id.limit_waitlist_switch);
        createButton = findViewById(R.id.create_button);
        cancelButton = findViewById(R.id.cancel_button);

        // Initialize lists
        waitList = new ArrayList<>();
        selectedList = new ArrayList<>();
        cancelledList = new ArrayList<>();
    }

    private void setupListeners() {
        dueDateButton.setOnClickListener(v -> showDatePickerDialog());
        createButton.setOnClickListener(v -> saveEventData());
        cancelButton.setOnClickListener(v -> finish());

        limitWaitlistSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            capacityEditText.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            if (isChecked) {
                capacityEditText.setText("");
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    dueDateButton.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void saveEventData() {
        if (!validateInputs()) {
            return;
        }

        String eventId = UUID.randomUUID().toString();
        Map<String, Object> eventData = createEventData(eventId);

        try {
            Bitmap qrCodeBitmap = QRCodeUtils.generateQrCode(eventId, 500, 500);

            // Compute QR code hash
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] qrCodeBytes = baos.toByteArray();
            String qrCodeHash = computeHash(qrCodeBytes);

            // Add the hash to event data
            eventData.put("qrCodeHash", qrCodeHash);

            // Save event data to Firestore
            db.collection("events").document(eventId)
                    .set(eventData)
                    .addOnSuccessListener(aVoid -> {
                        showToast("Event created successfully.");
                        displayQrCode(eventId, qrCodeBitmap, eventData);
                        uploadQrCodeToStorage(eventId, qrCodeBitmap);
                    })
                    .addOnFailureListener(e -> showToast("Failed to create event."));
        } catch (WriterException e) {
            Log.e("QR Code Error", "Error generating QR Code: " + e.getMessage());
            showToast("Failed to generate QR Code.");
        }
    }

    private void displayQrCode(String eventId, Bitmap qrCodeBitmap, Map<String, Object> eventData) {
        // Switch to the QR Code display layout
        setContentView(R.layout.organizer_qr_code);

        // Initialize views in the new layout
        ImageView qrCodeImageView = findViewById(R.id.event_qr_code_image);
        TextView eventTitleText = findViewById(R.id.event_title);
        TextView eventDescriptionText = findViewById(R.id.event_description);
        TextView eventDeadlineText = findViewById(R.id.event_deadline);
        TextView eventStatusText = findViewById(R.id.event_status);
        Button backButton = findViewById(R.id.back_button);

        // Populate the QR Code and event details
        qrCodeImageView.setImageBitmap(qrCodeBitmap);
        eventTitleText.setText("Event Title: " + eventData.get("name"));
        eventDescriptionText.setText("Event Description: " + eventData.get("description"));
        eventDeadlineText.setText("Event Deadline: " + eventData.get("regClosed"));
        eventStatusText.setText("Event Status: Active");

        // Handle back button click to finish activity
        backButton.setOnClickListener(v -> finish());
    }


    private boolean validateInputs() {
        String eventTitle = eventTitleEditText.getText().toString().trim();
        String eventDescription = eventDescriptionEditText.getText().toString().trim();
        String eventLocation = eventLocationEditText.getText().toString().trim();
        String capacity = capacityEditText.getText().toString().trim();
        boolean isLimitWaitlistEnabled = limitWaitlistSwitch.isChecked();

        if (TextUtils.isEmpty(eventTitle) || TextUtils.isEmpty(eventDescription) ||
                TextUtils.isEmpty(eventLocation) || TextUtils.isEmpty(selectedDate)) {
            showToast("Please fill in all fields");
            return false;
        }

        if (!isLimitWaitlistEnabled && TextUtils.isEmpty(capacity)) {
            showToast("Please enter event capacity");
            return false;
        }

        return true;
    }

    private Map<String, Object> createEventData(String eventId) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", eventId);
        eventData.put("name", eventTitleEditText.getText().toString().trim());
        eventData.put("description", eventDescriptionEditText.getText().toString().trim());
        eventData.put("location", eventLocationEditText.getText().toString().trim());
        eventData.put("regClosed", selectedDate);
        eventData.put("geolocationEnabled", geolocationSwitch.isChecked());
        eventData.put("limitWaitlistEnabled", limitWaitlistSwitch.isChecked());
        eventData.put("createdAt", System.currentTimeMillis());

        String capacity = capacityEditText.getText().toString().trim();
        if (!limitWaitlistSwitch.isChecked() && !TextUtils.isEmpty(capacity)) {
            eventData.put("capacity", Integer.parseInt(capacity));
        }

        eventData.put("waitList", waitList);
        eventData.put("selectedList", selectedList);
        eventData.put("cancelledList", cancelledList);
        eventData.put("organizerId", FirebaseUtils.getInstance(this).getDeviceId(this));

        return eventData;
    }

    private void uploadQrCodeToStorage(String eventId, Bitmap qrCodeBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("qr_codes")
                .child(eventId + ".jpg");

        storageRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update Firestore with QR code URL
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("qrCodeUrl", uri.toString());

                            db.collection("events")
                                    .document(eventId)
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> Log.d("QR Code", "QR code URL saved to Firestore."))
                                    .addOnFailureListener(e -> Log.e("QR Code", "Failed to save QR code URL.", e));
                        }))
                .addOnFailureListener(e -> Log.e("Upload Error", "Failed to upload QR code: " + e.getMessage()));
    }

    private String computeHash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data);
            StringBuilder hashString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hashString.append('0');
                hashString.append(hex);
            }
            return hashString.toString();
        } catch (Exception e) {
            Log.e("Hash Error", "Error computing hash: " + e.getMessage());
            return null;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (isQrCodeDisplayed) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
