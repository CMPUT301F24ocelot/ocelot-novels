package com.example.ocelotnovels.utils;


import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;



import androidx.annotation.NonNull;


import com.example.ocelotnovels.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FirebaseUtils {
    private static FirebaseUtils instance;
    private final FirebaseFirestore db;
    private final String deviceId;

    public FirebaseUtils(Context context) {
        this.db = FirebaseFirestore.getInstance();
        this.deviceId = getDeviceId(context);
    }

    public static FirebaseUtils getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseUtils(context);
        }
        return instance;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public String getDeviceId(Context context){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user_settings",Context.MODE_PRIVATE);
        String deviceId = sharedPreferences.getString("DeviceId",null);

        if (deviceId==null){
            deviceId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putString("DeviceId",deviceId);
            editor.apply();
        }

        return deviceId;
    }

    public DocumentReference getUserDocument(){
        return this.db.collection("users").document(deviceId);

    }

    public void pushUserDocument(Context context, Map<String,Object> userData){

        db.collection("users").document(deviceId).set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void fetchUserJoinedEvents(List<Event> eventList, Runnable onComplete) {
        getUserDocument().get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> eventsJoined = (ArrayList<String>) documentSnapshot.get("eventsJoined");
                        if (eventsJoined != null && !eventsJoined.isEmpty()) {
                            // Create a list to store all the event fetch tasks
                            List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

                            // Create tasks for fetching each event
                            for (String eventId : eventsJoined) {
                                Task<DocumentSnapshot> task = db.collection("events")
                                        .document(eventId)
                                        .get();
                                tasks.add(task);
                            }

                            // Wait for all tasks to complete
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

    public void joinEventWaitlist(String eventId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        DocumentReference eventRef = db.collection("events").document(eventId);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef);
            ArrayList<String> waitList = (ArrayList<String>) snapshot.get("waitingList");
            ArrayList<String> cancelledList = (ArrayList<String>) snapshot.get("cancelledList");

            if (waitList == null) {
                waitList = new ArrayList<>();
            }

            // Check if user is not already in waitlist and not in cancelled list
            if (!waitList.contains(deviceId) && (cancelledList == null || !cancelledList.contains(deviceId))) {
                waitList.add(deviceId);
                transaction.update(eventRef, "waitingList", waitList);
            }

            return null;
        }).addOnSuccessListener(result -> {
            if (onSuccess != null) {
                onSuccess.onSuccess(null);
            }
        }).addOnFailureListener(e -> {
            if (onFailure != null) {
                onFailure.onFailure(e);
            }
        });
    }

    public void leaveEventWaitlist(String eventId, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        DocumentReference userRef = getUserDocument();

        db.runTransaction(transaction -> {
            // Update event's waitlist and cancelled list
            DocumentSnapshot eventSnapshot = transaction.get(eventRef);
            ArrayList<String> waitList = (ArrayList<String>) eventSnapshot.get("waitingList");
            ArrayList<String> cancelledList = (ArrayList<String>) eventSnapshot.get("cancelledList");

            if (waitList != null && waitList.contains(deviceId)) {
                waitList.remove(deviceId);
                if (cancelledList == null) {
                    cancelledList = new ArrayList<>();
                }
                cancelledList.add(deviceId);

                transaction.update(eventRef,
                        "waitingList", waitList,
                        "cancelledList", cancelledList
                );
            }

            // Update user's eventsJoined list
            DocumentSnapshot userSnapshot = transaction.get(userRef);
            ArrayList<String> eventsJoined = (ArrayList<String>) userSnapshot.get("eventsJoined");

            if (eventsJoined != null && eventsJoined.contains(eventId)) {
                eventsJoined.remove(eventId);
                transaction.update(userRef, "eventsJoined", eventsJoined);
            }

            return null;
        }).addOnSuccessListener(result -> {
            if (onSuccess != null) {
                onSuccess.onSuccess(null);
            }
        }).addOnFailureListener(e -> {
            if (onFailure != null) {
                onFailure.onFailure(e);
            }
        });
    }


}