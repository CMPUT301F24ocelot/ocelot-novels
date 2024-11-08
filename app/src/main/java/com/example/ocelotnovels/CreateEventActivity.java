package com.example.ocelotnovels;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {

    private EditText eventTitleEditText;
    private EditText eventDescriptionEditText;
    private EditText eventLocationEditText;
    private EditText dueDateEditText;
    private Switch geolocationSwitch;
    private Switch limitWaitlistSwitch;
    private Button createButton;
    private Button cancelButton;
    private FirebaseFirestore db;

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

        // Generates a unique id for each event
        String eventId = UUID.randomUUID().toString();

        // Create a map to store the event data
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", eventId);
        eventData.put("title", eventTitle);
        eventData.put("description", eventDescription);
        eventData.put("location", eventLocation);
        eventData.put("date", dueDate);
        eventData.put("geolocationEnabled", isGeolocationEnabled);
        eventData.put("limitWaitlistEnabled", isLimitWaitlistEnabled);



        // Save event to Firestore
        db.collection("events").document(eventId)
                .set(eventData)
                .addOnSuccessListener(aVoid -> showToast("Event created successfully."))
                .addOnFailureListener(e -> showToast("Failed to create event."));
    }

    private void showToast(String message) {
        Toast.makeText(CreateEventActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
