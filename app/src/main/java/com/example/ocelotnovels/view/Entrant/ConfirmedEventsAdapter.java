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

public class ConfirmedEventsAdapter extends RecyclerView.Adapter<ConfirmedEventsAdapter.ConfirmedEventViewHolder> {

    private Context context;
    private List<Event> confirmedEvents;

    public ConfirmedEventsAdapter(Context context, List<Event> confirmedEvents) {
        this.context = context;
        this.confirmedEvents = confirmedEvents;
    }

    @NonNull
    @Override
    public ConfirmedEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_confirmed_event, parent, false);
        return new ConfirmedEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmedEventViewHolder holder, int position) {
        Event event = confirmedEvents.get(position);

        holder.eventNameTextView.setText(event.getEventName());
        holder.eventDescriptionTextView.setText(event.getEventDescription());
        holder.eventLocationTextView.setText(event.getEventLocation());
        holder.eventDateTextView.setText(event.getRegistrationClose());
    }

    @Override
    public int getItemCount() {
        return confirmedEvents.size();
    }

    public static class ConfirmedEventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView eventDescriptionTextView;
        TextView eventLocationTextView;
        TextView eventDateTextView;

        public ConfirmedEventViewHolder(@NonNull View itemView) {
            super(itemView);

            eventNameTextView = itemView.findViewById(R.id.event_name);
            eventDescriptionTextView = itemView.findViewById(R.id.event_description);
            eventLocationTextView = itemView.findViewById(R.id.event_location);
            eventDateTextView = itemView.findViewById(R.id.event_date);
        }
    }
}
