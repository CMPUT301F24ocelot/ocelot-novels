package com.example.ocelotnovels.view.organizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
<<<<<<< HEAD
=======

import com.example.ocelotnovels.CancelledEntrantsActivity;
import com.example.ocelotnovels.ConfirmedEntrantsActivity;
import com.example.ocelotnovels.InvitedEntrantsActivity;
import com.example.ocelotnovels.MapsActivity;
>>>>>>> origin/main
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.SelectedEntrantsActivity;
import com.example.ocelotnovels.WaitingListActivity;
import com.example.ocelotnovels.model.Event;
import com.example.ocelotnovels.CreateEventActivity;
import com.example.ocelotnovels.FacilityProfileActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrganizerMainActivity extends AppCompatActivity {

    public RecyclerView organizerRecyclerView;
    public OrganizerEventAdapter eventAdapter;
    public List<String> eventNames;
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
        eventNames = new ArrayList<>();
        eventAdapter = new OrganizerEventAdapter(eventNames, this);
        organizerRecyclerView.setAdapter(eventAdapter);

        // Load events from Firestore
        loadEventsFromFirestore();

<<<<<<< HEAD
        // Add Event Button Click
=======
        // Set greeting text
        TextView welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText("Lists");  // Replace with dynamic username if needed

        // Button to navigate to Entrant Map
        Button entrantMapButton = findViewById(R.id.entrant_map);
        entrantMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerMainActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        /*
        Button waitingListButton = findViewById(R.id.entrant_list);
        entrantMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerMainActivity.this, MapOfEntrantsActivity.class);
            startActivity(intent);
        });
        */

        Button entrantListButton = findViewById(R.id.entrant_list);
        entrantListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEntrantOptionsDialog();
            }
        });

        /*
>>>>>>> origin/main
        Button addEventButton = findViewById(R.id.add_events_button);
        addEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerMainActivity.this, CreateEventActivity.class);
            startActivity(intent);
<<<<<<< HEAD
        });

        // Facility Profile Button Click
        Button facilityProfileButton = findViewById(R.id.facility_profile_button);
        facilityProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerMainActivity.this, FacilityProfileActivity.class);
            startActivity(intent);
        });
=======
        }); */
    }
    private void showEntrantOptionsDialog() {
        final CharSequence[] items = {"Waitlist of Entrants", "Selected Entrants", "Invited Entrants", "Cancelled Entrants", "Confirmed Entrants"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manage Entrants");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        startActivity(new Intent(OrganizerMainActivity.this, WaitingListActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(OrganizerMainActivity.this, SelectedEntrantsActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(OrganizerMainActivity.this, InvitedEntrantsActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(OrganizerMainActivity.this, CancelledEntrantsActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(OrganizerMainActivity.this, ConfirmedEntrantsActivity.class));
                        break;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Constructor for dependency injection (for testing)
    public OrganizerMainActivity(FirebaseFirestore firestore) {
        this.db = firestore;
        this.eventNames = new ArrayList<>();
    }

    // Default constructor
    public OrganizerMainActivity() {
        this(FirebaseFirestore.getInstance());  // Use the real Firestore by default
>>>>>>> origin/main
    }

    public void loadEventsFromFirestore() {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventNames.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String eventName = document.getString("title");
                        if (eventName != null) {
                            eventNames.add(eventName);
                        }
                    }
                    eventAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                });
    }
}