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

public class WaitingListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private List<Entrant> entrantsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitinglist_entrants);

        recyclerView = findViewById(R.id.recyclerView_waitinglist_entrants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        entrantsList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();

        loadWaitingList();
    }

    private void loadWaitingList() {
        firestore.collection("events").document("nwsEG9KZHYrTTAN9azIH").collection("WaitingList")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Entrant entrant = document.toObject(Entrant.class);
                            entrantsList.add(entrant);
                        }
                        updateRecyclerView();
                    } else {
                        Log.e("WaitingListActivity", "Error getting documents: ", task.getException());
                        Toast.makeText(this, "Error loading list", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateRecyclerView() {
        EntrantsAdapter adapter = new EntrantsAdapter(entrantsList);
        recyclerView.setAdapter(adapter);
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
