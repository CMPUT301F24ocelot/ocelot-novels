package com.example.ocelotnovels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.model.Entrant;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class SelectedEntrantsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EntrantsAdapter adapter;
    private List<Entrant> entrantsList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_entrants);

        recyclerView = findViewById(R.id.recyclerView_selected_entrants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantsAdapter(this, entrantsList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadSelectedEntrants();
    }

    private void loadSelectedEntrants() {
        db.collection("events")
                .document("nwsEG9KZHYrTTAN9azIH")
                .collection("selectedParticipants")
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
                        // Handle the error
                    }
                });
    }

    private class EntrantsAdapter extends RecyclerView.Adapter<EntrantsAdapter.ViewHolder> {
        private List<Entrant> entrants;
        private AppCompatActivity context;

        EntrantsAdapter(AppCompatActivity context, List<Entrant> entrants) {
            this.context = context;
            this.entrants = entrants;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waiting_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Entrant entrant = entrants.get(position);
            holder.entrantName.setText(entrant.getFirstName() + " " + entrant.getLastName());
            // You can also set up click listeners here for 'replace' and 'cancel'
        }

        @Override
        public int getItemCount() {
            return entrants.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView entrantName;

            ViewHolder(View view) {
                super(view);
                entrantName = view.findViewById(R.id.tv_username);
                // Initialize other views like buttons
            }
        }
    }
}
