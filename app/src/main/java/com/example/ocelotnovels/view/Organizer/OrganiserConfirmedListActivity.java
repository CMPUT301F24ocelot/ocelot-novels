/**
 * OrganiserConfirmedListActivity displays a list of users who are confirmed for an event.
 * This activity retrieves data from Firebase Firestore and populates a RecyclerView with
 * the confirmed entrants for a specific event. It handles UI initialization, Firebase setup,
 * and updates the UI based on data fetched from the database.
 *
 * Features:
 * - Displays a list of confirmed users for an event.
 * - Shows an empty state message if no users are confirmed.
 * - Fetches data dynamically using FirebaseUtils.
 * - Supports navigation back to the previous activity.
 */
package com.example.ocelotnovels.view.Organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.User;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrganiserConfirmedListActivity extends AppCompatActivity {

    private static final String TAG = "OrganiserConfirmedListActivity";
    private RecyclerView confirmedListRecyclerView;
    private OrganizerWaitingListAdapter confirmedListAdapter;
    private List<User> confirmedListUsers;
    private FirebaseFirestore db;
    private TextView emptyStateText;
    private String eventId;
    private FirebaseUtils firebaseUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organiser_confirmed_list);

        initializeViews();
        initializeFirebase();

        eventId = getIntent().getStringExtra("eventId").toString();
        Log.d("EVENTIDW", eventId);
        if (eventId == null) {
            Toast.makeText(this, "Event ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupRecyclerView();
        loadOrganiserConfirmedList();
    }


    /**
     * Initializes the views in the activity, such as the RecyclerView and empty state TextView.
     * Sets up the action bar with a title and a back button.
     */
    private void initializeViews() {
        confirmedListRecyclerView = findViewById(R.id.confirmed_list_recycler_view);
        emptyStateText = findViewById(R.id.confirmed_empty_state_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Confirmed List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Initializes the Firebase Firestore instance for database operations.
     */
    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Sets up the RecyclerView with a LinearLayoutManager and an adapter to display the list of confirmed users.
     */
    private void setupRecyclerView() {
        confirmedListUsers = new ArrayList<>();
        confirmedListAdapter = new OrganizerWaitingListAdapter(this, confirmedListUsers);

        confirmedListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        confirmedListRecyclerView.setAdapter(confirmedListAdapter);
    }

    /**
     * Loads the list of confirmed users for the event from Firebase Firestore.
     * Uses FirebaseUtils to fetch the data and updates the UI once the data is retrieved.
     */
    private void loadOrganiserConfirmedList() {
        firebaseUtils = new FirebaseUtils(this);
        String listType = "confirmedList";
        firebaseUtils.fetchOrganiserListEntrants(eventId, listType, confirmedListUsers, () -> {
            confirmedListAdapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }

    /**
     * Updates the UI to show an empty state message if no confirmed users are found.
     * Otherwise, displays the list of confirmed users.
     */
    private void updateEmptyState() {
        runOnUiThread(() -> {
            Log.d(TAG, "Updating empty state. Users count: " + confirmedListUsers.size());
            if (confirmedListUsers.isEmpty()) {
                emptyStateText.setVisibility(View.VISIBLE);
                confirmedListRecyclerView.setVisibility(View.GONE);
            } else {
                emptyStateText.setVisibility(View.GONE);
                confirmedListRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Handles the navigation up (back button) functionality in the action bar.
     * Returns the user to the previous activity.
     *
     * @return true if the navigation was successful.
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed(); // Navigate back to the previous activity
        return true;
    }
}