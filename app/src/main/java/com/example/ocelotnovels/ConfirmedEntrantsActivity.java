package com.example.ocelotnovels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.model.Entrant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ConfirmedEntrantsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private List<Entrant> entrantsList;
    private EntrantsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_entrants);

        // Setup the toolbar
        setSupportActionBar(findViewById(R.id.toolbar_confirmed_entrants));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView_confirmed_entrants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        entrantsList = new ArrayList<>();
        adapter = new EntrantsAdapter(entrantsList);
        recyclerView.setAdapter(adapter);
        firestore = FirebaseFirestore.getInstance();

        loadConfirmedEntrants();
    }

    private void loadConfirmedEntrants() {
        // Fetch confirmed entrants from Firestore
        firestore.collection("events").document("nwsEG9KZHYrTTAN9azIH").collection("ConfirmedEntrants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        entrantsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Entrant entrant = document.toObject(Entrant.class);
                            entrantsList.add(entrant);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle possible errors
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Close this activity on back arrow click
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class EntrantsAdapter extends RecyclerView.Adapter<EntrantsAdapter.ViewHolder> {

        private List<Entrant> entrantsList;

        EntrantsAdapter(List<Entrant> entrantsList) {
            this.entrantsList = entrantsList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waiting_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Entrant entrant = entrantsList.get(position);
            holder.nameTextView.setText(String.format("%s %s", entrant.getFirstName(), entrant.getLastName()));
            // Additional fields can be set here if your Entrant model includes more data
        }

        @Override
        public int getItemCount() {
            return entrantsList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;

            ViewHolder(View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.tv_username);
            }
        }
    }
}
