package com.example.ocelotnovels.view.Organizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Organizer;
import com.example.ocelotnovels.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrganizerWaitingListActivity extends AppCompatActivity {

    private static final String TAG = "OrganizerWaitingListActivity";
    private RecyclerView waitingListRecyclerView;
    private OrganizerWaitingListAdapter waitingListAdapter;
    private List<User> waitingListUsers;
    private FirebaseFirestore db;
    private TextView emptyStateText;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_waiting_list);

        initializeViews();
        initializeFirebase();

//        eventId = getIntent().getStringExtra("eventId");
        eventId = "7bd81111-6033-4219-acb5-7ee28e0aeccd";
        if (eventId == null) {
            Toast.makeText(this, "Event ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupRecyclerView();
        loadWaitingList();
    }

    private void initializeViews() {
        waitingListRecyclerView = findViewById(R.id.waiting_list_recycler_view);
        emptyStateText = findViewById(R.id.empty_state_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Waiting List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void setupRecyclerView() {
        waitingListUsers = new ArrayList<>();
        waitingListAdapter = new OrganizerWaitingListAdapter(this, waitingListUsers);

        waitingListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        waitingListRecyclerView.setAdapter(waitingListAdapter);
    }

    private void loadWaitingList() {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> waitingListFromDb = (List<String>) documentSnapshot.get("waitingList");
                        if (waitingListFromDb != null && !waitingListFromDb.isEmpty()) {
                            fetchUserDetails(waitingListFromDb);
                        } else {
                            updateEmptyState();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching waiting list", e);
                    Toast.makeText(this, "Failed to load waiting list", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchUserDetails(List<String> userIds) {
        waitingListUsers.clear();

        for (String userId : userIds) {
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");

                            if (fullName != null && email != null) {
                                // Split fullName into first and last name
                                String[] nameParts = fullName.split(" ", 2);
                                String firstName = nameParts.length > 0 ? nameParts[0] : "";
                                String lastName = nameParts.length > 1 ? nameParts[1] : "";

                                // Create a User object
                                User user = new User(firstName, lastName, email) {
                                };
                                waitingListUsers.add(user);
                                waitingListAdapter.notifyDataSetChanged();
                                updateEmptyState();
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error fetching user details for ID: " + userId, e));
        }
    }


    private void updateEmptyState() {
        runOnUiThread(() -> {
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
