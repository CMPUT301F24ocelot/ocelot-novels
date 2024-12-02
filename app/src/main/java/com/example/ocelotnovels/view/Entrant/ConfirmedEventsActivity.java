/**
 * This activity displays a list of confirmed events for the user.
 * It uses a RecyclerView to show the events and integrates with Firebase to fetch data.
 * The activity provides functionality for handling an empty state if no events are found.
 * Users can navigate back to the previous activity using the back button.
 */

package com.example.ocelotnovels.view.Entrant;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Event;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.example.ocelotnovels.view.Organizer.OrganizerWaitingListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ConfirmedEventsActivity extends AppCompatActivity {

    private static final String TAG = "ConfirmedEventsActivity";

    private RecyclerView confirmedEventsRecyclerView;
    private ConfirmedEventsAdapter confirmedEventsAdapter;
    private List<Event> confirmedEventsList;
    private FirebaseUtils firebaseUtils;
    private TextView emptyStateText;

    /**
     * Called when the activity is first created.
     * Initializes the views, Firebase, RecyclerView, and loads the confirmed events list.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_events);

        initializeViews();
        initializeFirebase();
        setupRecyclerView();
        loadConfirmedEventsList();
    }

    /**
     * Initializes the views and sets up the action bar with a title and back button.
     */
    private void initializeViews() {
        confirmedEventsRecyclerView = findViewById(R.id.confirmed_events_recycler_view);
        emptyStateText = findViewById(R.id.empty_state_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Confirmed Events");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Initializes the Firebase utility class for database interactions.
     * Displays an error message and closes the activity if initialization fails.
     */
    private void initializeFirebase() {
        try {
            firebaseUtils = FirebaseUtils.getInstance(this);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase", e);
            Toast.makeText(this, "Error initializing app", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Sets up the RecyclerView with an adapter and a linear layout manager.
     */
    private void setupRecyclerView() {
        confirmedEventsList = new ArrayList<>();
        confirmedEventsAdapter = new ConfirmedEventsAdapter(this, confirmedEventsList);
        confirmedEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        confirmedEventsRecyclerView.setAdapter(confirmedEventsAdapter);
    }


    /**
     * Loads the list of confirmed events from Firebase and updates the RecyclerView.
     */

    private void loadConfirmedEventsList() {
        firebaseUtils = new FirebaseUtils(this);
        String deviceId = firebaseUtils.getDeviceId(this);
        String listType = "confirmedEventsJoined";
        firebaseUtils.fetchUserConfirmedEvents(deviceId, listType, confirmedEventsList, () -> {
            confirmedEventsAdapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }

    /**
     * Updates the empty state view based on whether the confirmed events list is empty.
     */
    private void updateEmptyState() {
        runOnUiThread(() -> {
            Log.d(TAG, "Updating empty state. Users count: " + confirmedEventsList.size());
            if (confirmedEventsList.isEmpty()) {
                emptyStateText.setVisibility(View.VISIBLE);
                confirmedEventsRecyclerView.setVisibility(View.GONE);
            } else {
                emptyStateText.setVisibility(View.GONE);
                confirmedEventsRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Handles the back button functionality to navigate to the previous activity.
     *
     * @return true to indicate the action was handled.
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed(); // Navigate back to the previous activity
        return true;
    }
}