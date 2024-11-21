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

/**
 * Activity for displaying a list of invited entrants for a specific event.
 */
public class InvitedEntrantsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EntrantsAdapter adapter;
    private List<Entrant> entrantList;
    private FirebaseFirestore db;

    /**
     * Called when the activity is created. Initializes the UI components and starts loading entrant data.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this contains the data it most recently supplied in {@link #onSaveInstanceState}.
     */
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

    /**
     * Loads the list of invited entrants from the Firestore database for the specified event.
     */
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

    /**
     * Adapter class for binding the entrant data to a RecyclerView.
     */
    private class EntrantsAdapter extends RecyclerView.Adapter<EntrantsAdapter.ViewHolder> {

        private List<Entrant> entrantList;

        /**
         * Constructs an adapter with the given list of entrants.
         *
         * @param entrantList List of entrants to display.
         */
        EntrantsAdapter(List<Entrant> entrantList) {
            this.entrantList = entrantList;
        }

        /**
         * Inflates the layout for each item in the RecyclerView.
         *
         * @param parent   The parent ViewGroup into which the new view will be added after it is bound.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder instance.
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waiting_list_item, parent, false);
            return new ViewHolder(view);
        }

        /**
         * Binds data to the item view for the given position.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the item.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Entrant entrant = entrantList.get(position);
            holder.nameTextView.setText(String.format("%s %s", entrant.getFirstName(), entrant.getLastName()));
        }

        /**
         * Returns the total number of items in the data set.
         *
         * @return The total number of items.
         */
        @Override
        public int getItemCount() {
            return entrantList.size();
        }

        /**
         * ViewHolder class to hold references to the views for each data item.
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;

            /**
             * Constructor to initialize the views for a single item.
             *
             * @param itemView The item view layout.
             */
            ViewHolder(View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.tv_username); // Ensure this ID matches your entrant item layout
            }
        }
    }
}
