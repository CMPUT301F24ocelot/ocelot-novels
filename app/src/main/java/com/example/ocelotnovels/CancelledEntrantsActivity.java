package com.example.ocelotnovels;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.model.Entrant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CancelledEntrantsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EntrantsAdapter adapter;
    private List<Entrant> entrantList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_entrants);

        recyclerView = findViewById(R.id.recyclerView_cancelled_entrants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        entrantList = new ArrayList<>();
        adapter = new EntrantsAdapter(entrantList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadCancelledEntrants();
    }

    private void loadCancelledEntrants() {
        db.collection("events").document("nwsEG9KZHYrTTAN9azIH") // Replace with your actual event ID
                .collection("CancelledParticipants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Entrant entrant = document.toObject(Entrant.class);
                            entrantList.add(entrant);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("CancelledEntrants", "Error getting documents.", task.getException());
                        Toast.makeText(this, "Error loading cancelled entrants.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class EntrantsAdapter extends RecyclerView.Adapter<EntrantsAdapter.ViewHolder> {

        private List<Entrant> entrantList;

        EntrantsAdapter(List<Entrant> entrantList) {
            this.entrantList = entrantList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waiting_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Entrant entrant = entrantList.get(position);
            holder.nameTextView.setText(String.format("%s %s", entrant.getFirstName(), entrant.getLastName()));
        }

        @Override
        public int getItemCount() {
            return entrantList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;

            ViewHolder(View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.tv_username); // Make sure this ID matches your layout
            }
        }
    }
}
