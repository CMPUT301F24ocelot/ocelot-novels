package com.example.ocelotnovels;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.model.Event;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * RecyclerView.Adapter for displaying a list of events in a RecyclerView.
 * Provides functionality for binding event data to UI components and handling user interactions.
 */
public class EventArrayAdapter extends RecyclerView.Adapter<EventArrayAdapter.ViewHolder> {

    private final List<Event> eventList;

    /**
     * Constructs an adapter with a list of events.
     *
     * @param eventList The list of events to be displayed.
     */
    public EventArrayAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View for each event item.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_content, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * Updates the contents of the {@link ViewHolder} to reflect the event data at the given position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the event.
     * @param position The position of the event within the data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getEventName());

        // Format the event date as a string
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String formattedDate = dateFormat.format(event.getEventDate());
        holder.eventDate.setText(formattedDate);

        // Set up button click listener for removing an event from the waiting list
        holder.leaveButton.setOnClickListener(v -> removeEventFromWaitingList(event));
    }

    /**
     * Returns the total number of events in the list.
     *
     * @return The size of the event list.
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder for holding views of a single event item.
     * Contains references to the event's name, date, description, and action button.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventDescription;
        Button leaveButton;

        /**
         * Constructs a ViewHolder for an event item.
         *
         * @param itemView The View representing the event item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_due_date);
            eventDescription = itemView.findViewById(R.id.event_description);
            leaveButton = itemView.findViewById(R.id.leave_button);
        }
    }

    /**
     * Removes an event from the waiting list.
     * Updates Firestore and notifies the adapter if needed.
     *
     * @param event The event to be removed from the waiting list.
     */
    private void removeEventFromWaitingList(Event event) {
        // Logic to remove the event from the waiting list in Firestore
        // Update Firestore and notify the adapter if needed
    }
}
