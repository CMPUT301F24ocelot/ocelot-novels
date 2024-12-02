/**
 * OrganizerMainActivity is the main screen for organizers to view, manage, and create events.
 * This activity interacts with Firestore to load event data and provides navigation to other
 * activities like MapsActivity and CreateEventActivity.
 */
package com.example.ocelotnovels.view.Organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ocelotnovels.MainActivity;
import com.example.ocelotnovels.CancelledEntrantsActivity;
import com.example.ocelotnovels.ConfirmedEntrantsActivity;
import com.example.ocelotnovels.InvitedEntrantsActivity;
import com.example.ocelotnovels.MapsActivity;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.SelectedEntrantsActivity;
import com.example.ocelotnovels.WaitingListActivity;
import com.example.ocelotnovels.model.Event;
import com.example.ocelotnovels.CreateEventActivity;
import com.example.ocelotnovels.FacilityProfileActivity;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.example.ocelotnovels.view.Organizer.OrganizerEventAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizerMainActivity extends AppCompatActivity {

    public RecyclerView organizerRecyclerView;
    public OrganizerEventAdapter eventAdapter;
    private List<Map<String, String>> eventDetails;
    public FirebaseFirestore db;
    public String facilityId;

    /**
     * Initializes the OrganizerMainActivity and sets up the RecyclerView,
     * buttons, and Firebase Firestore connection. Also loads events specific to the organizer.
     *
     * @param savedInstanceState Saved state data from previous activity lifecycle (if any).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_mainevents);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Setup RecyclerView
        organizerRecyclerView = findViewById(R.id.OrganizerRecyclerView);
        organizerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventDetails = new ArrayList<>();
        eventAdapter = new OrganizerEventAdapter(eventDetails, this);
        organizerRecyclerView.setAdapter(eventAdapter);

        // Load events from Firestore
        loadEventsFromFirestore();

        facilityId = FirebaseUtils.getInstance(this).getFacilityId(this);

        // Add Event Button Click
        Button addEventButton = findViewById(R.id.add_events_button);
        addEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerMainActivity.this, CreateEventActivity.class);
            intent.putExtra("facilityId", facilityId);
            startActivity(intent);
        });

        // Button to navigate to Entrant Map
        Button entrantMapButton = findViewById(R.id.entrant_map);
        entrantMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerMainActivity.this, MapsActivity.class);
            startActivity(intent);
        });


        /*// Facility Profile Button Click
        Button facilityProfileButton = findViewById(R.id.facility_profile_button);
        facilityProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerMainActivity.this, FacilityProfileActivity.class);
            startActivity(intent);
        });*/

        // Entrant List Button Click
        /*Button entrantListButton = findViewById(R.id.entrant_list);
        entrantListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEntrantListDropdown(v);
            }
        });*/

        /*// Back Button Click
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerMainActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Optional: Call finish to close OrganizerMainActivity
        });*/
    }

    /**
     * Inflates the organizer's menu options.
     *
     * @param menu The menu to be inflated.
     * @return true if the menu is successfully created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.organizer_drawer_menu, menu);
        return true;
    }

    /**
     * Handles the selection of menu items.
     *
     * @param item The selected menu item.
     * @return true if the menu item is handled successfully.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        // Handle menu item clicks
        if (id == R.id.menu_facility_profile) {
            // Navigate to Profile Activity
            Intent facilityProfileActivity = new Intent(OrganizerMainActivity.this, com.example.ocelotnovels.FacilityProfileActivity.class);
            startActivity(facilityProfileActivity);
        }

        if (id == R.id.menu_entrant_map) {
            // Navigate to Profile Activity
            Intent mapsActivity = new Intent(OrganizerMainActivity.this, MapsActivity.class);
            startActivity(mapsActivity);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Fetches events from Firestore specific to the organizer's facility
     * and updates the RecyclerView with the event details.
     */
    public void loadEventsFromFirestore() {
        // Use the facilityId to filter events specific to this facility
        db.collection("events")
                .whereEqualTo("organizerDeviceId", facilityId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventDetails.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String eventName = document.getString("name");
                        String eventDate = document.getString("eventDate");
                        String eventLocation = document.getString("location");

                        // Log the event date
                        Log.d("EVENTDATE", eventDate != null ? eventDate : "null");

                        // Handle null values for eventDate
                        if (eventName != null && eventLocation != null) {
                            Map<String, String> event = new HashMap<>();
                            event.put("name", eventName);
                            event.put("date", eventDate != null ? eventDate : "No date available");
                            event.put("location", eventLocation);
                            eventDetails.add(event);
                        }
                    }
                    eventAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Reloads event data from Firestore whenever the activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadEventsFromFirestore(); // Implement this method to fetch events from Firestore
    }

}
