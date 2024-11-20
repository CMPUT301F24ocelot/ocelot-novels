package com.example.ocelotnovels;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ocelotnovels.utils.QRCodeUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {

    private EditText eventTitleEditText;
    private EditText eventDescriptionEditText;
    private EditText eventLocationEditText;
    private Button dueDateButton;
    private Switch geolocationSwitch;
    private Switch limitWaitlistSwitch;
    private Button createButton;
    private Button cancelButton;
    private FirebaseFirestore db;
    private ImageView qrCodeImageView;
    private String selectedDate = "";


    /**
     * Initializes the activity, setting up the layout, views, and Firestore instance.
     * Sets up listeners for the Create and Cancel buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState().
     */
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
        dueDateButton = findViewById(R.id.event_due_date);
        geolocationSwitch = findViewById(R.id.geolocation_switch);
        limitWaitlistSwitch = findViewById(R.id.limit_waitlist_switch);
        createButton = findViewById(R.id.create_button);
        cancelButton = findViewById(R.id.cancel_button);
        qrCodeImageView = findViewById(R.id.qr_code_image);

        // Set up date picker dialog
        dueDateButton.setOnClickListener(v -> showDatePickerDialog());

        // Set listeners for the buttons
        createButton.setOnClickListener(v -> saveEventData());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void showDatePickerDialog() {
        // Get Current Date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Update the button text with selected date
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    dueDateButton.setText(selectedDate);
                }, year, month, day);

        // Set minimum date to current date
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void saveEventData() {
        String eventTitle = eventTitleEditText.getText().toString().trim();
        String eventDescription = eventDescriptionEditText.getText().toString().trim();
        String eventLocation = eventLocationEditText.getText().toString().trim();
        boolean isGeolocationEnabled = geolocationSwitch.isChecked();
        boolean isLimitWaitlistEnabled = limitWaitlistSwitch.isChecked();

        // Validate input
        if (TextUtils.isEmpty(eventTitle) || TextUtils.isEmpty(eventDescription) ||
                TextUtils.isEmpty(eventLocation) || TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generates a unique id for each event
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

        // Save event to Firestore
        db.collection("events").document(eventId)
                .set(eventData)
                .addOnSuccessListener(aVoid -> {
                    showToast("Event created successfully.");
                    generateAndUploadQrCode(eventId);
                })
                .addOnFailureListener(e -> showToast("Failed to create event."));
    }

    private void showToast(String message) {
        Toast.makeText(CreateEventActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void generateAndUploadQrCode(String eventId) {
        try {
            // Generate QR code and display it as Bitmap
            Bitmap qrCodeBitmap = QRCodeUtils.generateQrCode(eventId, 500, 500);
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
            qrCodeImageView.setVisibility(View.VISIBLE);

            // Generate QR code and save it locally
            String qrCodePath = QRCodeUtils.qrCodeGenerator(eventId);
            File qrCodeFile = new File(qrCodePath);

            // Define the Firebase Storage path
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("qr_codes/" + eventId + ".jpg");

            // Upload the QR code image to Firebase Storage
            storageRef.putFile(android.net.Uri.fromFile(qrCodeFile))
                    .addOnSuccessListener(taskSnapshot -> {
                        // Retrieve the download URL
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();

                            // Display the download URL in the log
                            Log.d("QR Code URL", "Download URL: " + downloadUrl);

                            // Display a toast with the download URL
                            Toast.makeText(CreateEventActivity.this, "QR Code URL: " + downloadUrl, Toast.LENGTH_LONG).show();

                            // Update Firestore with the QR Code URI
                            storeQrCodeUriInFirestore(eventId, downloadUrl);
                        });
                    })
                    .addOnFailureListener(e -> Log.e("Upload Error", "Failed to upload QR code"));
        } catch (WriterException | IOException e) {
            Log.e("QR Code Error", "Failed to generate QR code: " + e.getMessage());
        }
    }


    private void storeQrCodeUriInFirestore(String eventId, String qrCodeUri) {
        db.collection("events").document(eventId)
                .update("qrCodeUrl", qrCodeUri)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "QR Code URI successfully updated"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating QR Code URI", e));
    }
}
