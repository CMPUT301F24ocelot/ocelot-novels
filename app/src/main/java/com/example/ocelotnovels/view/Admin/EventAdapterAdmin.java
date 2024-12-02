package com.example.ocelotnovels.view.Admin;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Event;
import com.example.ocelotnovels.model.User;

import java.util.ArrayList;

/**
 * This is the class that is used to display the events in the admin browser
 */
public class EventAdapterAdmin extends ArrayAdapter<Event> {

    /**
     * This will create the view for the listView in the admin browser
     * @param context
     * @param events
     */
    public EventAdapterAdmin(@NonNull Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_list_admin,parent
                    ,false);
        }else{
            view = convertView;
        }
        Event event = getItem(position);
        if(event != null){
            TextView name = view.findViewById(R.id.event_name);
            name.setText(event.getEventName());
        }
        Button detailsButton = view.findViewById(R.id.btn_details);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toProfile = new Intent(EventAdapterAdmin.this.getContext(), EventDetailsAdminView.class);
                toProfile.putExtra("Event", event);
                startActivity(EventAdapterAdmin.this.getContext(),toProfile,null);
            }
        });
        return view;
    }
}
