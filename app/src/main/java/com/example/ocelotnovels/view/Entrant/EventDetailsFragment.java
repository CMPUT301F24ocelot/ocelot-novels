package com.example.ocelotnovels.view.Entrant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.SignUpActivity;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EventDetailsFragment extends DialogFragment {

    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;
    private FirebaseUtils firebaseUtils;
    private DocumentReference userDocument;
    private DocumentReference eventDocument;

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
                        String title = documentSnapshot.getString("Name");
                        String description = documentSnapshot.getString("Description");
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

    private final ActivityResultLauncher<Intent> signUpLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Sign-up was successful, check user and join event
                    joinEvent();
                } else {
                    Toast.makeText(getContext(), "Sign-up required to join the event", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void joinEvent() {
        userDocument = firebaseUtils.getUserDocument();
        eventDocument = firebaseUtils.getDb().collection("events").document(eventId);

        userDocument.get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists() && userDoc.contains("email")) {
                        // User exists, proceed to check event capacity
                        String deviceId = firebaseUtils.getDeviceId(getContext());
                        checkEventCapacityAndJoin(eventId, deviceId);
                    } else {
                        Toast.makeText(getContext(), "User not found in database. Please sign up.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getContext(), SignUpActivity.class);
                        signUpLauncher.launch(intent);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error checking user in database", Toast.LENGTH_SHORT).show();
                    Log.e("EventDetailsFragment", "Failed to check user", e);
                });

        // Implement joining the event functionality here
        Toast.makeText(getContext(), "Joining event: " + eventId, Toast.LENGTH_SHORT).show();
    }

    private void checkEventCapacityAndJoin(String eventId, String deviceId) {
        eventDocument.get()
                .addOnSuccessListener(eventDoc -> {
                    if (eventDoc.exists()) {
                        int eventCapacity = eventDoc.getLong("eventCapacity").intValue();
                        List<String> waitingList = (List<String>) eventDoc.get("waitingList");

                        if (waitingList == null) {
                            waitingList = new ArrayList<>();
                        }

                        // Check if there's enough capacity
                        if (waitingList.size() < eventCapacity) {
                            // Capacity is available, proceed to add user to event
                            addEventToUser(eventId, deviceId);
                        } else {
                            // Capacity full
                            Toast.makeText(getContext(), "Event is already at full capacity", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to check event capacity", Toast.LENGTH_SHORT).show();
                    Log.e("EventDetailsFragment", "Error fetching event document", e);
                });
    }


    private void addEventToUser(String eventId, String deviceId) {
        // Step 2: Add the eventId to the user's eventsJoined array
        userDocument.update("eventsJoined", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> {
                    // Successfully added the event to the user's joined list
                    addUserToEventWaitingList(eventId, deviceId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to join event", Toast.LENGTH_SHORT).show();
                    Log.e("EventDetailsFragment", "Error updating user document", e);
                });
    }

    private void addUserToEventWaitingList(String eventId, String deviceId) {
        eventDocument.update("waitingList", FieldValue.arrayUnion(deviceId))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Successfully joined the event", Toast.LENGTH_SHORT).show();

                    // Redirect to WaitingListActivity after joining
                    Intent intent = new Intent(getContext(), WaitingListActivity.class);
                    intent.putExtra("eventId", eventId);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to add to event waiting list", Toast.LENGTH_SHORT).show();
                    Log.e("EventDetailsFragment", "Error updating event document", e);
                });
    }


}
