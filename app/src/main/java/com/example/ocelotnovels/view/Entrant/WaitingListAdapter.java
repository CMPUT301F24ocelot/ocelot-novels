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

public class WaitingListAdapter extends RecyclerView.Adapter<WaitingListAdapter.ViewHolder> {
    private final List<Event> eventList;
    private final OnEventActionListener listener;

    public interface OnEventActionListener {
        void onLeaveEvent(Event event);
    }

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

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventNameText;
        private final TextView eventDescriptionText;
        private final TextView eventLocationText;
        private final TextView registrationCloseText;
        private final Button leaveButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameText = itemView.findViewById(R.id.event_name);
            eventDescriptionText = itemView.findViewById(R.id.event_description);
            eventLocationText = itemView.findViewById(R.id.event_location);
            registrationCloseText = itemView.findViewById(R.id.registration_close);
            leaveButton = itemView.findViewById(R.id.leave_button);
        }

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