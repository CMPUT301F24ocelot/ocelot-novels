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
import com.example.ocelotnovels.model.User;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrganiserConfirmedListActivity extends AppCompatActivity {

    private static final String TAG = "OrganiserConfirmedListActivity";
    private RecyclerView confirmedListRecyclerView;
    private OrganizerWaitingListAdapter confirmedListAdapter;
    private List<User> confirmedListUsers;
    private FirebaseFirestore db;
    private TextView emptyStateText;
    private String eventId;
    private FirebaseUtils firebaseUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organiser_confirmed_list);

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
        loadOrganiserConfirmedList();
    }



    private void initializeViews() {
        confirmedListRecyclerView = findViewById(R.id.confirmed_list_recycler_view);
        emptyStateText = findViewById(R.id.confirmed_empty_state_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Confirmed List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void setupRecyclerView() {
        confirmedListUsers = new ArrayList<>();
        confirmedListAdapter = new OrganizerWaitingListAdapter(this, confirmedListUsers);

        confirmedListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        confirmedListRecyclerView.setAdapter(confirmedListAdapter);
    }


    private void loadOrganiserConfirmedList() {
        firebaseUtils = new FirebaseUtils(this);
        String listType = "confirmedList";
        firebaseUtils.fetchOrganiserListEntrants(eventId, listType, confirmedListUsers, () -> {
            confirmedListAdapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }

    private void updateEmptyState() {
        runOnUiThread(() -> {
            Log.d(TAG, "Updating empty state. Users count: " + confirmedListUsers.size());
            if (confirmedListUsers.isEmpty()) {
                emptyStateText.setVisibility(View.VISIBLE);
                confirmedListRecyclerView.setVisibility(View.GONE);
            } else {
                emptyStateText.setVisibility(View.GONE);
                confirmedListRecyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed(); // Navigate back to the previous activity
        return true;
    }
}