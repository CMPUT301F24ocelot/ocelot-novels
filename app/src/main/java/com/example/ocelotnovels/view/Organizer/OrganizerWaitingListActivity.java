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
            // Show options for sampling (use capacity or set custom capacity)
            showPollingOptions();
        });
    }

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