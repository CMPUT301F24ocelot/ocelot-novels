/**
 * The WaitingListActivity class displays a list of entrants who are in the waiting list for a specific event.
 * The data is fetched from a Firestore collection and displayed in a RecyclerView.
 *
 * Features:
 * - Fetches the waiting list data from Firestore.
 * - Populates a RecyclerView with the data.
 * - Handles success and error cases for Firestore data fetching.
 * - Implements a custom RecyclerView.Adapter for displaying entrants.
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
 * The WaitingListActivity class displays a list of entrants in the waiting list for an event.
 * It fetches data from Firestore and populates a RecyclerView with the list of entrants.
 */
public class WaitingListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private List<Entrant> entrantsList;

    /**
     * Called when the activity is first created.
     * Initializes the RecyclerView, Firestore instance, and loads the waiting list data.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state.
     */
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

    public void setMockData(List<Entrant> mockData) {
        entrantsList.clear();
        entrantsList.addAll(mockData);
        updateRecyclerView();
    }

    /**
     * Fetches the list of entrants from the "WaitingList" collection in Firestore.
     * On success, it populates the entrants list and updates the RecyclerView.
     * Displays an error message if the operation fails.
     */
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

    /**
     * Updates the RecyclerView with the list of entrants.
     */
    private void updateRecyclerView() {
        EntrantsAdapter adapter = new EntrantsAdapter(entrantsList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Adapter class for managing the RecyclerView's data and views.
     */
    private class EntrantsAdapter extends RecyclerView.Adapter<EntrantsAdapter.ViewHolder> {

        private List<Entrant> entrantsList;

        /**
         * Constructs an EntrantsAdapter with the specified list of entrants.
         *
         * @param entrantsList The list of entrants to display.
         */
        EntrantsAdapter(List<Entrant> entrantsList) {
            this.entrantsList = entrantsList;
        }

        /**
         * Creates a new ViewHolder to represent an item in the RecyclerView.
         *
         * @param parent   The parent ViewGroup.
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
         * Binds data to the specified ViewHolder.
         *
         * @param holder   The ViewHolder to bind data to.
         * @param position The position of the data item in the list.
         */
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Entrant entrant = entrantsList.get(position);
            holder.nameTextView.setText(String.format("%s %s", entrant.getFirstName(), entrant.getLastName()));
        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total item count.
         */
        @Override
        public int getItemCount() {
            return entrantsList.size();
        }

        /**
         * ViewHolder class for caching views associated with a single RecyclerView item.
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;

            /**
             * Constructs a ViewHolder and initializes the views.
             *
             * @param itemView The view representing the item.
             */
            ViewHolder(View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.tv_username);
            }
        }
    }
}
