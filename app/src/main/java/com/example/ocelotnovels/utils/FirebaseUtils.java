package com.example.ocelotnovels.utils;


import android.content.Context;
import android.content.SharedPreferences;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
}
