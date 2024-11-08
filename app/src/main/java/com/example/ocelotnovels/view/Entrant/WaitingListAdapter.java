package com.example.ocelotnovels.view.Entrant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Event;
import com.example.ocelotnovels.utils.FirebaseUtils;

import java.util.List;

public class WaitingListAdapter extends RecyclerView.Adapter<WaitingListAdapter.ViewHolder> {
    private List<Event> eventList;
    private Context context;
    private FirebaseUtils firebaseUtils;

    public WaitingListAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText(event.getEventDate().toString());
        holder.eventDescription.setText("Spots available: " + event.getWaitListOpenSpots());

        holder.deleteButton.setOnClickListener(v -> {
            // Logic to leave the waiting list for the event
            String currentUserId = firebaseUtils.getDeviceId(v.getContext());
            event.removeEntrantFromWaitList(currentUserId); // have to remove from firebase too
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventDescription;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_date);
            eventDescription = itemView.findViewById(R.id.event_description);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
