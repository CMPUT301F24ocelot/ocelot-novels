/**
 * This class implements a custom RecyclerView adapter for displaying a list of selected events.
 * Each event item provides details such as name, description, location, and date. The adapter
 * supports user interaction through accept and reject buttons, triggering callback listeners
 * for handling actions on individual events.
 */

package com.example.ocelotnovels.view.Entrant;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Event;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SelectedEventsAdapter extends RecyclerView.Adapter<SelectedEventsAdapter.SelectedEventViewHolder> {
    private List<Event> selectedEvents;
    private OnEventActionListener acceptListener;
    private OnEventActionListener rejectListener;

    // Interface for event action callbacks
    public interface OnEventActionListener {
        /**
         * Called when an action (accept or reject) is performed on an event.
         * @param event The event on which the action was performed.
         */
        void onEventAction(Event event);
    }

    /**
     * Constructs a new adapter for the list of selected events.
     *
     * @param selectedEvents List of events to display.
     * @param acceptListener Listener for the accept button actions.
     * @param rejectListener Listener for the reject button actions.
     */
    public SelectedEventsAdapter(List<Event> selectedEvents,
                                 OnEventActionListener acceptListener,
                                 OnEventActionListener rejectListener) {
        this.selectedEvents = selectedEvents;
        this.acceptListener = acceptListener;
        this.rejectListener = rejectListener;
    }

    @NonNull
    @Override
    public SelectedEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_event, parent, false);
        return new SelectedEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedEventViewHolder holder, int position) {
        Event event = selectedEvents.get(position);

        // Set event details
        holder.eventNameTextView.setText(event.getEventName());
        holder.eventDescriptionTextView.setText(event.getEventDescription());
        holder.eventLocationTextView.setText(event.getEventLocation());
        // Format the event date before setting it
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        String formattedDate = dateFormat.format(event.getEventDate());
//        Log.d("formattedDate", formattedDate);
//        holder.eventDateTextView.setText(formattedDate);
        holder.eventDateTextView.setText(event.getRegistrationClose());

        // Set accept button click listener
        holder.acceptButton.setOnClickListener(v -> {
            if (acceptListener != null) {
                acceptListener.onEventAction(event);
            }
        });

        // Set reject button click listener
        holder.rejectButton.setOnClickListener(v -> {
            if (rejectListener != null) {
                rejectListener.onEventAction(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedEvents.size();
    }

    /**
     * ViewHolder class for managing individual event items in the RecyclerView.
     */
    static class SelectedEventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView eventDescriptionTextView;
        TextView eventLocationTextView;
        TextView eventDateTextView;
        Button acceptButton;
        Button rejectButton;

        /**
         * Constructs a ViewHolder for an event item.
         *
         * @param itemView The view representing a single event item.
         */
        public SelectedEventViewHolder(@NonNull View itemView) {
            super(itemView);

            eventNameTextView = itemView.findViewById(R.id.event_name);
            eventDescriptionTextView = itemView.findViewById(R.id.event_description);
            eventLocationTextView = itemView.findViewById(R.id.event_location);
            eventDateTextView = itemView.findViewById(R.id.event_date);
            acceptButton = itemView.findViewById(R.id.accept_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
        }
    }
}