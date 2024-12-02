/**
 * MyFirebaseMessagingService is a service class that extends FirebaseMessagingService
 * to handle Firebase Cloud Messaging (FCM) functionality. It provides the following features:
 * - Handles the receipt of new FCM tokens and sends them to the server or Firestore.
 * - Processes incoming FCM notifications and data messages.
 * - Displays notifications to the user.
 * - Handles custom data payload logic.
 */
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

    /**
     * Called when a new FCM token is generated. This method sends the token to the server or Firestore.
     *
     * @param token The new FCM token.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("FCM TOKEN", "Refreshed token: " + token);
        super.onNewToken(token);
        // Send the new FCM token to your server or Firestore
        sendTokenToServer(token);
    }

    /**
     * Called when an FCM message is received. Handles both notification and data payloads.
     *
     * @param remoteMessage The message received from FCM.
     */
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

    /**
     * Displays a notification to the user with the specified title and message.
     *
     * @param title   The title of the notification.
     * @param message The message body of the notification.
     */
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

    /**
     * Handles custom data payload received from the FCM message.
     *
     * @param data A map of key-value pairs representing the data payload.
     */
    private void handleDataPayload(Map<String, String> data) {
        // Process custom data payload
        String eventId = data.get("eventId");
        String message = data.get("message");
        // Add your logic here, e.g., update Firestore or notify the user in-app
    }

    /**
     * Sends the FCM token to Firestore or your server for later use.
     *
     * @param token The FCM token to send.
     */
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
