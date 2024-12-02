/**
 * OrganiserSelectedListActivity
 *
 * This activity displays a list of users selected for an event in a RecyclerView.
 * The data is fetched from Firebase Firestore based on the provided event ID.
 * If no users are found, an empty state message is displayed.
 * The activity also includes navigation support to go back to the previous screen.
 *
 * Key Features:
 * - Displays a list of selected users for an event.
 * - Handles empty state visibility when no users are available.
 * - Fetches data dynamically from Firebase Firestore.
 * - Ensures proper navigation to the previous activity.
 *
 * Layouts:
 * - activity_organiser_selected_list.xml (RecyclerView and empty state TextView).
 */
package com.example.ocelotnovels.view.Organizer;

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
import com.example.ocelotnovels.model.User;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.example.ocelotnovels.view.Organizer.OrganiserSelectedListAdapter;

import java.util.ArrayList;
import java.util.List;

public class OrganiserSelectedListActivity extends AppCompatActivity {
    private static final String TAG = "OrganiserSelectedListActivity";
    private RecyclerView selectedListRecyclerView;
    private OrganiserSelectedListAdapter selectedListAdapter;
    private List<User> selectedUsersList;
    private FirebaseUtils firebaseUtils;
    private TextView emptyStateText;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organiser_selected_list);

        // Get eventId from intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "Event ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        initializeFirebase();
        setupRecyclerView();
        loadSelectedList();
    }

  
    /**
     * Initializes UI components for the activity.
     * Sets up the RecyclerView and empty state TextView.
     */
    private void initializeViews() {
        selectedListRecyclerView = findViewById(R.id.selected_list_recycler_view);
        emptyStateText = findViewById(R.id.selected_empty_state_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Selected List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Initializes Firebase Firestore instance.
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
     * Sets up the RecyclerView for displaying the selected users list.
     * Configures the adapter and layout manager.
     */
    private void setupRecyclerView() {
        selectedUsersList = new ArrayList<>();
        selectedListAdapter = new OrganiserSelectedListAdapter(selectedUsersList, user -> {
            if (user != null && eventId != null) {
                cancelEntrant(user);
            }
        });

        selectedListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        selectedListRecyclerView.setAdapter(selectedListAdapter);
    }


    private void loadSelectedList() {
        if (firebaseUtils != null) {
            firebaseUtils.fetchOrganiserListEntrants(eventId, "selectedList", selectedUsersList, () -> {
                selectedListAdapter.notifyDataSetChanged();
                updateEmptyState();
            });
        }
    }

    private void cancelEntrant(User user) {
        Log.d("USERR", String.valueOf(user.getDevice_ID()));
        if (firebaseUtils != null) {
            firebaseUtils.removeEntrantFromEvent(
                    eventId,
                    user.getDevice_ID(),
                    aVoid -> runOnUiThread(() -> {
                        // Remove user from the list
                        selectedUsersList.remove(user);
                        selectedListAdapter.notifyDataSetChanged();
                        updateEmptyState();

                        Toast.makeText(OrganiserSelectedListActivity.this,
                                "Successfully canceled entrant", Toast.LENGTH_SHORT).show();
                    }),
                    e -> runOnUiThread(() -> {
                        Toast.makeText(OrganiserSelectedListActivity.this,
                                "Failed to cancel entrant", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error canceling entrant", e);
                    })
            );
        }

    /**
     * Loads the list of selected users for the given event ID from Firebase.
     * Updates the adapter and manages the empty state based on the data fetched.
     */
    private void loadOrganiserSelectedList() {
        firebaseUtils = new FirebaseUtils(this);
        String listType = "selectedList";
        firebaseUtils.fetchOrganiserListEntrants(eventId, listType, selectedListUsers, () -> {
            selectedListAdapter.notifyDataSetChanged();
            updateEmptyState();
        });

    }

    /**
     * Updates the visibility of the empty state message based on the user list size.
     * If the list is empty, displays a message; otherwise, shows the RecyclerView.
     */
    private void updateEmptyState() {
        runOnUiThread(() -> {
            if (selectedUsersList.isEmpty()) {
                emptyStateText.setVisibility(View.VISIBLE);
                selectedListRecyclerView.setVisibility(View.GONE);
            } else {
                emptyStateText.setVisibility(View.GONE);
                selectedListRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Handles navigation back to the previous activity when the back button is pressed.
     *
     * @return true if navigation is handled successfully, false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed(); // Navigate back to the previous activity
        return true;
    }

}