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
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.example.ocelotnovels.view.Entrant.WaitingListAdapter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class OrganizerWaitingListActivity extends AppCompatActivity {

    private static final String TAG = "OrganizerWaitingListActivity";
    private RecyclerView waitingListRecyclerView;
    private OrganizerWaitingListAdapter waitingListAdapter;
    private List<User> waitingListUsers;
    private FirebaseFirestore db;
    private TextView emptyStateText;
    private String eventId;
    private FirebaseUtils firebaseUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_waiting_list);

        initializeViews();
        initializeFirebase();

        eventId = getIntent().getStringExtra("eventId").toString();
//        eventId = "7bd81111-6033-4219-acb5-7ee28e0aeccd";
        Log.d("EVENTIDW", eventId);
        if (eventId == null) {
            Toast.makeText(this, "Event ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupRecyclerView();
//        loadWaitingList();
        loadOrganiserWaitingList();
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


    private void loadOrganiserWaitingList() {
        firebaseUtils = new FirebaseUtils(this);
        firebaseUtils.fetchOrganiserWaitingListEntrants(eventId, waitingListUsers, () -> {
            waitingListAdapter.notifyDataSetChanged();
            updateEmptyState();
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
