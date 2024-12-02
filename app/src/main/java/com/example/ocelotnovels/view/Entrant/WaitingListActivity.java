/**
 * WaitingListActivity.java
 *
 * This class represents the activity for managing the waiting list of events that a user has joined.
 * It interacts with Firebase to fetch the list of events, display them in a RecyclerView, and
 * allow users to leave the waitlist of specific events. The activity provides a clear and responsive UI
 * to display the events and updates dynamically when a user leaves an event waitlist.
 *
 * Key Features:
 * - Fetching and displaying user-joined events from Firebase.
 * - Allowing users to leave the waitlist of a specific event.
 * - Handling Firebase operations for managing event data and user locations.
 * - Updating the UI to reflect changes in the event list dynamically.
 * - Providing navigation back to the main activity.
 */

package com.example.ocelotnovels.view.Entrant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.MainActivity;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Event;
import com.example.ocelotnovels.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The WaitingListActivity class displays a list of events the user has joined in a waiting list.
 * Users can leave specific event waitlists, and the UI updates dynamically based on Firebase data.
 */
public class WaitingListActivity extends AppCompatActivity {
    private static final String TAG = "WaitingListActivity";
    private RecyclerView waitingListRecyclerView;
    private WaitingListAdapter waitingListAdapter;
    private List<Event> eventList;
    private FirebaseUtils firebaseUtils;
    private TextView emptyStateText;

    /**
     * Called when the activity is first created. Sets up the UI, initializes Firebase,
     * and loads the list of events the user has joined.
     *
     * @param savedInstanceState If the activity is being re-initialized after being
     *                           previously shut down, this Bundle contains the most recent data.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_list);

        initializeViews();
        initializeFirebase();
        setupRecyclerView();
        loadUserEvents();
    }

    /**
     * Initializes the UI components of the activity.
     */
    private void initializeViews() {
        waitingListRecyclerView = findViewById(R.id.waiting_list_recycler_view);
        emptyStateText = findViewById(R.id.empty_state_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Waiting List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Initializes Firebase utilities for interacting with the database.
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
     * Sets up the RecyclerView to display the waiting list of events.
     */
    private void setupRecyclerView() {
        eventList = new ArrayList<>();
        waitingListAdapter = new WaitingListAdapter(eventList, event -> {
            if (event != null && event.getEventId() != null) {
                leaveEventWaitlist(event);
            }
        });

        waitingListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        waitingListRecyclerView.setAdapter(waitingListAdapter);
    }

    /**
     * Loads the events the user has joined and updates the RecyclerView.
     */
    private void loadUserEvents() {
        if (firebaseUtils != null) {
            firebaseUtils.fetchUserJoinedEvents(eventList, () -> {
                waitingListAdapter.notifyDataSetChanged();
                updateEmptyState();
            });
        }
    }

    /**
     * Removes the user from the waitlist of a specific event and updates Firebase.
     *
     * @param event The event to leave the waitlist for.
     */
    private void leaveEventWaitlist(Event event) {
        if (firebaseUtils != null) {
            firebaseUtils.leaveEventWaitlist(
                    event.getEventId(),
                    aVoid -> runOnUiThread(() -> {
                        // Remove the event and corresponding location
                        int eventIndex = eventList.indexOf(event);
                        if (eventIndex >= 0) {
                            removeUserLocation(eventIndex); // Remove location using index
                        }

                        // Remove event from the eventList
                        eventList.remove(event);
                        waitingListAdapter.notifyDataSetChanged();

                        // Update UI
                        updateEmptyState();

                        Toast.makeText(WaitingListActivity.this,
                                "Successfully left event waitlist", Toast.LENGTH_SHORT).show();
                    }),
                    e -> runOnUiThread(() -> {
                        Toast.makeText(WaitingListActivity.this,
                                "Failed to leave event waitlist", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error leaving waitlist", e);
                    })
            );
        }
    }

    /**
     * Removes the user's location from the waitlist in Firebase.
     *
     * @param index The index of the event's location in the user's location list.
     */
    private void removeUserLocation(int index) {
        firebaseUtils.getUserDocument().get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Object> eventLocations = (List<Object>) documentSnapshot.get("eventLocations");
                if (eventLocations != null && index >= 0 && index < eventLocations.size()) {
                    // Remove the location at the specified index
                    eventLocations.remove(index);
                    firebaseUtils.getUserDocument().update("eventLocations", eventLocations)
                            .addOnSuccessListener(aVoid -> Log.i(TAG, "Location removed successfully"))
                            .addOnFailureListener(e -> Log.e(TAG, "Error removing location", e));
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching user document", e));
    }


    /**
     * Updates the UI based on whether the event list is empty or not.
     */
    private void updateEmptyState() {
        runOnUiThread(() -> {
            if (eventList.isEmpty()) {
                emptyStateText.setVisibility(View.VISIBLE);
                waitingListRecyclerView.setVisibility(View.GONE);
            } else {
                emptyStateText.setVisibility(View.GONE);
                waitingListRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Handles navigation back to the main activity when the back button is pressed.
     *
     * @return True if the navigation was handled successfully.
     */
    @Override
    public boolean onSupportNavigateUp() {
        // Navigate back to MainActivity explicitly
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        return true;
    }

    /**
     * Handles the back button press and navigates to the main activity.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }


}