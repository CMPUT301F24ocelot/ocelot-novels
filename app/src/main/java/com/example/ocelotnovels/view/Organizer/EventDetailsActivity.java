package com.example.ocelotnovels.view.Organizer;
<<<<<<< HEAD
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
=======

import android.os.Bundle;
import android.util.Log;
>>>>>>> origin/main
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

<<<<<<< HEAD
import com.example.ocelotnovels.CancelledEntrantsActivity;
import com.example.ocelotnovels.ConfirmedEntrantsActivity;
import com.example.ocelotnovels.InvitedEntrantsActivity;
import com.example.ocelotnovels.MainActivity;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.SelectedEntrantsActivity;
import com.example.ocelotnovels.view.Entrant.ProfileActivity;
import com.example.ocelotnovels.view.Entrant.WaitingListActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
public class EventDetailsActivity extends AppCompatActivity {
    private static final String TAG = "EventDetailsActivity";
    private FirebaseFirestore db;
=======
import com.example.ocelotnovels.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";

    private FirebaseFirestore db;

>>>>>>> origin/main
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);
<<<<<<< HEAD
        // Get the event name passed from the adapter
        String eventName = getIntent().getStringExtra("eventName");
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
=======

        // Get the event name passed from the adapter
        String eventName = getIntent().getStringExtra("eventName");

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

>>>>>>> origin/main
        // Fetch event details and display them
        fetchEventDetails(eventName);
    }

<<<<<<< HEAD


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entrant_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        // Handle menu item clicks
        if (id == R.id.menu_waiting_list) {
            // Navigate to Profile Activity
            Intent waitingListActivity = new Intent(EventDetailsActivity.this, WaitingListActivity.class);
            startActivity(waitingListActivity);
        }

        if (id == R.id.menu_selected_list) {
            // Navigate to Profile Activity
            Intent selectedEntrantsActivity = new Intent(EventDetailsActivity.this, SelectedEntrantsActivity.class);
            startActivity(selectedEntrantsActivity);
        }

        if (id == R.id.menu_invited_list) {
            // Navigate to Profile Activity
            Intent invitedEntrantsActivity = new Intent(EventDetailsActivity.this, InvitedEntrantsActivity.class);
            startActivity(invitedEntrantsActivity);
        }

        if (id == R.id.menu_cancelled_list) {
            // Navigate to Profile Activity
            Intent cancelledEntrantsActivity = new Intent(EventDetailsActivity.this, CancelledEntrantsActivity.class);
            startActivity(cancelledEntrantsActivity);
        }

        if (id == R.id.menu_confirmed_list) {
            // Navigate to Profile Activity
            Intent confirmedEntrantsActivity = new Intent(EventDetailsActivity.this, ConfirmedEntrantsActivity.class);
            startActivity(confirmedEntrantsActivity);
        }

        return super.onOptionsItemSelected(item);
    }
=======
>>>>>>> origin/main
    private void fetchEventDetails(String eventName) {
        // Query Firestore for the event details
        db.collection("events")
                .whereEqualTo("name", eventName) // Replace "name" with your Firestore field for event names
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Retrieve event details
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String eventDate = document.getString("date");
                            String eventLocation = document.getString("location");
                            String eventDescription = document.getString("description");
<<<<<<< HEAD
=======

>>>>>>> origin/main
                            // Set the details in TextViews
                            TextView nameTextView = findViewById(R.id.event_name_textview);
                            TextView dateTextView = findViewById(R.id.event_date_textview);
                            TextView locationTextView = findViewById(R.id.event_location_textview);
                            TextView descriptionTextView = findViewById(R.id.event_description_textview);
<<<<<<< HEAD
=======

>>>>>>> origin/main
                            nameTextView.setText(eventName);
                            dateTextView.setText(eventDate != null ? eventDate : "No date available");
                            locationTextView.setText(eventLocation != null ? eventLocation : "No location available");
                            descriptionTextView.setText(eventDescription != null ? eventDescription : "No description available");
                        }
                    } else {
                        Log.w(TAG, "No matching documents found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching event details", e);
                });
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> origin/main
