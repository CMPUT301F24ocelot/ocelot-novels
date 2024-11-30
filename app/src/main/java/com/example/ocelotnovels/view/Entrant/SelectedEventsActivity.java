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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_events);

        initializeViews();
        initializeFirebase();
        setupRecyclerView();
        loadSelectedEvents();
    }

    private void initializeViews() {
        selectedEventsRecyclerView = findViewById(R.id.selected_events_recycler_view);
        emptyStateText = findViewById(R.id.empty_state_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Selected Events");
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

    private void loadSelectedEvents() {
        if (firebaseUtils != null) {
            firebaseUtils.fetchUserSelectedEvents(selectedEventsList, () -> {
                Log.d(TAG, "Fetched events: " + selectedEventsList.size());

                selectedEventsAdapter.notifyDataSetChanged();
                updateEmptyState();
            });
        }
    }

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

            firebaseUtils.addConfirmedEvent(event.getEventId(), success -> {
                        Log.d("addConfirmedEvent", "Added user successfully to confirmed list in users");
                    },
                    failure -> {
                        Log.d("addConfirmedEvent", "Error adding user to confirmed list in users");
                    });
        }
    }

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}