package com.example.ocelotnovels.utils;


import android.content.Context;
import android.content.SharedPreferences;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

    protected String getDeviceId(Context context){
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

    public DocumentReference getDocument(){
        return this.db.collection("users").document(deviceId);
    }

    // Organizer-Specific Methods

    // Get events created by this organizer
    public Task<QuerySnapshot> getOrganizerEvents() {
        return this.db.collection("events")
                .whereEqualTo("organizerId", deviceId)
                .get();
    }

    // Add a new event
    public Task<DocumentReference> addEvent(Event event) {
        return this.db.collection("events").add(event);
    }

    // Update an existing event
    public Task<Void> updateEvent(String eventId, Map<String, Object> updates) {
        return this.db.collection("events").document(eventId).update(updates);
    }

    // Delete an event
    public Task<Void> deleteEvent(String eventId) {
        return this.db.collection("events").document(eventId).delete();
    }

    public static String getUserDisplayName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getDisplayName() : "User";
    }
}
