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

public class EventArrayAdapter extends RecyclerView.Adapter<EventArrayAdapter.ViewHolder> {

    private final List<Event> eventList;

    public EventArrayAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getEventName());

        // Format the date to a string
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String formattedDate = dateFormat.format(event.getEventDate());
        holder.eventDate.setText(formattedDate);  // Set the formatted date string

//        holder.eventDescription.setText(event.getEventDescription());

        // Set up the button click for leaving the waiting list
        holder.leaveButton.setOnClickListener(v -> {
            // Handle leave waiting list logic here
            removeEventFromWaitingList(event);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventDescription;
        Button leaveButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_date);
            eventDescription = itemView.findViewById(R.id.event_description);
            leaveButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    private void removeEventFromWaitingList(Event event) {
        // Logic to remove the event from the waiting list in Firestore
        // Update Firestore and notify the adapter if needed
    }
}
