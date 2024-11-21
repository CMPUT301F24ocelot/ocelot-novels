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

import com.example.ocelotnovels.utils.QRCodeUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {

    private EditText eventTitleEditText;
    private EditText eventDescriptionEditText;
    private EditText eventLocationEditText;
    private EditText capacityEditText;  // New EditText for capacity
    private Button dueDateButton;
    private Switch geolocationSwitch;
    private Switch limitWaitlistSwitch;
    private Button createButton;
    private Button cancelButton;
    private FirebaseFirestore db;
    private String selectedDate = "";

    // New variables for QR code display
    private ImageView qrCodeImageView;
    private TextView eventTitleText;
    private TextView eventDescriptionText;
    private TextView eventDeadlineText;
    private TextView eventStatusText;
    private Button backButton;
    private boolean isQrCodeDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews();

        // Set up date picker dialog
        dueDateButton.setOnClickListener(v -> showDatePickerDialog());

        // Set listeners for the buttons
        createButton.setOnClickListener(v -> saveEventData());
        cancelButton.setOnClickListener(v -> finish());

        // Set listener for limitWaitlistSwitch
        limitWaitlistSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            capacityEditText.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            // Clear capacity when switch is turned on
            if (isChecked) {
                capacityEditText.setText("");
            }
        });
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
    }

    private void initializeQrCodeViews() {
        setContentView(R.layout.organizer_qr_code);

        qrCodeImageView = findViewById(R.id.event_qr_code_image);
        eventTitleText = findViewById(R.id.event_title);
        eventDescriptionText = findViewById(R.id.event_description);
        eventDeadlineText = findViewById(R.id.event_deadline);
        eventStatusText = findViewById(R.id.event_status);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    dueDateButton.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void saveEventData() {
        String eventTitle = eventTitleEditText.getText().toString().trim();
        String eventDescription = eventDescriptionEditText.getText().toString().trim();
        String eventLocation = eventLocationEditText.getText().toString().trim();
        String capacity = capacityEditText.getText().toString().trim();
        boolean isGeolocationEnabled = geolocationSwitch.isChecked();
        boolean isLimitWaitlistEnabled = limitWaitlistSwitch.isChecked();

        // Validate input
        if (TextUtils.isEmpty(eventTitle) || TextUtils.isEmpty(eventDescription) ||
                TextUtils.isEmpty(eventLocation) || TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate capacity if waitlist is not limited
        if (!isLimitWaitlistEnabled && TextUtils.isEmpty(capacity)) {
            Toast.makeText(this, "Please enter event capacity", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique event ID
        String eventId = UUID.randomUUID().toString();

        // Create a map to store the event data
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", eventId);
        eventData.put("name", eventTitle);
        eventData.put("description", eventDescription);
        eventData.put("location", eventLocation);
        eventData.put("regClosed", selectedDate);
        eventData.put("geolocationEnabled", isGeolocationEnabled);
        eventData.put("limitWaitlistEnabled", isLimitWaitlistEnabled);
        eventData.put("createdAt", System.currentTimeMillis());

        // Only add capacity if waitlist is not limited
        if (!isLimitWaitlistEnabled && !TextUtils.isEmpty(capacity)) {
            eventData.put("capacity", Integer.parseInt(capacity));
        }

        // Save event to Firestore
        db.collection("events").document(eventId)
                .set(eventData)
                .addOnSuccessListener(aVoid -> generateAndDisplayQrCode(eventId, eventTitle, eventDescription, selectedDate))
                .addOnFailureListener(e -> showToast("Failed to create event."));
    }

    private void generateAndDisplayQrCode(String eventId, String eventTitle,
                                          String eventDescription, String eventDeadline) {
        try {
            // Switch to QR code layout
            initializeQrCodeViews();
            isQrCodeDisplayed = true;

            // Generate QR code using just the eventId
            Bitmap qrCodeBitmap = QRCodeUtils.generateQrCode(eventId, 500, 500);
            qrCodeImageView.setImageBitmap(qrCodeBitmap);

            // Update text views
            eventTitleText.setText("Event Title: " + eventTitle);
            eventDescriptionText.setText("Event Description: " + eventDescription);
            eventDeadlineText.setText("Event Deadline: " + eventDeadline);
            eventStatusText.setText("Event Status: Active");

            // Upload QR code to Firebase Storage
            uploadQrCodeToStorage(eventId, qrCodeBitmap);

        } catch (WriterException e) {
            Log.e("QR Code Error", "Failed to generate QR code: " + e.getMessage());
            showToast("Failed to generate QR code.");
        }
    }

    private void uploadQrCodeToStorage(String eventId, Bitmap qrCodeBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("qr_codes")
                .child(eventId + ".jpg");

        storageRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL and store it in Firestore
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("qrCodeUrl", uri.toString());

                        db.collection("events")
                                .document(eventId)
                                .update(updates)
                                .addOnSuccessListener(aVoid ->
                                        Log.d("QR Code", "QR code URL saved to Firestore"))
                                .addOnFailureListener(e ->
                                        Log.e("QR Code", "Failed to save QR code URL", e));
                    });
                })
                .addOnFailureListener(e ->
                        Log.e("Upload Error", "Failed to upload QR code: " + e.getMessage()));
    }


    private void showToast(String message) {
        Toast.makeText(CreateEventActivity.this, message, Toast.LENGTH_SHORT).show();
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