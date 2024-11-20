package com.example.ocelotnovels.view.Entrant;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Event;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class WaitingListActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView waitingListRecyclerView;
    public WaitingListAdapter waitingListAdapter;
    private List<Event> eventList;
    private String deviceId;

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

        FirebaseUtils firebaseUtils = new FirebaseUtils(this);
        deviceId = firebaseUtils.getDeviceId(this);


        firebaseUtils.fetchUserWaitingListEvents(eventList,()->{
            waitingListAdapter.notifyDataSetChanged();
        });
    }




}
