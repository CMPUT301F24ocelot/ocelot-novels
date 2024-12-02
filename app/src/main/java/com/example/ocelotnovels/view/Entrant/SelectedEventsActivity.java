/**
 * SelectedEventsActivity.java
 *
 * This activity displays a list of events that the user has been selected to join or invited to.
 * It retrieves the events from Firebase, displays them in a RecyclerView, and allows the user to
 * accept or decline invitations. If there are no selected events, an empty state message is shown.
 *
 * Key functionalities:
 * - Fetch and display a list of selected events using Firebase.
 * - Allow the user to accept or decline event invitations.
 * - Update the UI dynamically to reflect changes in the list of events.
 *
 * This activity uses:
 * - RecyclerView to display events.
 * - FirebaseUtils for backend interaction.
 * - A TextView to show an empty state when no events are available.
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

import java.util.ArrayList;
import java.util.List;

public class SelectedEventsActivity extends AppCompatActivity {

    private static final String TAG = "SelectedEventsActivity";

    private RecyclerView selectedEventsRecyclerView;
    private SelectedEventsAdapter selectedEventsAdapter;
    private List<Event> selectedEventsList;
    private FirebaseUtils firebaseUtils;
    private TextView emptyStateText;

    /**
     * Called when the activity is created. Sets up the UI, initializes Firebase, and loads events.
     *
     * @param savedInstanceState The saved state of the activity.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_events);

        initializeViews();
        initializeFirebase();
        setupRecyclerView();
        loadSelectedEvents();
    }

    /**
     * Initializes the views in the activity, including the RecyclerView and the empty state TextView.
     */
    private void initializeViews() {
        selectedEventsRecyclerView = findViewById(R.id.selected_events_recycler_view);
        emptyStateText = findViewById(R.id.empty_state_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Selected Events");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Initializes the FirebaseUtils instance for interacting with Firebase.
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
     * Sets up the RecyclerView to display selected events using a custom adapter.
     */
    private void setupRecyclerView() {
        selectedEventsList = new ArrayList<>();
        selectedEventsAdapter = new SelectedEventsAdapter(selectedEventsList,
                // Accept Event Callback
                event -> respondToInvitation(event, true),
                // Reject Event Callback
                event -> respondToInvitation(event, false)
        );

        selectedEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectedEventsRecyclerView.setAdapter(selectedEventsAdapter);
    }

    /**
     * Loads the list of selected events from Firebase and updates the UI.
     */
    private void loadSelectedEvents() {
        if (firebaseUtils != null) {
            firebaseUtils.fetchUserSelectedEvents(selectedEventsList, () -> {
                Log.d(TAG, "Fetched events: " + selectedEventsList.size());

                selectedEventsAdapter.notifyDataSetChanged();
                updateEmptyState();
            });
        }
    }

    /**
     * Handles the user's response to an event invitation. Accepts or declines the event and updates Firebase.
     *
     * @param event  The event the user is responding to.
     * @param accept True if the user accepts the invitation, false otherwise.
     */
    private void respondToInvitation(Event event, boolean accept) {
        if (firebaseUtils != null) {
            firebaseUtils.respondToEventInvitation(
                    event.getEventId(),
                    accept,
                    aVoid -> runOnUiThread(() -> {
                        // Remove event from the list
                        selectedEventsList.remove(event);
                        selectedEventsAdapter.notifyDataSetChanged();
                        updateEmptyState();

                        // Show appropriate toast
                        String message = accept ?
                                "Event invitation accepted" :
                                "Event invitation declined";
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }),
                    e -> runOnUiThread(() -> {
                        Toast.makeText(this,
                                "Failed to process event invitation",
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error responding to invitation", e);
                    })
            );
            if (accept) {
                firebaseUtils.addConfirmedEvent(event.getEventId(), success -> {
                            Log.d("addConfirmedEvent", "Added user successfully to confirmed list in users");
                        },
                        failure -> {
                            Log.d("addConfirmedEvent", "Error adding user to confirmed list in users");
                        });
            }
        }
    }

    /**
     * Updates the visibility of the empty state message based on the size of the event list.
     */

    private void updateEmptyState() {
        runOnUiThread(() -> {
            if (selectedEventsList.isEmpty()) {
                emptyStateText.setVisibility(View.VISIBLE);
                selectedEventsRecyclerView.setVisibility(View.GONE);
            } else {
                emptyStateText.setVisibility(View.GONE);
                selectedEventsRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Handles navigation up button clicks to return to the previous activity.
     *
     * @return True if the navigation up action is handled.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}