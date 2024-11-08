package com.example.ocelotnovels.utils;


import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.ocelotnovels.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
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

}