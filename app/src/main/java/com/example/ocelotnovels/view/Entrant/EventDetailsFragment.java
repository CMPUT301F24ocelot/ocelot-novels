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

import com.example.ocelotnovels.MainActivity;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.SignUpActivity;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EventDetailsFragment extends DialogFragment {

    private static final String ARG_EVENT_ID = "eventId";
    private static final String ARG_DEVICE_ID = "deviceId";
    private String eventId,deviceId;
    private FirebaseUtils firebaseUtils;
    private DocumentReference userDocument;
    private DocumentReference eventDocument;

    private TextView eventTitle, eventDescription, eventStatus, eventDeadline;

    public static EventDetailsFragment newInstance(String eventId,String deviceId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        args.putString(ARG_DEVICE_ID,deviceId);
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


        // Initialize UI elements
        eventTitle = dialogView.findViewById(R.id.user_event_title);
        eventDescription = dialogView.findViewById(R.id.user_event_description);
        eventStatus = dialogView.findViewById(R.id.user_event_status);
        eventDeadline = dialogView.findViewById(R.id.user_event_deadline);

        // Load event details if eventId is available
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            deviceId = getArguments().getString(ARG_DEVICE_ID);
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

                        // Check if registrationClose is null
                        Timestamp registrationCloseTimestamp = documentSnapshot.getTimestamp("regClosed");
                        String deadline = (registrationCloseTimestamp != null) ? registrationCloseTimestamp.toDate().toString() : "No deadline set";

                        eventTitle.setText("Event Title: " + title);
                        eventDescription.setText("Event Description: " + description);
                        eventStatus.setText("Event Status: " + status);
                        eventDeadline.setText("Event Deadline: " + deadline);
                        Log.i("deviceId",deviceId);
                    }
                })
                .addOnFailureListener(e -> {
                    //Toast.makeText(, "Failed to load event details", Toast.LENGTH_SHORT).show();
                    eventTitle.setText("Failed to load event details");
                });
    }

    /*
    private final ActivityResultLauncher<Intent> signUpLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Sign-up was successful, check user and join event
                    joinEvent();
                } else {

                    //Toast.makeText(requireContext(), "Sign-up required to join the event", Toast.LENGTH_SHORT).show();
                }
            }
    );*/

    /*
    private void joinEvent() {

        userDocument = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        eventDocument = FirebaseFirestore.getInstance().collection("events").document(eventId);

        userDocument.get()
                .addOnSuccessListener(userDoc -> {

                    if (userDoc.exists() && userDoc.contains("email")) {
                        //Toast.makeText(requireContext(), "Joining event..", Toast.LENGTH_SHORT).show();
                        // User exists, proceed to check event capacity
                        //String deviceId = firebaseUtils.getDeviceId(requireContext());
                        Log.i("no joinEvent2","101");
                        checkEventCapacityAndJoin(eventId, deviceId);
                    } else {
                        //Toast.makeText(requireContext(), "User not found in database. Please sign up.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(requireContext(), SignUpActivity.class);
                        Log.i("no joinEvent","101");
                        signUpLauncher.launch(intent);
                    }
                })
                .addOnFailureListener(e -> {
                    //Toast.makeText(requireContext(), "Error checking user in database", Toast.LENGTH_SHORT).show();
                    Log.e("EventDetailsFragment", "Failed to check user", e);
                });
    }*/

    private void joinEvent() {
        // Get references
        userDocument = FirebaseFirestore.getInstance().collection("users").document(deviceId);
        eventDocument = FirebaseFirestore.getInstance().collection("events").document(eventId);

        // Check if fragment is attached to avoid null context
        if (!isAdded()) {
            Log.e("EventDetailsFragment", "Fragment not attached to context");
            return;
        }

        userDocument.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("email")) {
                String userEmail = documentSnapshot.getString("email");
                if (userEmail != null && !userEmail.isEmpty()) {
                    // User has email, proceed with joining event
                    checkEventCapacityAndJoin(eventId, deviceId);
                } else {
                    handleNoUser();
                }
            } else {
                handleNoUser();
            }
        }).addOnFailureListener(e -> {
            if (getActivity() != null) {
                Toast.makeText(getActivity(),
                        "Error checking user details: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
            Log.e("EventDetailsFragment", "Error checking user details", e);
        });
    }

    private void handleNoUser() {
        if (getActivity() != null) {
            Toast.makeText(getActivity(),
                    "Please sign up first",
                    Toast.LENGTH_SHORT).show();

            // Navigate back to MainActivity
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void checkEventCapacityAndJoin(String eventId, String deviceId) {
        if (!isAdded()) return;

        eventDocument.get()
                .addOnSuccessListener(eventDoc -> {
                    if (eventDoc.exists()) {
                        Long capacityLong = eventDoc.getLong("eventCapacity");
                        if (capacityLong == null) {
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(),
                                        "Error: Event capacity not set",
                                        Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }

                        int eventCapacity = capacityLong.intValue();
                        List<String> waitingList = (List<String>) eventDoc.get("waitingList");
                        waitingList = waitingList != null ? waitingList : new ArrayList<>();

                        if (waitingList.contains(deviceId)) {
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(),
                                        "You have already joined this event",
                                        Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }

                        if (waitingList.size() < eventCapacity) {
                            addEventToUser(eventId, deviceId);
                        } else {
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(),
                                        "Event is at full capacity",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(),
                                    "Event not found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(),
                                "Error checking event capacity: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    Log.e("EventDetailsFragment", "Error checking event capacity", e);
                });
    }

    private void addEventToUser(String eventId, String deviceId) {
        if (!isAdded()) return;

        userDocument.update("eventsJoined", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> {
                    addUserToEventWaitingList(eventId, deviceId);
                })
                .addOnFailureListener(e -> {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(),
                                "Failed to join event: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    Log.e("EventDetailsFragment", "Error updating user document", e);
                });
    }

    private void addUserToEventWaitingList(String eventId, String deviceId) {
        if (!isAdded()) return;

        eventDocument.update("waitingList", FieldValue.arrayUnion(deviceId))
                .addOnSuccessListener(aVoid -> {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(),
                                "Successfully joined the event",
                                Toast.LENGTH_SHORT).show();

                        // Navigate to WaitingListActivity
                        Intent intent = new Intent(getActivity(), WaitingListActivity.class);
                        intent.putExtra("eventId", eventId);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .addOnFailureListener(e -> {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(),
                                "Failed to add to waiting list: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    Log.e("EventDetailsFragment", "Error updating event document", e);
                });
    }


}