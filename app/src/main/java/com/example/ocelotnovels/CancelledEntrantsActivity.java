/**
 * CancelledEntrantsActivity.java
 *
 * This activity is responsible for displaying a list of participants who have cancelled their
 * registration for an event. It fetches data from a Firestore database collection named
 * "CancelledParticipants" associated with a specific event document. The activity uses a
 * RecyclerView to display the list and includes an adapter to bind the data to the view.
 *
 * Features:
 * - Fetch cancelled entrants from Firestore and display them in a RecyclerView.
 * - Provide a user-friendly view for organizers to review the list of cancellations.
 *
 * Dependencies:
 * - Firebase Firestore
 * - AndroidX RecyclerView
 * - Entrant model class
 *
 * Layouts used:
 * - activity_cancelled_entrants.xml (Main layout for the activity)
 * - waiting_list_item.xml (Item layout for each entrant)
 */

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
 * Activity to display a list of participants who have cancelled their event registration.
 */
public class CancelledEntrantsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EntrantsAdapter adapter;
    private List<Entrant> entrantList;
    private FirebaseFirestore db;

    /**
     * Called when the activity is starting. Sets up the layout, initializes RecyclerView,
     * Firestore instance, and loads the list of cancelled entrants.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState().
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled_entrants);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView_cancelled_entrants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        entrantList = new ArrayList<>();
        adapter = new EntrantsAdapter(entrantList);
        recyclerView.setAdapter(adapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Load the list of cancelled entrants
        loadCancelledEntrants();
    }

    /**
     * Fetches the list of cancelled entrants from the Firestore database and updates the RecyclerView.
     */
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

    /**
     * RecyclerView Adapter for displaying the list of cancelled entrants.
     */
    private class EntrantsAdapter extends RecyclerView.Adapter<EntrantsAdapter.ViewHolder> {

        private List<Entrant> entrantList;

        /**
         * Constructor for EntrantsAdapter.
         *
         * @param entrantList The list of entrants to display.
         */
        EntrantsAdapter(List<Entrant> entrantList) {
            this.entrantList = entrantList;
        }

        /**
         * Inflates the layout for individual entrant items.
         *
         * @param parent   The parent view group.
         * @param viewType The view type of the new view.
         * @return A new ViewHolder for the entrant item.
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waiting_list_item, parent, false);
            return new ViewHolder(view);
        }

        /**
         * Binds data to the ViewHolder for a specific position.
         *
         * @param holder   The ViewHolder to bind data to.
         * @param position The position of the item within the dataset.
         */
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Entrant entrant = entrantList.get(position);
            holder.nameTextView.setText(String.format("%s %s", entrant.getFirstName(), entrant.getLastName()));
        }

        /**
         * Returns the total number of items in the dataset.
         *
         * @return The number of items.
         */
        @Override
        public int getItemCount() {
            return entrantList.size();
        }

        /**
         * ViewHolder class for individual entrant items.
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;

            /**
             * Constructor for ViewHolder.
             *
             * @param itemView The item view.
             */
            ViewHolder(View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.tv_username); // Ensure this ID matches your layout
            }
        }
    }
}
