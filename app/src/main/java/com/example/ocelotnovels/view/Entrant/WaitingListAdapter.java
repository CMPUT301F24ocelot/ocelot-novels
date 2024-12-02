/**
 * WaitingListAdapter is a RecyclerView adapter that binds a list of Event objects
 * to a view representing each event in the waiting list. It displays event details
 * such as name, description, location, and registration close time. Additionally,
 * it provides a button for the user to leave an event, triggering a callback through
 * an OnEventActionListener interface.
 */

package com.example.ocelotnovels.view.Entrant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Event;

import java.util.List;

/**
 * A RecyclerView Adapter for displaying a list of events in a waiting list.
 * Each item in the list shows details about an event and provides a button to
 * leave the event.
 */
public class WaitingListAdapter extends RecyclerView.Adapter<WaitingListAdapter.ViewHolder> {
    private final List<Event> eventList;
    private final OnEventActionListener listener;

    /**
     * Listener interface for event-related actions.
     */
    public interface OnEventActionListener {
        /**
         * Callback for when a user chooses to leave an event.
         *
         * @param event The event the user wants to leave.
         */
        void onLeaveEvent(Event event);
    }

    /**
     * Constructs a WaitingListAdapter with the given list of events and an action listener.
     *
     * @param eventList The list of events to display.
     * @param listener  The listener to handle event actions.
     */
    public WaitingListAdapter(List<Event> eventList, OnEventActionListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder class representing an individual item view for an event.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventNameText;
        private final TextView eventDescriptionText;
        private final TextView eventLocationText;
        private final TextView registrationCloseText;
        private final Button leaveButton;

        /**
         * Constructs a ViewHolder for an event item.
         *
         * @param itemView The view representing the event item.
         */
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameText = itemView.findViewById(R.id.event_name);
            eventDescriptionText = itemView.findViewById(R.id.event_description);
            eventLocationText = itemView.findViewById(R.id.event_location);
            registrationCloseText = itemView.findViewById(R.id.registration_close);
            leaveButton = itemView.findViewById(R.id.leave_button);
        }

        /**
         * Binds the event data to the item view.
         *
         * @param event The event object containing the data to display.
         */
        void bind(final Event event) {
            if (event != null) {
                eventNameText.setText(event.getEventName() != null ? event.getEventName() : "");
                eventDescriptionText.setText(event.getEventDescription() != null ? event.getEventDescription() : "");
                eventLocationText.setText(event.getEventLocation() != null ?
                        "Location: " + event.getEventLocation() : "Location: Not specified");
                registrationCloseText.setText(event.getRegistrationClose() != null ?
                        "Registration closes: " + event.getRegistrationClose() : "");

                leaveButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onLeaveEvent(event);
                    }
                });
            }
        }
    }
}