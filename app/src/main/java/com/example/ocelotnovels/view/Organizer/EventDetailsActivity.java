package com.example.ocelotnovels.view.Organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ocelotnovels.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";

    private FirebaseFirestore db;
    private String eventId; // To store the ID of the event being displayed

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        // Get the event name passed from the adapter
        String eventName = getIntent().getStringExtra("eventName");

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Fetch event details and display them
        fetchEventDetails(eventName);

        // Set up the Delete Event button
        Button deleteButton = findViewById(R.id.delete_event_button);
        deleteButton.setOnClickListener(v -> deleteEvent());
    }

    private void fetchEventDetails(String eventName) {
        // Query Firestore for the event details
        db.collection("events")
                .whereEqualTo("name", eventName) // Replace "name" with your Firestore field for event names
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Retrieve event details
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            eventId = document.getId(); // Store the event ID for deletion

                            String eventDate = document.getString("eventDate");
                            String eventLocation = document.getString("location");
                            String eventDescription = document.getString("description");
                            String posterUrl = document.getString("posterUrl"); // Field for poster URL

                            // Set the details in TextViews
                            TextView nameTextView = findViewById(R.id.event_name_textview);
                            TextView dateTextView = findViewById(R.id.event_date_textview);
                            TextView locationTextView = findViewById(R.id.event_location_textview);
                            TextView descriptionTextView = findViewById(R.id.event_description_textview);

                            nameTextView.setText(eventName);
                            dateTextView.setText(eventDate != null ? eventDate : "No date available");
                            locationTextView.setText(eventLocation != null ? eventLocation : "No location available");
                            descriptionTextView.setText(eventDescription != null ? eventDescription : "No description available");

                            // Load the event poster
                            ImageView posterImageView = findViewById(R.id.event_poster_imageview);
                            if (posterUrl != null && !posterUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(posterUrl)
                                        .placeholder(R.drawable.ic_image_placeholder)
                                        .error(R.drawable.ic_image_placeholder)
                                        .into(posterImageView);
                            } else {
                                posterImageView.setImageResource(R.drawable.ic_image_placeholder);
                            }
                        }
                    } else {
                        Log.w(TAG, "No matching documents found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching event details", e);
                });
    }

    private void deleteEvent() {
        if (eventId != null) {
            db.collection("events").document(eventId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Event deleted successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after deletion
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to delete event.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error deleting event", e);
                    });
        } else {
            Toast.makeText(this, "Event ID not found. Cannot delete.", Toast.LENGTH_SHORT).show();
        }
    }
}