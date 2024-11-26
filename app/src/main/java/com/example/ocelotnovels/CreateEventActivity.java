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

        // Initialize Firestore
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
        qrCodeImageView = findViewById(R.id.qr_code_image);

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

        db.collection("events").document(eventId)
                .set(eventData)
                .addOnSuccessListener(aVoid -> {
                    showToast("Event created successfully.");
                    generateAndDisplayQrCode(eventId,
                            eventTitleEditText.getText().toString().trim(),
                            eventDescriptionEditText.getText().toString().trim(),
                            selectedDate);
                })
                .addOnFailureListener(e -> showToast("Failed to create event."));
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

    private void generateAndDisplayQrCode(String eventId, String eventTitle,
                                          String eventDescription, String eventDeadline) {
        try {
            initializeQrCodeViews();
            isQrCodeDisplayed = true;

            Bitmap qrCodeBitmap = QRCodeUtils.generateQrCode(eventId, 500, 500);
            qrCodeImageView.setImageBitmap(qrCodeBitmap);

            eventTitleText.setText("Event Title: " + eventTitle);
            eventDescriptionText.setText("Event Description: " + eventDescription);
            eventDeadlineText.setText("Event Deadline: " + eventDeadline);
            eventStatusText.setText("Event Status: Active");

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
                .addOnSuccessListener(taskSnapshot ->
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
                        })
                )
                .addOnFailureListener(e ->
                        Log.e("Upload Error", "Failed to upload QR code: " + e.getMessage()));
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