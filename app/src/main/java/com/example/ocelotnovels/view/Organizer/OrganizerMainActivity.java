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

        // Add Event Button Click
        Button addEventButton = findViewById(R.id.add_events_button);
        addEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerMainActivity.this, CreateEventActivity.class);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.organizer_drawer_menu, menu);
        return true;
    }

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

    public void loadEventsFromFirestore() {
        db.collection("events")
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


    private void showEntrantListDropdown(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);

        // Dynamically add menu items
        popupMenu.getMenu().add("Waiting List");
        popupMenu.getMenu().add("Selected Entrants");
        popupMenu.getMenu().add("Invited Entrants");
        popupMenu.getMenu().add("Cancelled Entrants");
        popupMenu.getMenu().add("Confirmed Entrants");

        // Handle click events for each item in the dropdown menu
        popupMenu.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            switch (title) {
                case "Waiting List":
                    startActivity(new Intent(OrganizerMainActivity.this, WaitingListActivity.class));
                    break;
                case "Selected Entrants":
                    startActivity(new Intent(OrganizerMainActivity.this, SelectedEntrantsActivity.class));
                    break;
                case "Invited Entrants":
                    startActivity(new Intent(OrganizerMainActivity.this, InvitedEntrantsActivity.class));
                    break;
                case "Cancelled Entrants":
                    startActivity(new Intent(OrganizerMainActivity.this, CancelledEntrantsActivity.class));
                    break;
                case "Confirmed Entrants":
                    startActivity(new Intent(OrganizerMainActivity.this, ConfirmedEntrantsActivity.class));
                    break;
                default:
                    return false;
            }
            return true;
        });

        // Show the dropdown menu
        popupMenu.show();
    }
}
