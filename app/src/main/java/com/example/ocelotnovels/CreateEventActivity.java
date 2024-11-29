package com.example.ocelotnovels;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.example.ocelotnovels.utils.QRCodeUtils;
import com.example.ocelotnovels.view.Organizer.OrganizerMainActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {

    private static final int PICK_POSTER_REQUEST = 1;

    private EditText eventTitleEditText;
    private EditText eventDescriptionEditText;
    private EditText eventLocationEditText;
    private EditText capacityEditText;
    private TextView capacityTextView;
    private Button eventDateButton; // New button for Event Date
    private Button dueDateButton;
    private Button registrationOpenButton;
    private Switch geolocationSwitch;
    private Switch limitWaitlistSwitch;
    private Button createButton;
    private Button cancelButton;
    private Button uploadPosterButton;
    private Button removePosterButton;
    private ImageView posterImageView;

    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String currentDeviceId;
    private String facilityId;

    private String selectedEventDate = ""; // New field for Event Date
    private String selectedDueDate = "";
    private String selectedRegistrationOpenDate = "";
    private String eventPosterUrl = null;

    private List<String> selectedList;
    private List<String> cancelledList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_create_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firestore and Storage
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        currentDeviceId = FirebaseUtils.getInstance(this).getDeviceId(this);
        facilityId = getIntent().getStringExtra("facilityId");
        // Initialize views and lists
        initializeViews();

        // Set up listeners
        setupListeners();
        Log.d("FACILITYID", facilityId);
    }

    private void initializeViews() {
        eventTitleEditText = findViewById(R.id.event_title);
        eventDescriptionEditText = findViewById(R.id.event_description);
        eventLocationEditText = findViewById(R.id.event_location);
        capacityEditText = findViewById(R.id.event_capacity);
        capacityTextView = findViewById(R.id.capacity_text);
        eventDateButton = findViewById(R.id.event_happening_date); // New button
        dueDateButton = findViewById(R.id.event_due_date);
        registrationOpenButton = findViewById(R.id.event_registration_open);
        geolocationSwitch = findViewById(R.id.geolocation_switch);
        limitWaitlistSwitch = findViewById(R.id.limit_waitlist_switch);
        createButton = findViewById(R.id.create_button);
        cancelButton = findViewById(R.id.cancel_button);
        uploadPosterButton = findViewById(R.id.upload_poster_button);
        removePosterButton = findViewById(R.id.remove_poster_button);
        posterImageView = findViewById(R.id.event_poster_image);
    }

    private void setupListeners() {
        eventDateButton.setOnClickListener(v -> showDatePickerDialog(eventDateButton, "eventDate"));
        dueDateButton.setOnClickListener(v -> showDatePickerDialog(dueDateButton, "dueDate"));
        registrationOpenButton.setOnClickListener(v -> showDatePickerDialog(registrationOpenButton, "registrationOpen"));

        uploadPosterButton.setOnClickListener(v -> selectPoster());
        removePosterButton.setOnClickListener(v -> removePoster());

        createButton.setOnClickListener(v -> saveEventData());
        cancelButton.setOnClickListener(v -> finish());

        limitWaitlistSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            capacityTextView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            capacityEditText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                capacityEditText.setText(""); // Clear capacity input when hidden
            }
        });
    }

    private void showDatePickerDialog(Button button, String dateType) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    button.setText(selectedDate);

                    switch (dateType) {
                        case "eventDate":
                            selectedEventDate = selectedDate;
                            break;
                        case "dueDate":
                            selectedDueDate = selectedDate;
                            break;
                        case "registrationOpen":
                            selectedRegistrationOpenDate = selectedDate;
                            break;
                    }
                }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void selectPoster() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_POSTER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_POSTER_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                uploadPosterToStorage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadPosterToStorage(Bitmap bitmap) {
        String posterId = UUID.randomUUID().toString(); // Unique ID for the poster
        StorageReference posterRef = storageRef.child("images/events/" + posterId + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        posterRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> posterRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            eventPosterUrl = uri.toString();
                            Glide.with(this)
                                    .load(eventPosterUrl)
                                    .placeholder(R.drawable.ic_image_placeholder)
                                    .error(R.drawable.ic_image_placeholder)
                                    .into(posterImageView);
                            Toast.makeText(this, "Poster uploaded successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to get poster URL.", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to upload poster.", Toast.LENGTH_SHORT).show();
                });
    }

    private void removePoster() {
        posterImageView.setImageResource(R.drawable.ic_image_placeholder); // Reset to placeholder
        eventPosterUrl = null; // Clear the poster URL
        Toast.makeText(this, "Poster removed!", Toast.LENGTH_SHORT).show();
    }


    private Map<String, Object> createEventData(String eventId) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", eventId);
        eventData.put("name", eventTitleEditText.getText().toString().trim());
        eventData.put("description", eventDescriptionEditText.getText().toString().trim());
        eventData.put("location", eventLocationEditText.getText().toString().trim());
        eventData.put("eventDate", selectedEventDate);
        eventData.put("registrationOpen", selectedRegistrationOpenDate);
        eventData.put("regClosed", selectedDueDate);
        eventData.put("geolocationEnabled", geolocationSwitch.isChecked());
        eventData.put("limitWaitlistEnabled", limitWaitlistSwitch.isChecked());
        eventData.put("posterUrl", eventPosterUrl);
        eventData.put("createdAt", System.currentTimeMillis());
        eventData.put("organizerDeviceId", facilityId);

        String capacity = capacityEditText.getText().toString().trim();
        if (limitWaitlistSwitch.isChecked() && !TextUtils.isEmpty(capacity)) {
            eventData.put("capacity", Integer.parseInt(capacity));
        }

        return eventData;
    }

    private void addEventToFacility(String facilityId, String eventId) {
        DocumentReference facilityRef = db.collection("facilities").document(facilityId);

        facilityRef.update("events", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> Log.d("UpdateFacility", "Event added to facility successfully"))
                .addOnFailureListener(e -> Log.e("UpdateFacility", "Failed to add event to facility", e));
    }



    private void saveEventData() {
        if (!validateInputs()) {
            return;
        }

        String eventId = UUID.randomUUID().toString(); // Generate a unique event ID
        Map<String, Object> eventData = createEventData(eventId); // Prepare event data

        try {
            // Generate the QR Code
            Bitmap qrCodeBitmap = QRCodeUtils.generateQrCode(eventId, 500, 500);

            // Compute a hash for the QR Code (optional but useful for verification)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] qrCodeBytes = baos.toByteArray();
            String qrHash = computeHash(qrCodeBytes); // You need a helper function to compute the hash

            // Add the QR Code hash to the event data
            eventData.put("qrHash", qrHash);

            // Save the event data to Firestore
            db.collection("events").document(eventId)
                    .set(eventData)
                    .addOnSuccessListener(aVoid -> {
                        addEventToFacility(facilityId, eventId);
                        // Upload the QR Code image to Firebase Storage
                        uploadQrCodeToStorage(eventId, qrCodeBitmap);

                        // Navigate to QR Code Activity
                        navigateToQrCodeActivity(eventId, qrCodeBitmap, qrHash);
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        showToast("Failed to save event to Firestore.");
                    });
        } catch (WriterException e) {
            e.printStackTrace();
            showToast("Failed to generate QR Code.");
        }
    }

    private void uploadQrCodeToStorage(String eventId, Bitmap qrCodeBitmap) {
        // Reference for storing the QR Code image
        StorageReference qrCodeRef = storageRef.child("qrCodes/" + eventId + ".png");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        qrCodeRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> showToast("QR Code uploaded successfully."))
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showToast("Failed to upload QR Code.");
                });
    }

    private String computeHash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data);
            StringBuilder hashBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                hashBuilder.append(String.format("%02x", b));
            }
            return hashBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void navigateToQrCodeActivity(String eventId, Bitmap qrCodeBitmap, String qrHash) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();

        Intent intent = new Intent(CreateEventActivity.this, QrCodeActivity.class);
        intent.putExtra("eventTitle", eventTitleEditText.getText().toString().trim());
        intent.putExtra("eventDescription", eventDescriptionEditText.getText().toString().trim());
        intent.putExtra("eventDeadline", selectedDueDate);
        intent.putExtra("eventLocation", eventLocationEditText.getText().toString().trim());
        intent.putExtra("qrCode", byteArray);
        startActivity(intent);
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(eventTitleEditText.getText().toString().trim())
                || TextUtils.isEmpty(eventDescriptionEditText.getText().toString().trim())
                || TextUtils.isEmpty(eventLocationEditText.getText().toString().trim())
                || TextUtils.isEmpty(selectedEventDate)) {
            showToast("Please fill in all required fields.");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}