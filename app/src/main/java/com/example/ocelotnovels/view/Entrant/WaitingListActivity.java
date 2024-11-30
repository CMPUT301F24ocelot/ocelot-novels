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

public class WaitingListActivity extends AppCompatActivity {
    private static final String TAG = "WaitingListActivity";
    private RecyclerView waitingListRecyclerView;
    private WaitingListAdapter waitingListAdapter;
    private List<Event> eventList;
    private FirebaseUtils firebaseUtils;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_list);

        initializeViews();
        initializeFirebase();
        setupRecyclerView();
        loadUserEvents();
    }

    private void initializeViews() {
        waitingListRecyclerView = findViewById(R.id.waiting_list_recycler_view);
        emptyStateText = findViewById(R.id.empty_state_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Waiting List");
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
        eventList = new ArrayList<>();
        waitingListAdapter = new WaitingListAdapter(eventList, event -> {
            if (event != null && event.getEventId() != null) {
                leaveEventWaitlist(event);
            }
        });

        waitingListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        waitingListRecyclerView.setAdapter(waitingListAdapter);
    }

    private void loadUserEvents() {
        if (firebaseUtils != null) {
            firebaseUtils.fetchUserJoinedEvents(eventList, () -> {
                waitingListAdapter.notifyDataSetChanged();
                updateEmptyState();
            });
        }
    }

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


    @Override
    public boolean onSupportNavigateUp() {
        // Navigate back to MainActivity explicitly
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }


}