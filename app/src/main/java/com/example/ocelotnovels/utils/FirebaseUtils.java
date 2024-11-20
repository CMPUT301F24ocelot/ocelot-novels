package com.example.ocelotnovels.utils;


import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;



import androidx.annotation.NonNull;


import com.example.ocelotnovels.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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
    private FirebaseFirestore db;
    private String deviceId;

    public FirebaseUtils(Context context){
        if (context!=null){
            this.db = FirebaseFirestore.getInstance();
            this.deviceId = getDeviceId(context);
        }
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

    public void fetchUserWaitingListEvents(List<Event> eventList, Runnable onComplete) {
        // Query events where waitList array contains the current deviceId
        db.collection("events")
                .whereArrayContains("waitList", deviceId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    eventList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String eventId= document.getString("eventId");
                        Date eventDate =(Date) document.getDate("eventDate");
                        Date eventRegOpen =(Date) document.getDate("regOpen");
                        Date eventDeadline =(Date) document.getDate("regClosed");
                        String eventName = document.getString("name");
                        String eventDescription= document.getString("description");
                        Long eventCapacity =document.get("capacity")==null? -1 :  (Long) document.get("capacity");

                        String organizerId = document.getString("organizerId");
                        String eventLocation = document.getString("location");
                        String  posterUrl = document.getString("posterURL");
                        ArrayList<String> waitingList= (ArrayList<String>) document.get("waitList");
                        ArrayList<String> cancelledParticipants = (ArrayList<String>) document.get("cancelledList");
                        ArrayList<String> selectedParticipants = (ArrayList<String>) document.get("selectedList");
                        String qrHash = document.getString("qrHash");
                        Boolean geolocationEnabled = document.getBoolean("geolocationEnabled");
                        Event event = new Event(eventId,eventName,eventDescription,eventDate,eventRegOpen,eventDeadline,eventCapacity,posterUrl,organizerId,eventLocation,waitingList,selectedParticipants,cancelledParticipants,qrHash,geolocationEnabled);

                        eventList.add(event);
                    }
                    onComplete.run();
                })
                .addOnFailureListener(e -> {
                    // Log or handle errors
                    Log.e("Firestore", "Error fetching documents", e);
                });
    }

}