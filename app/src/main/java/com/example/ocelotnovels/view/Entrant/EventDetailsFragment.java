/**
 * This class represents the EventDetailsFragment, a dialog fragment that displays the details of an event
 * and provides functionality for a user to join the event. It fetches event details from Firebase Firestore
 * and verifies user information before allowing them to register for the event. If geolocation is enabled
 * for the event, it ensures location data is collected. The fragment also handles updating user and event
 * details in Firestore, such as adding the user to the event's waiting list and updating their event location.
 */

package com.example.ocelotnovels.view.Entrant;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.ocelotnovels.MainActivity;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.view.Entrant.WaitingListActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

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
    private ImageView eventImage;

    private TextView geolocationWarning;

    private GoogleMap mMap;

    /**
     * Creates a new instance of EventDetailsFragment with the specified event and user IDs.
     *
     * @param eventId the ID of the event to display details for.
     * @param userId  the ID of the user interacting with the fragment.
     * @return a new instance of EventDetailsFragment.
     */
    public static EventDetailsFragment newInstance(String eventId, String userId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates the dialog for displaying event details.
     *
     * @param savedInstanceState the saved state of the fragment.
     * @return the created dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the layout
        View view = LayoutInflater.from(getContext()).inflate(R.layout.user_scan_qr_event_details, null);

        // Initialize UI elements
        eventTitle = view.findViewById(R.id.user_event_title);
        eventDescription = view.findViewById(R.id.user_event_description);
        registrationDeadline = view.findViewById(R.id.user_event_deadline);
        geolocationWarning = view.findViewById(R.id.warning_text);
        eventImage = view.findViewById(R.id.event_details_poster_image);

        // Set default visibility for geolocation warning
        geolocationWarning.setVisibility(View.GONE);

        // Get arguments
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            userId = getArguments().getString(ARG_USER_ID);

            // Load event details
            loadEventDetails();
        } else {
            Log.e("EventDetailsFragment", "Arguments not passed to fragment");
            Toast.makeText(getContext(), "Failed to load event details", Toast.LENGTH_SHORT).show();
        }

        builder.setView(view)
                .setTitle("Join Event")
                .setPositiveButton("Join", null) // Overridden in onResume
                .setNegativeButton("Cancel", (dialog, which) -> dismiss());
        return builder.create();
    }

    /**
     * Resumes the fragment and sets up the positive button's click listener to verify the user and join the event.
     */
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

    /**
     * Loads event details from Firebase Firestore and updates the UI with the retrieved data.
     */
    private void loadEventDetails() {
        FirebaseFirestore.getInstance()
                .collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // Get event details
                        String eventDetailsImage = document.getString("posterUrl");
                        String title = document.getString("name");
                        String description = document.getString("description");
//                        String status = document.getString("status");
                        Boolean geolocationEnabled = document.getBoolean("geolocationEnabled");

                        // Get registration close timestamp
                        String registrationCloseTimestamp = document.getString("regClosed");
                        String deadline = (registrationCloseTimestamp != null) ? registrationCloseTimestamp : "No deadline set";

                        // Set the TextViews with event details
                        eventTitle.setText("Event Title: " + title);
                        eventDescription.setText("Event Description: " + description);
//                        eventStatus.setText("Event Status: " + status);
                        registrationDeadline.setText("Event Deadline: " + deadline);

                        // Load event poster image using Glide
                        if (eventDetailsImage != null && !eventDetailsImage.isEmpty()) {
                            Glide.with(getContext())
                                    .load(eventDetailsImage) // Load the image URL from Firestore
                                    .placeholder(R.drawable.ic_image_placeholder) // Placeholder image while loading
                                    .error(R.drawable.ic_image_placeholder) // Error image if loading fails
                                    .into(eventImage); // Set the loaded image into the ImageView
                        }

                        // Handle geolocation setting if applicable
                        if (geolocationEnabled != null && geolocationEnabled) {
                            geolocationWarning.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load event details", Toast.LENGTH_SHORT).show();
                    Log.e("JoinEventFragment", "Error loading event details", e);
                });
    }


    /**
     * Verifies the user by checking their Firestore document and proceeds with joining the event.
     */
    private void verifyUserAndJoinEvent() {
        if (!isAdded()) return;

        userDocument = FirebaseFirestore.getInstance().collection("users").document(userId);
        eventDocument = FirebaseFirestore.getInstance().collection("events").document(eventId);

        userDocument.get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists() && userDoc.contains("email")) {
                        // User has email, proceed with joining
                        fetchUserLocationAndJoinEvent();
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

    /**
     * Fetches the user's location and attempts to join the event.
     */
    private void fetchUserLocationAndJoinEvent() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, show a message
            Toast.makeText(requireContext(), "Location permission is required to join the event.", Toast.LENGTH_SHORT).show();
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.i("EventDetailsFragment", "Location fetched: Lat = " + latitude + ", Lon = " + longitude);
                        updateUserEventLocations(latitude, longitude);
                    } else {
                        Toast.makeText(requireContext(), "Unable to fetch your location.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error fetching location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("EventDetailsFragment", "Error fetching location", e);
                });
    }

    /**
     * Updates the user's location in Firestore and attempts to join the event if the user is eligible.
     *
     * @param latitude  the latitude of the user's location.
     * @param longitude the longitude of the user's location.
     */
    private void updateUserEventLocations(double latitude, double longitude) {
        // Create a GeoPoint object to be added
        GeoPoint geoPoint = new GeoPoint(latitude, longitude);

        userDocument.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<GeoPoint> eventLocations = (List<GeoPoint>) documentSnapshot.get("eventLocations");

                if (eventLocations == null) {
                    // If the eventLocations field does not exist, initialize it as an array
                    eventLocations = new ArrayList<>();
                }

                // Add the new GeoPoint to the array
                eventLocations.add(geoPoint);

                // Update the eventLocations field in Firestore
                userDocument.update("eventLocations", eventLocations)
                        .addOnSuccessListener(aVoid -> {
                            Log.i("EventDetailsFragment", "GeoPoint added to eventLocations field");
                            checkEventCapacityAndJoin(); // Proceed with joining the event
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Failed to update your location in Firestore.", Toast.LENGTH_SHORT).show();
                            Log.e("EventDetailsFragment", "Error updating location in Firestore", e);
                        });
            } else {
                Log.w("EventDetailsFragment", "User document does not exist.");
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed to fetch user document.", Toast.LENGTH_SHORT).show();
            Log.e("EventDetailsFragment", "Error fetching user document", e);
        });
    }



    /*private void checkEventCapacityAndJoin() {
        eventDocument.get()
                .addOnSuccessListener(eventDoc -> {
                    if (!eventDoc.exists()) {
                        Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<String> waitingList = (List<String>) eventDoc.get("waitingList");
                    List<String> cancelledList = (List<String>) eventDoc.get("cancelledList");

                    // Initialize lists if they are null
                    waitingList = waitingList != null ? waitingList : new ArrayList<>();
                    cancelledList = cancelledList != null ? cancelledList : new ArrayList<>();

                    // Check if the user is already in the waiting list
                    if (waitingList.contains(userId)) {
                        Toast.makeText(getContext(), "Already registered for this event", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Add the user to the waiting list
                    addUserToEvent();

                    // Remove user from cancelled list if present
                    if (cancelledList.contains(userId)) {
                        cancelledList.remove(userId);
                        eventDocument.update("cancelledList", cancelledList)
                                .addOnFailureListener(e -> Log.e("JoinEventFragment", "Failed to update cancelled list", e));
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error checking event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("JoinEventFragment", "Error checking event", e);
                });
    }*/


    /**
     * Checks if the event has capacity and adds the user to the waiting list if possible.
     */
    private void checkEventCapacityAndJoin() {
        eventDocument.get()
                .addOnSuccessListener(eventDoc -> {
                    if (!eventDoc.exists()) {
                        Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Long capacityLong = (eventDoc.get("capacityWaitingList") == null ? -1 : (long) eventDoc.get("capacityWaitingList"));

                    List<String> waitingList = (List<String>) eventDoc.get("waitingList");
                    List<String> cancelledList = (List<String>) eventDoc.get("cancelledList");

                    // Initialize lists if they are null
                    waitingList = waitingList != null ? waitingList : new ArrayList<>();
                    cancelledList = cancelledList != null ? cancelledList : new ArrayList<>();

                    // Check if the user is already in the waiting list
                    if (waitingList.contains(userId)) {
                        Toast.makeText(getContext(), "Already registered for this event", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Check event capacity and join
                    if (capacityLong >= 0 && waitingList.size() < capacityLong || capacityLong == -1) {
                        // Add the user to the waiting list
                        addUserToEvent();

                        // Remove user from cancelled list if present
                        if (cancelledList.contains(userId)) {
                            cancelledList.remove(userId);
                            eventDocument.update("cancelledList", cancelledList)
                                    .addOnFailureListener(e -> Log.e("JoinEventFragment", "Failed to update cancelled list", e));
                        }
                    } else {
                        Toast.makeText(getContext(), "Event is at full capacity", Toast.LENGTH_SHORT).show();
                    }

//                    // Add the user to the waiting list
//                    addUserToEvent();
//
//                    // Remove user from cancelled list if present
//                    if (cancelledList.contains(userId)) {
//                        cancelledList.remove(userId);
//                        eventDocument.update("cancelledList", cancelledList)
//                                .addOnFailureListener(e -> Log.e("JoinEventFragment", "Failed to update cancelled list", e));
//                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error checking event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("JoinEventFragment", "Error checking event", e);
                });
    }


    /**
     * Adds the user to the event's waiting list and updates their joined events in Firestore.
     */
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

    /**
     * Handles cases where the user document does not exist, redirecting to the MainActivity.
     */
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

    /**
     * Navigates the user to the WaitingListActivity after successfully joining the event.
     */
    private void navigateToWaitingList() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), WaitingListActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
            getActivity().finish();
        }
    }
}