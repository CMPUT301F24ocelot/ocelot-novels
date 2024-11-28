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

public class OrganizerEventAdapter extends RecyclerView.Adapter<OrganizerEventAdapter.EventViewHolder> {

    private List<Map<String, String>> eventDetails; // Updated list to hold event details
    private Context context;

    public OrganizerEventAdapter(List<Map<String, String>> eventDetails, Context context) {
        this.eventDetails = eventDetails;
        this.context = context;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.organizer_event_item, parent, false);
        return new EventViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return eventDetails.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;
        TextView eventDateTextView;
        TextView eventLocationTextView;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            eventDateTextView = itemView.findViewById(R.id.eventDateTextView);
            eventLocationTextView = itemView.findViewById(R.id.eventLocationTextView);
        }
    }
}