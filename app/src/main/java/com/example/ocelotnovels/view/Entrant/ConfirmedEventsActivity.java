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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_events);

        initializeViews();
        initializeFirebase();
        setupRecyclerView();
        loadConfirmedEventsList();
    }

    private void initializeViews() {
        confirmedEventsRecyclerView = findViewById(R.id.confirmed_events_recycler_view);
        emptyStateText = findViewById(R.id.empty_state_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Confirmed Events");
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
        confirmedEventsList = new ArrayList<>();
        confirmedEventsAdapter = new ConfirmedEventsAdapter(this, confirmedEventsList);
        confirmedEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        confirmedEventsRecyclerView.setAdapter(confirmedEventsAdapter);
    }


    private void loadConfirmedEventsList() {
        firebaseUtils = new FirebaseUtils(this);
        String deviceId = firebaseUtils.getDeviceId(this);
        String listType = "confirmedEventsJoined";
        firebaseUtils.fetchUserConfirmedEvents(deviceId, listType, confirmedEventsList, () -> {
            confirmedEventsAdapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }

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

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed(); // Navigate back to the previous activity
        return true;
    }
}