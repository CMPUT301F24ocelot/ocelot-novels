package com.example.ocelotnovels.utils;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ocelotnovels.model.Event;
import com.example.ocelotnovels.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
     * This method will get all of the user profiles for the admin to be able to browse them and then delete them if they have to.
     * @return ArrayList<User> return a list of users for the admin to be able to browse
     */
    public ArrayList<User> getProfilesCollection(){
        ArrayList<User> userArray = new ArrayList<User>();
        db.collection("users").get().addOnSuccessListener(querySnapshot ->{
            if(!querySnapshot.isEmpty()){
                List<DocumentSnapshot> profiles = querySnapshot.getDocuments();
                for(int i = 0;i<profiles.size();i++) {
                    DocumentSnapshot profile = profiles.get(i);
                    if(profile.exists()){
                        String name = profile.getString("name");
                        String[] nameParts = name.split(" ", 2);
                        String firstName = nameParts[0];
                        String lastName = nameParts[1];
                        String email = profile.getString("email");
                        String phone = profile.getString("phone");
                        User user;
                        if(phone != null && !phone.equals("")){
                            user = new User(firstName, lastName, email, phone);
                        }else{
                            user = new User(firstName, lastName, email);
                        }
                        userArray.add(user);
                    }
                }
            }
        });
        return userArray;
    }
}
