package com.example.ocelotnovels.utils;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.net.ParseException;

import com.example.ocelotnovels.model.Entrant;
import com.example.ocelotnovels.model.Event;
import com.example.ocelotnovels.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.security.auth.callback.Callback;

/**
 * Utility class to manage Firebase Firestore operations for the application.
 */
public class FirebaseUtils {
    private static FirebaseUtils instance;
    private final FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef,imagesRef,defaultPics,profileRef,profilePic;
    private final String deviceId;

    /**
     * Private constructor for FirebaseUtils to initialize Firebase Firestore instance and device ID.
     *
     * @param context The application context.
     */
    public FirebaseUtils(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.deviceId = getDeviceId(context);
        this.storage = FirebaseStorage.getInstance();
        this.storageRef = storage.getReference();
        this.imagesRef=storageRef.child("images");
        this.defaultPics=imagesRef.child("default");
        this.profileRef = imagesRef.child("profilePic");
        this.profilePic = profileRef.child((this.deviceId+".jpg"));
    }

    /**
     * Provides a singleton instance of FirebaseUtils.
     *
     * @param context The application context.
     * @return Singleton instance of FirebaseUtils.
     */
    public static FirebaseUtils getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseUtils(context);
        }
        return instance;
    }

    /**
     * Returns the Firebase Firestore instance.
     *
     * @return FirebaseFirestore instance.
     */
    public FirebaseFirestore getDb() {
        return db;
    }

    /**
     * Retrieves or generates a unique device ID for the user.
     *
     * @param context The application context.
     * @return The unique device ID.
     */
    public String getDeviceId(Context context) {
        SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        String deviceId = sharedPreferences.getString("DeviceId", null);

        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("DeviceId", deviceId);
            editor.apply();
        }

        return deviceId;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public void setStorage(FirebaseStorage storage) {
        this.storage = storage;
    }

    public StorageReference getStorageRef() {
        return storageRef;
    }

    public void setStorageRef(StorageReference storageRef) {
        this.storageRef = storageRef;
    }

    public StorageReference getImagesRef() {
        return imagesRef;
    }

    public void setImagesRef(StorageReference imagesRef) {
        this.imagesRef = imagesRef;
    }

    public StorageReference getDefaultPics() {
        return defaultPics;
    }

    public void setDefaultPics(StorageReference defaultPics) {
        this.defaultPics = defaultPics;
    }

    public StorageReference getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(StorageReference profilePic) {
        this.profilePic = profilePic;
    }

    /**
     * Retrieves or generates a unique facility ID.
     *
     * @param context The application context.
     * @return The unique facility ID.
     */
    public String getFacilityId(Context context) {
        SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences("facility_settings", Context.MODE_PRIVATE);
        String facilityId = sharedPreferences.getString("FacilityId", null);

        if (facilityId == null) {
            facilityId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("FacilityId", facilityId);
            editor.apply();
        }

        return facilityId;
    }

    /**
     * Gets the user's document reference in the Firestore database.
     *
     * @return DocumentReference to the user's document.
     */
    public DocumentReference getUserDocument() {
        return this.db.collection("users").document(deviceId);
    }

    /**
     * Pushes user data to the Firestore database.
     *
     * @param context  The application context.
     * @param userData A map containing user data.
     */
    public void pushUserDocument(Context context, Map<String, Object> userData) {
        db.collection("users").document(deviceId).set(userData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    /**
     * Fetches the list of events the user has joined and populates the provided list.
     *
     * @param eventList  The list to populate with Event objects.
     * @param onComplete Runnable to execute when the fetch is complete.
     */
    public void fetchUserJoinedEvents(List<Event> eventList, Runnable onComplete) {
        getUserDocument().get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> eventsJoined = (ArrayList<String>) documentSnapshot.get("eventsJoined");
                        if (eventsJoined != null && !eventsJoined.isEmpty()) {
                            List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                            for (String eventId : eventsJoined) {
                                tasks.add(db.collection("events").document(eventId).get());
                            }
                            Tasks.whenAllComplete(tasks)
                                    .addOnSuccessListener(taskSnapshots -> {
                                        eventList.clear();
                                        for (Task<DocumentSnapshot> task : tasks) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot doc = task.getResult();
                                                if (doc.exists()) {
                                                    Event event = new Event(
                                                            doc.getId(),
                                                            doc.getString("name"),
                                                            doc.getString("description"),
                                                            doc.getString("regClosed"),
                                                            doc.getString("location")
                                                    );
                                                    event.setGeolocationEnabled(doc.getBoolean("geolocationEnabled"));
                                                    event.setWaitList((ArrayList<String>) doc.get("waitingList"));
                                                    event.setSelectedParticipants((ArrayList<String>) doc.get("selectedList"));
                                                    event.setCancelledParticipants((ArrayList<String>) doc.get("cancelledList"));
                                                    eventList.add(event);
                                                }
                                            }
                                        }
                                        if (onComplete != null) {
                                            onComplete.run();
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Error fetching events", e));
                        } else {
                            eventList.clear();
                            if (onComplete != null) {
                                onComplete.run();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching user document", e));
    }

    /**
     * Adds the user to the waitlist of a specified event.
     *
     * @param eventId   The ID of the event.
     * @param onSuccess Listener to handle success.
     * @param onFailure Listener to handle failure.
     */
    public void joinEventWaitlist(String eventId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        DocumentReference eventRef = db.collection("events").document(eventId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef);
            ArrayList<String> waitList = (ArrayList<String>) snapshot.get("waitingList");
            ArrayList<String> cancelledList = (ArrayList<String>) snapshot.get("cancelledList");

            if (waitList == null) waitList = new ArrayList<>();
            if (cancelledList == null) cancelledList = new ArrayList<>();

            cancelledList.remove(deviceId);
            if (!waitList.contains(deviceId)) {
                waitList.add(deviceId);
                transaction.update(eventRef, "waitingList", waitList);
                transaction.update(eventRef, "cancelledList", cancelledList);
            }
            return null;
        }).addOnSuccessListener(result -> {
            if (onSuccess != null) onSuccess.onSuccess(null);
        }).addOnFailureListener(e -> {
            if (onFailure != null) onFailure.onFailure(e);
        });
    }

    /**
     * Removes the user from the waitlist of a specified event and updates the cancelled list.
     *
     * @param eventId   The ID of the event.
     * @param onSuccess Listener to handle success.
     * @param onFailure Listener to handle failure.
     */
    public void leaveEventWaitlist(String eventId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        DocumentReference userRef = getUserDocument();

        db.runTransaction(transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            ArrayList<String> waitList = (ArrayList<String>) eventSnapshot.get("waitingList");
            ArrayList<String> cancelledList = (ArrayList<String>) eventSnapshot.get("cancelledList");

            DocumentSnapshot userSnapshot = transaction.get(userRef);
            ArrayList<String> eventsJoined = (ArrayList<String>) userSnapshot.get("eventsJoined");

            if (waitList != null && waitList.contains(deviceId)) {
                waitList.remove(deviceId);
                if (cancelledList == null) cancelledList = new ArrayList<>();
                if (!cancelledList.contains(deviceId)) cancelledList.add(deviceId);

                transaction.update(eventRef, "waitingList", waitList);
                transaction.update(eventRef, "cancelledList", cancelledList);
            }

            if (eventsJoined != null && eventsJoined.contains(eventId)) {
                eventsJoined.remove(eventId);
                transaction.update(userRef, "eventsJoined", eventsJoined);
            }
            return null;
        }).addOnSuccessListener(result -> {
            if (onSuccess != null) onSuccess.onSuccess(null);
        }).addOnFailureListener(e -> {
            if (onFailure != null) onFailure.onFailure(e);
        });
    }

    public void uploadProfilePictureToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        profilePic.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> profilePic.getDownloadUrl()
                        .addOnSuccessListener(uri -> updateUserProfilePicUrl(uri.toString())));
    }

    public void updateUserProfilePicUrl(String url) {
        db.collection("users").document(deviceId).update("profilePicUrl", url)
                //.addOnCompleteListener(task -> Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show())
        ;
    }

    /**
     * Performs polling for an event after registration closes.
     * Users are selected from the waiting list based on event capacity.
     *
     * @param eventId   The ID of the event for which polling is performed.
     * @param newLimit  The new capacity limit for the event.
     * @param onSuccess Callback for success.
     * @param onFailure Callback for failure.
     */
    public void performPolling(String eventId, Integer newLimit, Runnable onSuccess, OnFailureListener onFailure) {
        DocumentReference eventRef = FirebaseFirestore.getInstance().collection("events").document(eventId);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> waitingList = (List<String>) documentSnapshot.get("waitingList");
                List<String> selectedList = (List<String>) documentSnapshot.get("selectedList");
                List<String> cancelledList = (List<String>) documentSnapshot.get("cancelledList");
                Long capacity = documentSnapshot.getLong("capacity");
                String regClosed = documentSnapshot.getString("regClosed");

                // Ensure waiting list and selected list are not null
                if (waitingList == null) waitingList = new ArrayList<>();
                if (selectedList == null) selectedList = new ArrayList<>();
                if (cancelledList==null) cancelledList= new ArrayList<>();
                // Check if registration is closed
                if (!isRegistrationClosed(regClosed)) {
                    Log.w(TAG, "Registration is still open for event: " + eventId);
                    if (onFailure != null) {
                        onFailure.onFailure(new Exception("Registration is still open."));
                    }
                    return;
                }

                // Calculate the updated capacity
                int newCapacity = newLimit != null
                        ? newLimit==-2? 1:newLimit-selectedList.size() // Use the new limit if provided
                        : (capacity == null || capacity < 0 ? waitingList.size() : Math.toIntExact(capacity))- selectedList.size();

                if (newCapacity <= 0) {
                    Log.w(TAG, "No spots available for sampling.");
                    if (onSuccess != null) onSuccess.run(); // Nothing to do, so consider it successful
                    return;
                }

                // Determine the new list of selected entrants
                List<String> newSelectedList = sampleEntrants(waitingList, newCapacity);
                newSelectedList.removeAll(cancelledList);
                List<String> finalSelectedList = new ArrayList<String>(selectedList);
                finalSelectedList.addAll(newSelectedList);


                // Update Firestore with the new selected entrants
                eventRef.update("selectedList", finalSelectedList,"waitingList", FieldValue.arrayRemove(finalSelectedList.toArray()))
                        .addOnSuccessListener(aVoid -> {
                            Log.i(TAG, "Selected entrants updated for event: " + eventId);

                            // Update all users' `selectedEventsJoined` field
                            updateUserSelectedEvents(eventId, newSelectedList, onSuccess, onFailure);
                        })
                        .addOnFailureListener(onFailure);
            } else {
                Log.e(TAG, "Event not found: " + eventId);
                if (onFailure != null) onFailure.onFailure(new Exception("Event not found."));
            }
        }).addOnFailureListener(onFailure);
    }

    /**
     * Updates the `selectedEventsJoined` field for each user in the new selected list.
     *
     * @param eventId         The ID of the event being updated.
     * @param newSelectedList List of user IDs to update.
     * @param onSuccess       Callback for success.
     * @param onFailure       Callback for failure.
     */
    private void updateUserSelectedEvents(String eventId, List<String> newSelectedList, Runnable onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AtomicInteger remainingUpdates = new AtomicInteger(newSelectedList.size());
        AtomicBoolean hasFailure = new AtomicBoolean(false);

        for (String userId : newSelectedList) {
            addSelectedEvent(eventId, userId,
                    aVoid -> {
                        // Decrement the counter and check if all updates are complete
                        if (remainingUpdates.decrementAndGet() == 0 && !hasFailure.get()) {
                            if (onSuccess != null) onSuccess.run();
                        }
                    },
                    e -> {
                        // Handle failure
                        hasFailure.set(true);
                        if (onFailure != null) onFailure.onFailure(e);
                    });
        }
    }


    /**
     * Samples entrants from the waiting list up to the specified limit.
     *
     * @param waitingList The list of users in the waiting list.
     * @param limit       The maximum number of users to select.
     * @return A new list of selected entrants.
     */
    private List<String> sampleEntrants(List<String> waitingList, int limit) {
        Collections.shuffle(waitingList); // Shuffle for randomness
        return new ArrayList<>(waitingList.subList(0, Math.min(limit, waitingList.size())));
    }

    /**
     * Checks if registration is closed based on the provided timestamp.
     *
     * @param regClosed The registration close date as a string.
     * @return True if registration is closed, false otherwise.
     */
    private boolean isRegistrationClosed(String regClosed) {
        if (regClosed == null) return false; // If regClosed is not set, assume it's open
        try {
            Date closeDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(regClosed);
            return closeDate != null && new Date().after(closeDate);
        } catch (ParseException | java.text.ParseException e) {
            Log.e(TAG, "Error parsing registration close date", e);
            return false; // Default to open in case of parsing error
        }
    }

    /**
     * Selects entrants from the waiting list based on available spots.
     *
     * @param waitingList     The list of users in the waiting list.
     * @param selectedList    The list of already selected users.
     * @param spotsAvailable  The number of spots available for selection.
     * @return A list of selected entrants.
     */
    private List<String> selectEntrants(List<String> waitingList, List<String> selectedList, int spotsAvailable) {
        List<String> remainingEntrants = new ArrayList<>(waitingList);
        remainingEntrants.removeAll(selectedList); // Exclude already selected entrants

        Collections.shuffle(remainingEntrants); // Shuffle for randomness
        return remainingEntrants.subList(0, Math.min(spotsAvailable, remainingEntrants.size()));
    }

    /**
     * Updates Firestore with the selected entrants.
     *
     * @param eventId         The ID of the event.
     * @param entrantsToSelect The list of selected entrants.
     * @param onSuccess       Callback for success.
     * @param onFailure       Callback for failure.
     */
    private void updateSelectedEntrants(String eventId, List<String> entrantsToSelect, Runnable onSuccess, OnFailureListener onFailure) {
        db.collection("events").document(eventId)
                .update("selectedList", FieldValue.arrayUnion(entrantsToSelect.toArray()),
                        "waitingList", FieldValue.arrayRemove(entrantsToSelect.toArray()))
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "Selected entrants updated for event: " + eventId);
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(onFailure);
    }



    public void fetchOrganiserListEntrants(String eventId, String listType, List<User> userList, Runnable onComplete) {
        if (eventId == null || listType == null) {
            Log.e(TAG, "Event ID or list type is null");
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        db.collection("events").document(eventId).get()
                .addOnSuccessListener(eventDocumentSnapshot -> {
                    if (eventDocumentSnapshot.exists()) {
                        Object listObj = eventDocumentSnapshot.get(listType);

                        Log.d(TAG, listType + " Object: " + listObj);
                        Log.d(TAG, listType + " Object Type: " + (listObj != null ? listObj.getClass().getName() : "null"));

                        ArrayList<String> userIds;
                        if (listObj instanceof ArrayList) {
                            userIds = (ArrayList<String>) listObj;
                        } else if (listObj instanceof List) {
                            userIds = new ArrayList<>((List<String>) listObj);
                        } else {
                            Log.e(TAG, "Unexpected " + listType + " type");
                            userIds = new ArrayList<>();
                        }

                        if (userIds != null && !userIds.isEmpty()) {
                            Log.d(TAG, "Number of users in " + listType + ": " + userIds.size());

                            List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                            for (String userId : userIds) {
                                Log.d(TAG, "Fetching user ID: " + userId);
                                tasks.add(db.collection("users").document(userId).get());
                            }

                            Tasks.whenAllComplete(tasks)
                                    .addOnSuccessListener(taskSnapshots -> {
                                        userList.clear();
                                        for (Task<DocumentSnapshot> task : tasks) {
                                            try {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot doc = task.getResult();
                                                    if (doc != null && doc.exists()) {
                                                        String fullName = doc.getString("name");
                                                        Log.d(TAG, "Processing user: " + fullName);

                                                        String firstName = "";
                                                        String lastName = "";
                                                        if (fullName != null && !fullName.isEmpty()) {
                                                            String[] nameParts = fullName.split(" ", 2);
                                                            firstName = nameParts.length > 0 ? nameParts[0].trim() : "";
                                                            lastName = nameParts.length > 1 ? nameParts[1].trim() : nameParts[0].trim();
                                                        }

                                                        Entrant user = new Entrant(
                                                                firstName,
                                                                lastName,
                                                                doc.getString("email"),
                                                                doc.getId()
                                                        );
                                                        userList.add(user);
                                                    } else {
                                                        Log.w(TAG, "User document does not exist or is null");
                                                    }
                                                } else {
                                                    Log.e(TAG, "Task to fetch user failed", task.getException());
                                                }
                                            } catch (Exception e) {
                                                Log.e(TAG, "Error processing individual user", e);
                                            }
                                        }

                                        Log.d(TAG, "Total users processed: " + userList.size());

                                        if (onComplete != null) {
                                            onComplete.run();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error fetching " + listType + " users", e);
                                        if (onComplete != null) {
                                            onComplete.run();
                                        }
                                    });
                        } else {
                            Log.d(TAG, "No users in " + listType);
                            userList.clear();
                            if (onComplete != null) {
                                onComplete.run();
                            }
                        }
                    } else {
                        Log.e(TAG, "Event document does not exist");
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching event document", e);
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
    }


    /**
     * Adds the selected event to the user's `selectedEventsJoined` field in Firestore.
     * Removes the event from `eventsJoined` if it exists there.
     *
     * @param eventId   Event to be added to selected events
     * @param userId    User whose `selectedEventsJoined` has to be updated
     * @param onSuccess Success callback
     * @param onFailure Failure callback
     */
    public void addSelectedEvent(String eventId, String userId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        // Reference to the user's document in Firestore
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(userId);

        FirebaseFirestore.getInstance().runTransaction(transaction -> {
            // Fetch the user's document snapshot
            DocumentSnapshot userSnapshot = transaction.get(userRef);

            // Retrieve `selectedEventsJoined` and `eventsJoined` fields from the snapshot
            ArrayList<String> selectedEventsJoined = (ArrayList<String>) userSnapshot.get("selectedEventsJoined");
            ArrayList<String> eventJoinedByUser = (ArrayList<String>) userSnapshot.get("eventsJoined");

            // Initialize the lists if they are null
            if (selectedEventsJoined == null) {
                selectedEventsJoined = new ArrayList<>();
            }
            if (eventJoinedByUser == null) {
                eventJoinedByUser = new ArrayList<>();
            }

            // Add the event to `selectedEventsJoined` if not already present
            if (!selectedEventsJoined.contains(eventId)) {
                selectedEventsJoined.add(eventId);
                transaction.update(userRef, "selectedEventsJoined", selectedEventsJoined);

                // Remove the event from `eventsJoined` if it exists there
                if (eventJoinedByUser.contains(eventId)) {
                    eventJoinedByUser.remove(eventId);
                    transaction.update(userRef, "eventsJoined", eventJoinedByUser);
                }
            }

            return null; // Transaction return value
        }).addOnSuccessListener(result -> {
            if (onSuccess != null) onSuccess.onSuccess(null);
        }).addOnFailureListener(e -> {
            if (onFailure != null) onFailure.onFailure(e);
        });
    }


    /**
     * Handle user's response to event invitation
     * @param eventId Event being responded to
     * @param response Accept (true) or Reject (false)
     * @param onSuccess Success callback
     * @param onFailure Failure callback
     */
    public void respondToEventInvitation(String eventId, boolean response, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        DocumentReference userRef = getUserDocument();

        db.runTransaction(transaction -> {
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            DocumentSnapshot userSnapshot = transaction.get(userRef);

            // Remove from selected list
            ArrayList<String> selectedList = (ArrayList<String>) eventSnapshot.get("selectedList");

                    // Remove from user's selected events
            ArrayList<String> selectedEventsJoined = (ArrayList<String>) userSnapshot.get("selectedEventsJoined");

            if (response) {
                // User accepts - move to confirmed list
                ArrayList<String> confirmedList = (ArrayList<String>) eventSnapshot.get("confirmedList");
                if (confirmedList == null) confirmedList = new ArrayList<>();

                if (selectedList != null) selectedList.remove(deviceId);
                if (!confirmedList.contains(deviceId)) confirmedList.add(deviceId);

                transaction.update(eventRef, "selectedList", selectedList);
                transaction.update(eventRef, "confirmedList", confirmedList);

            } else {
                // User rejects - move to cancelled list
                ArrayList<String> cancelledList = (ArrayList<String>) eventSnapshot.get("cancelledList");


                if (selectedList != null) selectedList.remove(deviceId);
                if (cancelledList == null) cancelledList = new ArrayList<>();
                if (!cancelledList.contains(deviceId)) cancelledList.add(deviceId);

                // Add back to waiting list for another chance
//                if (waitingList == null) waitingList = new ArrayList<>();
//                if (!waitingList.contains(deviceId)) waitingList.add(deviceId);

                transaction.update(eventRef, "selectedList", selectedList);
                transaction.update(eventRef, "cancelledList", cancelledList);



            }

            // Remove from user's selected events
            if (selectedEventsJoined != null) selectedEventsJoined.remove(eventId);
            transaction.update(userRef, "selectedEventsJoined", selectedEventsJoined);



            return null;
        }).addOnSuccessListener(result -> {
            if (!response){
                // Perform polling to sample another user
                performPolling(eventId, -2,
                        () -> Log.i(TAG, "Another user sampled after rejection for event: " + eventId),
                        e -> Log.e(TAG, "Failed to sample another user: " + e.getMessage())
                );
            }
            if (onSuccess != null) onSuccess.onSuccess(null);
        }).addOnFailureListener(e -> {
            if (onFailure != null) onFailure.onFailure(e);
        });
    }

    /**
     * Fetch user's selected events
     * @param eventList List to populate with selected events
     * @param onComplete Callback when fetching is complete
     */
    public void fetchUserSelectedEvents(List<Event> eventList, Runnable onComplete) {
        getUserDocument().get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> selectedEventsJoined = (ArrayList<String>) documentSnapshot.get("selectedEventsJoined");

                        Log.d("SelectedEvents", "Selected events fetched: " + selectedEventsJoined);


                        if (selectedEventsJoined != null && !selectedEventsJoined.isEmpty()) {
                            List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                            for (String eventId : selectedEventsJoined) {
                                tasks.add(db.collection("events").document(eventId).get());
                            }

                            Log.d("SelectedEvents", "Selected events fetched: " + selectedEventsJoined);

                            Tasks.whenAllComplete(tasks)
                                    .addOnSuccessListener(taskSnapshots -> {
                                        eventList.clear();
                                        for (Task<DocumentSnapshot> task : tasks) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot doc = task.getResult();

                                                Log.d("DOC", doc.getId());
                                                if (doc.exists()) {
                                                    Event event = new Event(
                                                            doc.getId(),
                                                            doc.getString("name"),
                                                            doc.getString("description"),
                                                            doc.getString("eventDate"),
                                                            doc.getString("location")
                                                    );
//                                                    event.setGeolocationEnabled(doc.getBoolean("geolocationEnabled"));
//                                                    event.setWaitList((ArrayList<String>) doc.get("waitingList"));
//                                                    event.setSelectedParticipants((ArrayList<String>) doc.get("selectedList"));
//                                                    event.setCancelledParticipants((ArrayList<String>) doc.get("cancelledList"));
                                                    eventList.add(event);
                                                }

                                                Log.d("SelectedEvents", "Selected events fetched: " + eventList.size());

                                            }
                                        }

                                        if (onComplete != null) {
                                            onComplete.run();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error fetching selected events", e);
                                        if (onComplete != null) {
                                            onComplete.run();
                                        }
                                    });
                        } else {
                            eventList.clear();
                            if (onComplete != null) {
                                onComplete.run();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user document", e);
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
    }


    public void addConfirmedEvent(String eventId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        DocumentReference userRef = getUserDocument();

        db.runTransaction(transaction -> {
            DocumentSnapshot userSnapshot = transaction.get(userRef);
            ArrayList<String> confirmedEventsJoined = (ArrayList<String>) userSnapshot.get("confirmedEventsJoined");

            if (confirmedEventsJoined == null) {
                confirmedEventsJoined = new ArrayList<>();
            }

            if (!confirmedEventsJoined.contains(eventId)) {
                confirmedEventsJoined.add(eventId);
                transaction.update(userRef, "confirmedEventsJoined", confirmedEventsJoined);
            }

            return null;
        }).addOnSuccessListener(result -> {
            if (onSuccess != null) onSuccess.onSuccess(null);
        }).addOnFailureListener(e -> {
            if (onFailure != null) onFailure.onFailure(e);
        });
    }


    public void fetchUserConfirmedEvents(String userId, String listType, List<Event> eventList, Runnable onComplete) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> confirmedEvents = (ArrayList<String>) documentSnapshot.get(listType);

                        if (confirmedEvents != null && !confirmedEvents.isEmpty()) {
                            List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                            for (String eventId : confirmedEvents) {
                                tasks.add(db.collection("events").document(eventId).get());
                            }

                            Tasks.whenAllComplete(tasks)
                                    .addOnSuccessListener(taskSnapshots -> {
                                        eventList.clear();
                                        for (Task<DocumentSnapshot> task : tasks) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot doc = task.getResult();
                                                if (doc != null && doc.exists()) {
                                                    Event event = new Event(
                                                            doc.getId(),
                                                            doc.getString("name"),
                                                            doc.getString("description"),
                                                            doc.getString("eventDate"),
                                                            doc.getString("location")
                                                    );
                                                    event.setGeolocationEnabled(doc.getBoolean("geolocationEnabled"));
                                                    event.setWaitList((ArrayList<String>) doc.get("waitingList"));
                                                    event.setSelectedParticipants((ArrayList<String>) doc.get("selectedList"));
                                                    event.setCancelledParticipants((ArrayList<String>) doc.get("cancelledList"));
                                                    eventList.add(event);
                                                }
                                            }
                                        }
                                        if (onComplete != null) {
                                            onComplete.run();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error fetching confirmed events", e);
                                        if (onComplete != null) {
                                            onComplete.run();
                                        }
                                    });
                        } else {
                            eventList.clear();
                            if (onComplete != null) {
                                onComplete.run();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user document", e);
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
    }


     /* This method will get all of the user profiles for the admin to be able to browse them and then delete them if they have to.
     * @return ArrayList<User> return a list of users for the admin to be able to browse
     * @author Nathan Barrett
     */
    public ArrayList<User> getProfilesCollection() {
        ArrayList<User> userArray = new ArrayList<User>();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getString("name");
                        Log.d("Admin", name);
                        String[] nameParts = name.split(" ", 2);
                        String firstName = nameParts[0];
                        String lastName = nameParts[1];
                        String email = document.getString("email");
                        String phone = document.getString("phone");
                        String deviceId = document.getId();
                        User user;
                        if (phone != null && !phone.equals("")) {
                            user = new User(firstName, lastName, email, phone, deviceId);
                        } else {
                            user = new User(firstName, lastName, email, deviceId);
                        }
                        Log.d("Admin", user.toString());
                        userArray.add(user);
                    }
                } else {
                    Log.d("Admin", "is empty");
                }
            }
        });
        return userArray;
    }

    public void getAllEvent(){
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Admin", document.getId() + " => " + document.getData());
                    }
                }
            }
        });
    }

    public void removeEntrantFromEvent(String eventId, String userId,
                                       OnSuccessListener<Void> onSuccess,
                                       OnFailureListener onFailure) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        DocumentReference userRef = db.collection("users").document(userId);
        Log.d("DEVIDEIDSEL", userId);
        db.runTransaction(transaction -> {
            // Fetch event document
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            List<String> selectedList = (List<String>) eventSnapshot.get("selectedList");
            List<String> cancelledList = (List<String>) eventSnapshot.get("cancelledList");

            // Fetch user document
            DocumentSnapshot userSnapshot = transaction.get(userRef);
            List<String> selectedEventsJoined = (List<String>) userSnapshot.get("selectedEventsJoined");

            // Ensure lists are initialized
            if (selectedList == null) selectedList = new ArrayList<>();
            if (cancelledList == null) cancelledList = new ArrayList<>();
            if (selectedEventsJoined == null) selectedEventsJoined = new ArrayList<>();

            // Remove user from selected list and add to cancelled list
            selectedList.remove(userId);
            if (!cancelledList.contains(userId)) cancelledList.add(userId);

            // Remove event from user's selected events
            selectedEventsJoined.remove(eventId);

            // Update Firestore
            transaction.update(eventRef, "selectedList", selectedList);
            transaction.update(eventRef, "cancelledList", cancelledList);
            transaction.update(userRef, "selectedEventsJoined", selectedEventsJoined);

            return null;
        }).addOnSuccessListener(result -> {
            if (onSuccess != null) onSuccess.onSuccess(null);
        }).addOnFailureListener(e -> {
            if (onFailure != null) onFailure.onFailure(e);
        });
    }


}

