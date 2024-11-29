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


    private void initializeViews() {
        cancelledListRecyclerView = findViewById(R.id.cancelled_list_recycler_view);
        emptyStateText = findViewById(R.id.cancelled_empty_state_text);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Cancelled List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void setupRecyclerView() {
        cancelledListUsers = new ArrayList<>();
        cancelledListAdapter = new OrganizerWaitingListAdapter(this, cancelledListUsers);

        cancelledListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cancelledListRecyclerView.setAdapter(cancelledListAdapter);
    }


    private void loadOrganiserSelectedList() {
        firebaseUtils = new FirebaseUtils(this);
        String listType = "cancelledList";
        firebaseUtils.fetchOrganiserListEntrants(eventId, listType, cancelledListUsers, () -> {
            cancelledListAdapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }

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

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed(); // Navigate back to the previous activity
        return true;
    }
}