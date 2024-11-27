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

public class OrganizerEventAdapter extends RecyclerView.Adapter<OrganizerEventAdapter.EventViewHolder> {

    public List<String> eventNames;
    public Context context;

    public OrganizerEventAdapter(List<String> eventNames, Context context) {
        this.eventNames = eventNames;
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
        String eventName = eventNames.get(position);
        holder.eventNameTextView.setText(eventName);
        // Set a click listener on the item
        holder.itemView.setOnClickListener(v -> {
            // Open EventDetailsActivity when the item is clicked
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("eventName", eventName); // Pass the event name to the next activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventNames.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView;

        public EventViewHolder(View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
        }
    }
}