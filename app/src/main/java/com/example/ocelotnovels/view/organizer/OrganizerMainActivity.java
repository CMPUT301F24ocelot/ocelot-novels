package com.example.ocelotnovels.view.organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Event;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrganizerMainActivity extends AppCompatActivity {

    private FirebaseUtils firebaseUtils;
    private RecyclerView recyclerView;
    private OrganizerEventAdapter eventAdapter;
    private List<Event> eventList;
    private TextView welcomeText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_mainevents);

        // Initialize FirebaseUtils
        firebaseUtils = new FirebaseUtils(this);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new OrganizerEventAdapter(eventList);
        recyclerView.setAdapter(eventAdapter);

        // Fetch organizer events
        fetchOrganizerEvents();

        welcomeText = findViewById(R.id.welcome_text);
        String userName = FirebaseUtils.getUserDisplayName();
        welcomeText.setText("Hello " + userName + "!");
    }

    private void fetchOrganizerEvents() {
        firebaseUtils.getOrganizerEvents()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        Log.d("OrganizerMainActivity", "Event: " + event.getTitle());
                        // Handle displaying events in the RecyclerView
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("OrganizerMainActivity", "Error fetching events", e);
                });
    }

    // Example method to add a new event
    private void addNewEvent() {
        Event newEvent = new Event("Event Title", "2025-01-01", "Location", 20, deviceId);
        firebaseUtils.addEvent(newEvent)
                .addOnSuccessListener(documentReference -> {
                    Log.d("OrganizerMainActivity", "Event added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("OrganizerMainActivity", "Error adding event", e);
                });
    }
}
