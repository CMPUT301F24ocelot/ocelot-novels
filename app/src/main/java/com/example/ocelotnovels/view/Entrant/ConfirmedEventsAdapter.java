/**
 * This adapter class is used for managing and displaying a list of confirmed events
 * in a RecyclerView. Each event contains details such as its name, description, location,
 * and registration closing date. The adapter binds the event data to the corresponding
 * views in the RecyclerView, ensuring an efficient and reusable implementation of the
 * event list UI.
 */

package com.example.ocelotnovels.view.Entrant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Event;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter class for displaying a list of confirmed events in a RecyclerView.
 */
public class ConfirmedEventsAdapter extends RecyclerView.Adapter<ConfirmedEventsAdapter.ConfirmedEventViewHolder> {

    private Context context;
    private List<Event> confirmedEvents;

    /**
     * Constructor for the ConfirmedEventsAdapter.
     *
     * @param context         The context in which the adapter is used.
     * @param confirmedEvents The list of confirmed events to be displayed.
     */
    public ConfirmedEventsAdapter(Context context, List<Event> confirmedEvents) {
        this.context = context;
        this.confirmedEvents = confirmedEvents;
    }

    /**
     * Inflates the layout for each item in the RecyclerView.
     *
     * @param parent   The parent ViewGroup into which the new view will be added.
     * @param viewType The view type of the new View.
     * @return A new ConfirmedEventViewHolder for the inflated view.
     */
    @NonNull
    @Override
    public ConfirmedEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_confirmed_event, parent, false);
        return new ConfirmedEventViewHolder(view);
    }

    /**
     * Binds the event data to the corresponding views in the ViewHolder.
     *
     * @param holder   The ViewHolder which holds the views for an event.
     * @param position The position of the event in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull ConfirmedEventViewHolder holder, int position) {
        Event event = confirmedEvents.get(position);

        holder.eventNameTextView.setText(event.getEventName());
        holder.eventDescriptionTextView.setText(event.getEventDescription());
        holder.eventLocationTextView.setText(event.getEventLocation());
        holder.eventDateTextView.setText(event.getRegistrationClose());
    }

    /**
     * Returns the total number of events in the list.
     *
     * @return The size of the confirmed events list.
     */
    @Override
    public int getItemCount() {
        return confirmedEvents.size();
    }

    /**
     * ViewHolder class for managing individual event views.
     */
    public static class ConfirmedEventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView eventDescriptionTextView;
        TextView eventLocationTextView;
        TextView eventDateTextView;

        /**
         * Constructor for the ConfirmedEventViewHolder.
         *
         * @param itemView The view representing a single event item.
         */
        public ConfirmedEventViewHolder(@NonNull View itemView) {
            super(itemView);

            eventNameTextView = itemView.findViewById(R.id.event_name);
            eventDescriptionTextView = itemView.findViewById(R.id.event_description);
            eventLocationTextView = itemView.findViewById(R.id.event_location);
            eventDateTextView = itemView.findViewById(R.id.event_date);
        }
    }
}
