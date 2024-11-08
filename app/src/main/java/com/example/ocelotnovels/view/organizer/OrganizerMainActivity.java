package com.example.ocelotnovels.view.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.MapsActivity;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Event;
import com.example.ocelotnovels.utils.FirebaseUtils;
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

        // Set greeting text
        TextView welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText("Hello Gareth!");  // Replace with dynamic username if needed

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

        Button addEventButton = findViewById(R.id.add_events_button);
        entrantMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerMainActivity.this, CreateEventActivity.class);
            startActivity(intent);
        }); */

    }

    // Constructor for dependency injection (for testing)
    public OrganizerMainActivity(FirebaseFirestore firestore) {
        this.db = firestore;
        this.eventNames = new ArrayList<>();
    }

    // Default constructor
    public OrganizerMainActivity() {
        this(FirebaseFirestore.getInstance());  // Use the real Firestore by default
    }

    public void loadEventsFromFirestore() {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventNames.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String eventName = document.getString("eventName");
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
