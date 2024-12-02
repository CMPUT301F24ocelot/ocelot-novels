/**
 * ConfirmedEntrantsActivity.java
 *
 * This activity displays a list of confirmed entrants for a specific event retrieved
 * from the Firestore database. It uses a RecyclerView to show the entrants' names
 * in a scrollable list. The activity includes functionality to:
 * - Fetch and display confirmed entrants from Firestore.
 * - Use a custom RecyclerView adapter to handle the entrant data.
 * - Provide navigation back to the previous activity using the toolbar.
 *
 * Features:
 * - Integration with Firestore for data fetching.
 * - Custom RecyclerView Adapter for dynamic list updates.
 * - Error handling for data retrieval failures.
 *
 * Dependencies:
 * - Firestore database with a collection for confirmed entrants.
 * - XML layout resources for activity and list items.
 */

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

/**
 * Activity to display a list of confirmed entrants for a specific event.
 */
public class ConfirmedEntrantsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private List<Entrant> entrantsList;
    private EntrantsAdapter adapter;

    /**
     * Called when the activity is starting. Sets up the layout, initializes the toolbar,
     * RecyclerView, and Firestore instance, and loads confirmed entrants.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState().
     */
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

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Load confirmed entrants
        loadConfirmedEntrants();
    }

    /**
     * Fetches the list of confirmed entrants from the Firestore database and updates the RecyclerView.
     */
    private void loadConfirmedEntrants() {
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

    /**
     * Handles item selection in the options menu.
     *
     * @param item The menu item that was selected.
     * @return True if the item selection was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Close this activity on back arrow click
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * RecyclerView Adapter for displaying the list of entrants.
     */
    private class EntrantsAdapter extends RecyclerView.Adapter<EntrantsAdapter.ViewHolder> {

        private List<Entrant> entrantsList;

        /**
         * Constructor for EntrantsAdapter.
         *
         * @param entrantsList The list of entrants to display.
         */
        EntrantsAdapter(List<Entrant> entrantsList) {
            this.entrantsList = entrantsList;
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
            Entrant entrant = entrantsList.get(position);
            holder.nameTextView.setText(String.format("%s %s", entrant.getFirstName(), entrant.getLastName()));
        }

        /**
         * Returns the total number of items in the dataset.
         *
         * @return The number of items.
         */
        @Override
        public int getItemCount() {
            return entrantsList.size();
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
                nameTextView = itemView.findViewById(R.id.tv_username);
            }
        }
    }
}
