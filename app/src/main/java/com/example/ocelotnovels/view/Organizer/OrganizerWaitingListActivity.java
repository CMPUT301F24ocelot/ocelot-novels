/**
 * OrganizerWaitingListActivity
 *
 * This activity displays and manages the waiting list for an event.
 * It retrieves the waiting list of users from Firebase, displays it
 * in a RecyclerView, and provides functionality for organizers to
 * sample users from the list using event capacity or a custom capacity.
 *
 * Key features:
 * - Fetch and display a waiting list of users.
 * - Sampling users based on event capacity or custom capacity.
 * - Handles UI updates for an empty state or populated list.
 * - Integrates with Firebase for data retrieval and updates.
 */

package com.example.ocelotnovels.view.Organizer;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.User;
import com.example.ocelotnovels.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

public class OrganizerWaitingListActivity extends AppCompatActivity {

    private static final String TAG = "OrganizerWaitingListActivity";
    private RecyclerView waitingListRecyclerView;
    private OrganizerWaitingListAdapter waitingListAdapter;
    private List<User> waitingListUsers;
    private FirebaseUtils firebaseUtils;
    private TextView emptyStateText;
    private Button sampleButton;
    private String eventId;

    /**
     * Initializes the activity and its views, retrieves event ID,
     * and sets up the RecyclerView and other UI elements.
     *
     * @param savedInstanceState Saved instance state from a previous run.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_waiting_list);

        initializeViews();
        initializeFirebase();

        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Event ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupRecyclerView();
        loadOrganiserWaitingList();

        // Set up button click listener for sampling
        setupSampleButton();
    }

    /**
     * Initializes views used in the activity.
     */
    private void initializeViews() {
        waitingListRecyclerView = findViewById(R.id.waiting_list_recycler_view);
        emptyStateText = findViewById(R.id.empty_state_text);
        sampleButton = findViewById(R.id.sample_users_button);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Waiting List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Initializes Firebase utilities for database interactions.
     */
    private void initializeFirebase() {
        firebaseUtils = FirebaseUtils.getInstance(this);
    }

    /**
     * Sets up the RecyclerView to display the waiting list of users.
     */
    private void setupRecyclerView() {
        waitingListUsers = new ArrayList<>();
        waitingListAdapter = new OrganizerWaitingListAdapter(this, waitingListUsers);

        waitingListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        waitingListRecyclerView.setAdapter(waitingListAdapter);
    }

    /**
     * Loads the waiting list from Firebase and updates the UI accordingly.
     */
    private void loadOrganiserWaitingList() {
        String listType = "waitingList";
        firebaseUtils.fetchOrganiserListEntrants(eventId, listType, waitingListUsers, () -> {
            waitingListAdapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }

    /**
     * Sets up the button to handle sampling options for the waiting list.
     */
    private void setupSampleButton() {
        sampleButton.setOnClickListener(v -> {
            // Show options for sampling (use capacity or set custom capacity)
            showPollingOptions();
        });
    }

    /**
     * Displays polling options for the user to choose event capacity
     * or custom capacity for sampling.
     */
    private void showPollingOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sampling Options");

        String[] options = {"Use Event Capacity", "Set Custom Capacity"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Use event capacity
                performSamplingWithCapacity(null);
            } else if (which == 1) {
                // Show input dialog for custom capacity
                showCustomCapacityInput();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Displays an input dialog for the user to enter a custom capacity
     * for sampling.
     */
    private void showCustomCapacityInput() {
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);
        inputDialog.setTitle("Set Custom Capacity");

        // Input field for custom capacity
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER); // Only numbers allowed
        input.setHint("Enter number of participants");
        inputDialog.setView(input);

        inputDialog.setPositiveButton("Confirm", (dialog, which) -> {
            String inputValue = input.getText().toString().trim();
            if (!inputValue.isEmpty()) {
                try {
                    int customCapacity = Integer.parseInt(inputValue);
                    performSamplingWithCapacity(customCapacity);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid number entered. Try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Capacity cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        inputDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        inputDialog.show();
    }

    /**
     * Performs sampling of users from the waiting list using the specified
     * capacity or event capacity if no custom capacity is provided.
     *
     * @param customCapacity The custom capacity for sampling, or null for event capacity.
     */
    private void performSamplingWithCapacity(Integer customCapacity) {
        firebaseUtils.performPolling(
                eventId,
                customCapacity,
                () -> runOnUiThread(() -> {
                    Toast.makeText(this, "Sampling completed successfully.", Toast.LENGTH_SHORT).show();
                    loadOrganiserWaitingList(); // Reload the waiting list to reflect changes
                }),
                e -> runOnUiThread(() -> {
                    Toast.makeText(this, "Sampling failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error during sampling", e);
                })
        );
    }

    /**
     * Updates the UI to reflect whether the waiting list is empty or populated.
     */
    private void updateEmptyState() {
        runOnUiThread(() -> {
            Log.d(TAG, "Updating empty state. Users count: " + waitingListUsers.size());
            if (waitingListUsers.isEmpty()) {
                emptyStateText.setVisibility(View.VISIBLE);
                waitingListRecyclerView.setVisibility(View.GONE);
            } else {
                emptyStateText.setVisibility(View.GONE);
                waitingListRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Handles navigation back to the previous activity.
     *
     * @return True if navigation is handled, false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed(); // Navigate back to the previous activity
        return true;
    }
}