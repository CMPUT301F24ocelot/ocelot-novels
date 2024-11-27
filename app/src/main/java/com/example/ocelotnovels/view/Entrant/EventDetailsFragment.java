package com.example.ocelotnovels.view.Entrant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ocelotnovels.MainActivity;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.view.Entrant.WaitingListActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventDetailsFragment extends DialogFragment {
    private static final String ARG_EVENT_ID = "eventId";
    private static final String ARG_USER_ID = "userId";

    private String eventId;
    private String userId;
    private DocumentReference userDocument;
    private DocumentReference eventDocument;

    private TextView eventTitle;
    private TextView eventDescription;
    private TextView eventStatus;
    private TextView registrationDeadline;

    private TextView geolocationWarning;

    public static EventDetailsFragment newInstance(String eventId, String userId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate custom layout
        View view = LayoutInflater.from(getContext()).inflate(R.layout.user_scan_qr_event_details, null);

        // Initialize UI elements
        eventTitle = view.findViewById(R.id.user_event_title);
        eventDescription = view.findViewById(R.id.user_event_description);
        eventStatus = view.findViewById(R.id.user_event_status);
        registrationDeadline = view.findViewById(R.id.user_event_deadline);
        geolocationWarning = view.findViewById(R.id.warning_text);
        // Get event and user IDs from arguments
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            userId = getArguments().getString(ARG_USER_ID);
            loadEventDetails();
        }

        return builder
                .setView(view)
                .setTitle("Join Event")
                .setPositiveButton("Join", null) // We'll override this later
                .setNegativeButton("Cancel", (dialog, which) -> dismiss())
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                verifyUserAndJoinEvent();
            });
        }
    }

    private void loadEventDetails() {
        FirebaseFirestore.getInstance()
                .collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
//                        eventTitle.setText(document.getString("title"));
//                        eventDescription.setText(document.getString("description"));
//                        //eventCapacity.setText("Capacity: " + document.getLong("eventCapacity"));
//
//                        Timestamp deadline = document.getTimestamp("registrationDeadline");
//                        if (deadline != null) {
//                            registrationDeadline.setText("Deadline: " + deadline.toDate().toString());
//                        }
                        String title = document.getString("name");
                        String description = document.getString("description");
                        String status = document.getString("status");
                        Boolean geolocationEnabled = document.getBoolean("geolocationEnabled");
                        // Check if registrationClose is null
                        String registrationCloseTimestamp = document.getString("regClosed");
                        String deadline = (registrationCloseTimestamp != null) ? registrationCloseTimestamp.toString() : "No deadline set";

                        eventTitle.setText("Event Title: " + title);
                        eventDescription.setText("Event Description: " + description);
                        eventStatus.setText("Event Status: " + status);
                        registrationDeadline.setText("Event Deadline: " + deadline);
                        //Log.i("deviceId",deviceId);
                        if (geolocationEnabled){
                            geolocationWarning.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load event details", Toast.LENGTH_SHORT).show();
                    Log.e("JoinEventFragment", "Error loading event details", e);
                });
    }

    private void verifyUserAndJoinEvent() {
        if (!isAdded()) return;

        userDocument = FirebaseFirestore.getInstance().collection("users").document(userId);
        eventDocument = FirebaseFirestore.getInstance().collection("events").document(eventId);

        userDocument.get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists() && userDoc.contains("email")) {
                        // User has email, proceed with joining
                        checkEventCapacityAndJoin();
                    } else {
                        // No email found, redirect to MainActivity
                        handleNoUser();
                    }
                })
                .addOnFailureListener(e -> {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(),
                                "Error verifying user: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    Log.e("JoinEventFragment", "Error verifying user", e);
                });
    }

    private void checkEventCapacityAndJoin() {
        eventDocument.get()
                .addOnSuccessListener(eventDoc -> {
                    if (!eventDoc.exists()) {
                        Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Long capacityLong = (eventDoc.get("capacity") == null ? -1 : (long) eventDoc.get("capacity"));
//                    if (capacityLong == null) {
//                        Toast.makeText(getContext(), "Invalid event capacity", Toast.LENGTH_SHORT).show();
//                        return;
//                    }

                    List<String> waitingList = (List<String>) eventDoc.get("waitingList");
                    waitingList = waitingList != null ? waitingList : new ArrayList<>();

                    if (waitingList.contains(userId)) {
                        Toast.makeText(getContext(), "Already registered for this event", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (capacityLong >= 0 && waitingList.size() < capacityLong || capacityLong == -1) {
                        addUserToEvent();
                    } else {
                        Toast.makeText(getContext(), "Event is at full capacity", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error checking capacity: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("JoinEventFragment", "Error checking capacity", e);
                });
    }

    private void addUserToEvent() {
        // Add event to user's joined events
        userDocument.update("eventsJoined", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> {
                    // Add user to event's waiting list
                    eventDocument.update("waitingList", FieldValue.arrayUnion(userId))
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(getContext(), "Successfully joined event", Toast.LENGTH_SHORT).show();
                                navigateToWaitingList();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to update event", Toast.LENGTH_SHORT).show();
                                Log.e("JoinEventFragment", "Error updating event", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update user", Toast.LENGTH_SHORT).show();
                    Log.e("JoinEventFragment", "Error updating user", e);
                });
    }

    private void handleNoUser() {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), "Please sign up first", Toast.LENGTH_SHORT).show();

            // Navigate back to MainActivity
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void navigateToWaitingList() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), WaitingListActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
            getActivity().finish();
        }
    }
}