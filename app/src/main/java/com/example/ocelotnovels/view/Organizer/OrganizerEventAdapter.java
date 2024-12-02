package com.example.ocelotnovels.view.Organizer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.R;

import java.util.List;
import java.util.Map;

/**
 * This adapter class is used to display a list of events for an organizer in a RecyclerView.
 * Each event includes details such as name, date, and location, and clicking on an event item
 * navigates to the EventDetailsActivity, passing the event name as an extra.
 */
public class OrganizerEventAdapter extends RecyclerView.Adapter<OrganizerEventAdapter.EventViewHolder> {

    private List<Map<String, String>> eventDetails; // Updated list to hold event details
    private Context context;

    /**
     * Constructor for the OrganizerEventAdapter.
     *
     * @param eventDetails A list of maps, where each map contains details about an event.
     *                     Each map should have keys like "name", "date", and "location".
     * @param context      The context in which the adapter is being used.
     */
    public OrganizerEventAdapter(List<Map<String, String>> eventDetails, Context context) {
        this.eventDetails = eventDetails;
        this.context = context;
    }

    /**
     * Called when a new ViewHolder is created to represent an event item.
     * Inflates the item layout and creates a ViewHolder.
     *
     * @param parent   The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new view.
     * @return A new instance of EventViewHolder.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.organizer_event_item, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds event details to the specified ViewHolder.
     * Updates the TextViews in the ViewHolder with the event's name, date, and location.
     * Sets an OnClickListener to navigate to EventDetailsActivity when the item is clicked.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the event in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Map<String, String> event = eventDetails.get(position);
        holder.eventNameTextView.setText(event.get("name"));
        holder.eventDateTextView.setText("Date: " + event.get("date"));
        holder.eventLocationTextView.setText("Location: " + event.get("location"));

        // Set a click listener on the item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("eventName", event.get("name"));
            context.startActivity(intent);
        });
    }

    /**
     * Returns the total number of events in the list.
     *
     * @return The size of the eventDetails list.
     */
    @Override
    public int getItemCount() {
        return eventDetails.size();
    }

    /**
     * ViewHolder class for organizing event items in the RecyclerView.
     * Holds references to the TextViews for displaying event details.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView eventDateTextView;
        TextView eventLocationTextView;

        /**
         * Constructor for the EventViewHolder.
         * Initializes the TextViews with their corresponding views in the layout.
         *
         * @param itemView The root view of the event item layout.
         */
        public EventViewHolder(View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            eventDateTextView = itemView.findViewById(R.id.eventDateTextView);
            eventLocationTextView = itemView.findViewById(R.id.eventLocationTextView);
        }
    }
}