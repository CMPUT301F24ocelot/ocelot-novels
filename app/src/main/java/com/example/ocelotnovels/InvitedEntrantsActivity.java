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

public class InvitedEntrantsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EntrantsAdapter adapter;
    private List<Entrant> entrantList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invited_entrants);

        recyclerView = findViewById(R.id.recyclerView_invited_entrants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        entrantList = new ArrayList<>();
        adapter = new EntrantsAdapter(entrantList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadInvitedEntrants();
    }

    private void loadInvitedEntrants() {
        db.collection("events").document("nwsEG9KZHYrTTAN9azIH") // Replace with your actual event ID
                .collection("InvitedEntrants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Entrant entrant = document.toObject(Entrant.class);
                            entrantList.add(entrant);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("InvitedEntrants", "Error getting documents.", task.getException());
                        Toast.makeText(this, "Error loading invited entrants.", Toast.LENGTH_SHORT).show();
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
                nameTextView = itemView.findViewById(R.id.tv_username); // Ensure this ID matches your entrant item layout
            }
        }
    }
}
