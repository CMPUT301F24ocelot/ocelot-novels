package com.example.ocelotnovels.view.Entrant;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class WaitingListActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView waitingListRecyclerView;
    private WaitingListAdapter waitingListAdapter;
    private List<Event> eventList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_list);

        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();
        waitingListRecyclerView = findViewById(R.id.waiting_list_recycler_view);
        waitingListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        waitingListAdapter = new WaitingListAdapter(this, eventList);
        waitingListRecyclerView.setAdapter(waitingListAdapter);

        fetchUserWaitingListEvents();
    }

    private void fetchUserWaitingListEvents() {
        String userDeviceId = getUserDeviceId();  // Method to get current user's device ID

        db.collection("events")
                .whereArrayContains("waitList", userDeviceId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        eventList.add(event);
                    }
                    waitingListAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private String getUserDeviceId() {
        // Get the user's device ID, which could be stored in SharedPreferences or FirebaseAuth
        return "user_device_id";  // Placeholder for actual deviceId retrieval
    }
}
