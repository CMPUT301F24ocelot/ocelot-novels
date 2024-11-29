package com.example.ocelotnovels;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "notification_channel";

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("FCM TOKEN", "Refreshed token: " + token);
        super.onNewToken(token);
        // Send the new FCM token to your server or Firestore
        sendTokenToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Handle the received notification or data message
        if (remoteMessage.getNotification() != null) {
            // Display the notification
            showNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody()
            );
        }

        if (remoteMessage.getData().size() > 0) {
            // Handle data payload if necessary
            handleDataPayload(remoteMessage.getData());
        }
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel for Android 8.0 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Firebase Notifications");
            notificationManager.createNotificationChannel(channel);
        }

        // Create an intent to open the app when the notification is tapped
        Intent intent = new Intent(this, MainActivity.class); // Change to your desired activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Show the notification
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void handleDataPayload(Map<String, String> data) {
        // Process custom data payload
        String eventId = data.get("eventId");
        String message = data.get("message");
        // Add your logic here, e.g., update Firestore or notify the user in-app
    }

    private void sendTokenToServer(String token) {
        // Send the FCM token to Firestore or your server
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUtils firebaseUtils = new FirebaseUtils(this);
        String deviceId = firebaseUtils.getDeviceId(this); // Replace with the current user's ID

        db.collection("users").document(deviceId)
                .update("fcmToken", token)
                .addOnSuccessListener(aVoid -> Log.d("FCM", "Token updated successfully"))
                .addOnFailureListener(e -> Log.w("FCM", "Error updating token", e));
    }
}
