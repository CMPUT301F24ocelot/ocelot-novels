/**
 * OrganiserCancelledListActivity
 *
 * This activity is responsible for displaying the list of users who have been
 * cancelled from an event. It retrieves data from Firestore using FirebaseUtils
 * and updates the UI based on the fetched data. The activity includes functionality
 * to initialize views, configure a RecyclerView for the list, and handle empty states.
 *
 * Key Features:
 * - Fetch and display the cancelled list of users for a specific event.
 * - Show an empty state message if no users are found.
 * - Use Firebase Firestore for data storage and retrieval.
 * - Back navigation support using the action bar.
 */

package com.example.ocelotnovels.view.Organizer;

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
import com.example.ocelotnovels.model.User;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrganiserCancelledListActivity extends AppCompatActivity {

    private static final String TAG = "OrganiserCancelledListActivity";
    private RecyclerView cancelledListRecyclerView;
    private OrganizerWaitingListAdapter cancelledListAdapter;
    private List<User> cancelledListUsers;
    private FirebaseFirestore db;
    private TextView emptyStateText;
    private String eventId;
    private FirebaseUtils firebaseUtils;

    /**
     * Called when the activity is created.
     * Initializes views, Firebase, and sets up the RecyclerView.
     * Fetches the cancelled list data for the provided event ID.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organiser_cancelled_list);

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
        loadOrganiserSelectedList();
    }

    /**
     * Initializes the views for the activity, including setting the title for the action bar.
     */
    private void initializeViews() {
        cancelledListRecyclerView = findViewById(R.id.cancelled_list_recycler_view);
        emptyStateText = findViewById(R.id.cancelled_empty_state_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Cancelled List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Initializes the Firebase Firestore database instance.
     */
    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Configures the RecyclerView with an adapter and layout manager for displaying the list.
     */
    private void setupRecyclerView() {
        cancelledListUsers = new ArrayList<>();
        cancelledListAdapter = new OrganizerWaitingListAdapter(this, cancelledListUsers);

        cancelledListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cancelledListRecyclerView.setAdapter(cancelledListAdapter);
    }

    /**
     * Loads the list of cancelled users for the event from Firestore and updates the UI.
     */
    private void loadOrganiserSelectedList() {
        firebaseUtils = new FirebaseUtils(this);
        String listType = "cancelledList";
        firebaseUtils.fetchOrganiserListEntrants(eventId, listType, cancelledListUsers, () -> {
            cancelledListAdapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }

    /**
     * Updates the empty state view based on whether the cancelled user list is empty.
     */
    private void updateEmptyState() {
        runOnUiThread(() -> {
            Log.d(TAG, "Updating empty state. Users count: " + cancelledListUsers.size());
            if (cancelledListUsers.isEmpty()) {
                emptyStateText.setVisibility(View.VISIBLE);
                cancelledListRecyclerView.setVisibility(View.GONE);
            } else {
                emptyStateText.setVisibility(View.GONE);
                cancelledListRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Handles the action bar's back navigation button press.
     *
     * @return true if navigation is handled successfully.
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed(); // Navigate back to the previous activity
        return true;
    }
}