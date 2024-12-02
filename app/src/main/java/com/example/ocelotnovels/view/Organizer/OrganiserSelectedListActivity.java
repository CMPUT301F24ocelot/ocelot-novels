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

    private void initializeViews() {
        selectedListRecyclerView = findViewById(R.id.selected_list_recycler_view);
        emptyStateText = findViewById(R.id.selected_empty_state_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Selected List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeFirebase() {
        try {
            firebaseUtils = FirebaseUtils.getInstance(this);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase", e);
            Toast.makeText(this, "Error initializing app", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

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
    }

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

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed(); // Navigate back to the previous activity
        return true;
    }

}