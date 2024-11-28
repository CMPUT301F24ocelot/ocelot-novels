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

import com.example.ocelotnovels.utils.FirebaseUtils;
import com.example.ocelotnovels.utils.QRCodeUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
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
        capacityTextView = findViewById(R.id.capacity_text);
        dueDateButton = findViewById(R.id.event_due_date);
        registrationOpenButton = findViewById(R.id.event_registration_open);
        geolocationSwitch = findViewById(R.id.geolocation_switch);
        limitWaitlistSwitch = findViewById(R.id.limit_waitlist_switch);
        createButton = findViewById(R.id.create_button);
        cancelButton = findViewById(R.id.cancel_button);
        uploadPosterButton = findViewById(R.id.upload_poster_button);
        removePosterButton = findViewById(R.id.remove_poster_button);
        posterImageView = findViewById(R.id.organizer_event_image);

        selectedList = new ArrayList<>();
        cancelledList = new ArrayList<>();
    }

    private void setupListeners() {
        dueDateButton.setOnClickListener(v -> showDatePickerDialog(dueDateButton, true));
        registrationOpenButton.setOnClickListener(v -> showDatePickerDialog(registrationOpenButton, false));

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

    private void showDatePickerDialog(Button button, boolean isDueDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    button.setText(selectedDate);

                    if (isDueDate) {
                        selectedDueDate = selectedDate;
                    } else {
                        selectedRegistrationOpenDate = selectedDate;
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
                posterImageView.setImageBitmap(bitmap);
                uploadPosterToStorage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadPosterToStorage(Bitmap bitmap) {
        String posterId = UUID.randomUUID().toString();
        StorageReference posterRef = storageRef.child("event_posters/" + posterId + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        posterRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> posterRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            eventPosterUrl = uri.toString();
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
        eventPosterUrl = null;
        Toast.makeText(this, "Poster removed!", Toast.LENGTH_SHORT).show();
    }

    private void saveEventData() {
        if (!validateInputs()) {
            return;
        }

        String eventId = UUID.randomUUID().toString();
        Map<String, Object> eventData = createEventData(eventId);

        // Save event data to Firestore
        db.collection("events").document(eventId)
                .set(eventData)
                .addOnSuccessListener(aVoid -> showToast("Event created successfully."))
                .addOnFailureListener(e -> showToast("Failed to create event."));
    }

    private Map<String, Object> createEventData(String eventId) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", eventId);
        eventData.put("name", eventTitleEditText.getText().toString().trim());
        eventData.put("description", eventDescriptionEditText.getText().toString().trim());
        eventData.put("location", eventLocationEditText.getText().toString().trim());
        eventData.put("registrationOpen", selectedRegistrationOpenDate);
        eventData.put("regClosed", selectedDueDate);
        eventData.put("geolocationEnabled", geolocationSwitch.isChecked());
        eventData.put("limitWaitlistEnabled", limitWaitlistSwitch.isChecked());
        eventData.put("posterUrl", eventPosterUrl);
        eventData.put("createdAt", System.currentTimeMillis());

        String capacity = capacityEditText.getText().toString().trim();
        if (limitWaitlistSwitch.isChecked() && !TextUtils.isEmpty(capacity)) {
            eventData.put("capacity", Integer.parseInt(capacity));
        }

        eventData.put("selectedList", selectedList);
        eventData.put("cancelledList", cancelledList);
        eventData.put("organizerId", FirebaseUtils.getInstance(this).getDeviceId(this));

        return eventData;
    }

    private boolean validateInputs() {
        String eventTitle = eventTitleEditText.getText().toString().trim();
        String eventDescription = eventDescriptionEditText.getText().toString().trim();
        String eventLocation = eventLocationEditText.getText().toString().trim();

        if (TextUtils.isEmpty(eventTitle) || TextUtils.isEmpty(eventDescription) ||
                TextUtils.isEmpty(eventLocation) || TextUtils.isEmpty(selectedDueDate) ||
                TextUtils.isEmpty(selectedRegistrationOpenDate)) {
            showToast("Please fill in all fields");
            return false;
        }

        if (limitWaitlistSwitch.isChecked() && TextUtils.isEmpty(capacityEditText.getText().toString().trim())) {
            showToast("Please enter event capacity");
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
