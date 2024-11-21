package com.example.ocelotnovels;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ocelotnovels.utils.FirebaseUtils;
import com.example.ocelotnovels.utils.QRCodeUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.IOException;
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
    private Button dueDateButton;
    private Switch geolocationSwitch;
    private Switch limitWaitlistSwitch;
    private Button createButton;
    private Button cancelButton;
    private FirebaseFirestore db;
    private ImageView qrCodeImageView;
    private String selectedDate = "";
    private List<String> waitList;
    private List<String> selectedList;
    private List<String> cancelledList;

    /**
     * Initializes the activity, setting up the layout, views, and Firestore instance.
     * Configures listeners for the Create and Cancel buttons, and initializes lists for event data.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState().
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        // Initialize Firestore and UI components
        db = FirebaseFirestore.getInstance();
        eventTitleEditText = findViewById(R.id.event_title);
        eventDescriptionEditText = findViewById(R.id.event_description);
        eventLocationEditText = findViewById(R.id.event_location);
        dueDateButton = findViewById(R.id.event_due_date);
        geolocationSwitch = findViewById(R.id.geolocation_switch);
        limitWaitlistSwitch = findViewById(R.id.limit_waitlist_switch);
        createButton = findViewById(R.id.create_button);
        cancelButton = findViewById(R.id.cancel_button);
        qrCodeImageView = findViewById(R.id.qr_code_image);
        waitList = new ArrayList<>();
        selectedList = new ArrayList<>();
        cancelledList = new ArrayList<>();

        // Set up date picker dialog
        dueDateButton.setOnClickListener(v -> showDatePickerDialog());

        // Set listeners for the Create and Cancel buttons
        createButton.setOnClickListener(v -> saveEventData());
        cancelButton.setOnClickListener(v -> finish());
    }

    /**
     * Displays a date picker dialog for selecting the event's registration closing date.
     * Ensures that the selected date is not earlier than the current date.
     */
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Update the button text with the selected date
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    dueDateButton.setText(selectedDate);
                }, year, month, day);

        // Set the minimum date to the current date
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    /**
     * Validates the input fields and saves the event data to Firestore.
     * Also generates a QR code for the event and uploads it to Firebase Storage.
     */
    private void saveEventData() {
        String eventTitle = eventTitleEditText.getText().toString().trim();
        String eventDescription = eventDescriptionEditText.getText().toString().trim();
        String eventLocation = eventLocationEditText.getText().toString().trim();
        boolean isGeolocationEnabled = geolocationSwitch.isChecked();
        boolean isLimitWaitlistEnabled = limitWaitlistSwitch.isChecked();

        // Validate required fields
        if (TextUtils.isEmpty(eventTitle) || TextUtils.isEmpty(eventDescription) ||
                TextUtils.isEmpty(eventLocation) || TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique ID for the event
        String eventId = UUID.randomUUID().toString();

        // Prepare event data for Firestore
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", eventId);
        eventData.put("name", eventTitle);
        eventData.put("description", eventDescription);
        eventData.put("location", eventLocation);
        eventData.put("regClosed", selectedDate);
        eventData.put("geolocationEnabled", isGeolocationEnabled);
        eventData.put("limitWaitlistEnabled", isLimitWaitlistEnabled);
        eventData.put("waitList", waitList);
        eventData.put("selectedList", selectedList);
        eventData.put("cancelledList", cancelledList);
        eventData.put("organizerId", FirebaseUtils.getInstance(this).getDeviceId(this));

        // Save the event to Firestore
        db.collection("events").document(eventId)
                .set(eventData)
                .addOnSuccessListener(aVoid -> {
                    showToast("Event created successfully.");
                    generateAndUploadQrCode(eventId);
                })
                .addOnFailureListener(e -> showToast("Failed to create event."));
    }

    /**
     * Displays a toast message.
     *
     * @param message The message to be displayed.
     */
    private void showToast(String message) {
        Toast.makeText(CreateEventActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Generates a QR code for the event and uploads it to Firebase Storage.
     *
     * @param eventId The unique identifier of the event.
     */
    private void generateAndUploadQrCode(String eventId) {
        try {
            Bitmap qrCodeBitmap = QRCodeUtils.generateQrCode(eventId, 500, 500);
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
            qrCodeImageView.setVisibility(View.VISIBLE);

            String qrCodePath = QRCodeUtils.qrCodeGenerator(eventId);
            File qrCodeFile = new File(qrCodePath);

            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference().child("qr_codes/" + eventId + ".jpg");

            storageRef.putFile(android.net.Uri.fromFile(qrCodeFile))
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                Log.d("QR Code URL", "Download URL: " + downloadUrl);
                                storeQrCodeUriInFirestore(eventId, downloadUrl);
                            }))
                    .addOnFailureListener(e -> Log.e("Upload Error", "Failed to upload QR code"));
        } catch (WriterException | IOException e) {
            Log.e("QR Code Error", "Failed to generate QR code: " + e.getMessage());
        }
    }

    /**
     * Updates the Firestore document with the QR code URL.
     *
     * @param eventId The unique identifier of the event.
     * @param qrCodeUri The URL of the uploaded QR code.
     */
    private void storeQrCodeUriInFirestore(String eventId, String qrCodeUri) {
        db.collection("events").document(eventId)
                .update("qrCodeUrl", qrCodeUri)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "QR Code URI successfully updated"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating QR Code URI", e));
    }
}
