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

/**
 * Activity to display a list of selected entrants for a specific event.
 * Fetches data from a Firestore database and populates a RecyclerView with the entrant details.
 */
public class SelectedEntrantsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EntrantsAdapter adapter;
    private List<Entrant> entrantsList = new ArrayList<>();
    private FirebaseFirestore db;

    /**
     * Called when the activity is starting. Sets up the UI and initializes Firestore database access.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, null.
     */
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

    /**
     * Loads the list of selected entrants from the Firestore database
     * and updates the RecyclerView adapter with the retrieved data.
     */
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

    /**
     * RecyclerView Adapter class for displaying the list of entrants.
     */
    private class EntrantsAdapter extends RecyclerView.Adapter<EntrantsAdapter.ViewHolder> {
        private List<Entrant> entrants;
        private AppCompatActivity context;

        /**
         * Constructor for the adapter.
         *
         * @param context  the activity context
         * @param entrants the list of entrants to display
         */
        EntrantsAdapter(AppCompatActivity context, List<Entrant> entrants) {
            this.context = context;
            this.entrants = entrants;
        }

        /**
         * Inflates the view for individual list items.
         *
         * @param parent   the parent view group
         * @param viewType the type of view
         * @return a new ViewHolder instance
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waiting_list_item, parent, false);
            return new ViewHolder(view);
        }

        /**
         * Binds data to the views for a specific list item.
         *
         * @param holder   the ViewHolder for the current list item
         * @param position the position of the list item
         */
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Entrant entrant = entrants.get(position);
            holder.entrantName.setText(entrant.getFirstName() + " " + entrant.getLastName());
            // You can also set up click listeners here for 'replace' and 'cancel'
        }

        /**
         * Returns the total number of items in the data set.
         *
         * @return the size of the entrants list
         */
        @Override
        public int getItemCount() {
            return entrants.size();
        }

        /**
         * ViewHolder class for caching views for a single entrant item.
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView entrantName;

            /**
             * Constructor for ViewHolder.
             *
             * @param view the root view for the list item
             */
            ViewHolder(View view) {
                super(view);
                entrantName = view.findViewById(R.id.tv_username);
                // Initialize other views like buttons
            }
        }
    }
}
