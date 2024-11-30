package com.example.ocelotnovels.view.Organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    private void initializeViews() {
        waitingListRecyclerView = findViewById(R.id.waiting_list_recycler_view);
        emptyStateText = findViewById(R.id.empty_state_text);
        sampleButton = findViewById(R.id.sample_users_button);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Waiting List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeFirebase() {
        firebaseUtils = FirebaseUtils.getInstance(this);
    }

    private void setupRecyclerView() {
        waitingListUsers = new ArrayList<>();
        waitingListAdapter = new OrganizerWaitingListAdapter(this, waitingListUsers);

        waitingListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        waitingListRecyclerView.setAdapter(waitingListAdapter);
    }

    private void loadOrganiserWaitingList() {
        String listType = "waitingList";
        firebaseUtils.fetchOrganiserListEntrants(eventId, listType, waitingListUsers, () -> {
            waitingListAdapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }

    private void setupSampleButton() {
        sampleButton.setOnClickListener(v -> {
            // Trigger the sampling process
            firebaseUtils.performPolling(
                    eventId,
                    () -> runOnUiThread(() -> {
                        Toast.makeText(this, "Sampling completed successfully.", Toast.LENGTH_SHORT).show();
                        loadOrganiserWaitingList(); // Reload the waiting list to reflect changes
                    }),
                    e -> runOnUiThread(() -> {
                        Toast.makeText(this, "Sampling failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error during sampling", e);
                    })
            );

            firebaseUtils.addSelectedEvent(eventId, success -> {
                        // Event added to selectedEventsJoined
//                        Toast.makeText(this, "Sampling completed successfully.", Toast.LENGTH_SHORT).show();
                        Log.d("addSelectedEvent", "Added user successfully");

                    },
                    failure -> {
                        // Handle error
                        Log.d("addSelectedEvent", "Error adding user to selected list");
                    });

        });
    }

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

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed(); // Navigate back to the previous activity
        return true;
    }
}