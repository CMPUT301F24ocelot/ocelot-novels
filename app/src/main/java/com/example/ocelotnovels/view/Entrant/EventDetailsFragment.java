package com.example.ocelotnovels.view.Entrant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailsFragment extends DialogFragment {

    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;
    private FirebaseUtils firebaseUtils;

    private TextView eventTitle, eventDescription, eventStatus, eventDeadline;

    public static EventDetailsFragment newInstance(String eventId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.user_scan_qr_event_details, null);

        // Initialize FirebaseUtils
        firebaseUtils = new FirebaseUtils(getContext());

        // Initialize UI elements
        eventTitle = dialogView.findViewById(R.id.user_event_title);
        eventDescription = dialogView.findViewById(R.id.user_event_description);
        eventStatus = dialogView.findViewById(R.id.user_event_status);
        eventDeadline = dialogView.findViewById(R.id.user_event_deadline);

        // Load event details if eventId is available
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            loadEventDetails(eventId);
        }

        // Set up the dialog
        builder.setView(dialogView)
                .setTitle("Event Details")
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        joinEvent();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    private void loadEventDetails(String eventId) {
        FirebaseFirestore.getInstance().collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("eventName");
                        String description = documentSnapshot.getString("eventDescription");
                        String status = documentSnapshot.getString("status");
                        String deadline = documentSnapshot.getTimestamp("registrationClose").toDate().toString();

                        eventTitle.setText("Event Title: " + title);
                        eventDescription.setText("Event Description: " + description);
                        eventStatus.setText("Event Status: " + status);
                        eventDeadline.setText("Event Deadline: " + deadline);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load event details", Toast.LENGTH_SHORT).show();
                    eventTitle.setText("Failed to load event details");
                });
    }

    private void joinEvent() {

        // Implement joining the event functionality here
        Toast.makeText(getContext(), "Joining event: " + eventId, Toast.LENGTH_SHORT).show();
    }
}
