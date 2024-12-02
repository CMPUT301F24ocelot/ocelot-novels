/**
 * EventDetailsActivity is responsible for displaying and managing the details of a specific event.
 * This activity fetches event details such as name, date, location, description, and poster from Firestore,
 * and allows the user to:
 * - Edit the event poster by selecting a new image from the device.
 * - Delete the event from Firestore.
 * - Navigate to other screens like waiting lists, selected lists, cancelled lists, and confirmed lists for the event.
 *
 * Key features:
 * - Firebase Firestore integration for retrieving and updating event details.
 * - Firebase Storage for uploading and displaying event posters.
 * - Glide library for image loading.
 * - Menu options for navigating to different event-related lists.
 * - Error handling and user feedback using Toast messages and logs.
 *
 * Dependencies:
 * - Firebase Firestore
 * - Firebase Storage
 * - Glide
 */

package com.example.ocelotnovels.view.Organizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ocelotnovels.MapsActivity;
import com.example.ocelotnovels.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class EventDetailsActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailsActivity";
    private static final int PICK_POSTER_REQUEST = 1;

    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String eventId; // To store the ID of the event being displayed
    private String currentPosterUrl;
    public OrganizerEventAdapter eventAdapter;

    private ImageView posterImageView;

    /**
     * Initializes the activity and sets up event details, buttons, and Firebase references.
     *
     * @param savedInstanceState Saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Event Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get the event name passed from the adapter
        String eventName = getIntent().getStringExtra("eventName");

        // Initialize Firestore and Firebase Storage
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        posterImageView = findViewById(R.id.event_poster_imageview);

        // Fetch event details and display them
        fetchEventDetails(eventName);

        // Set up the Delete Event button
        Button deleteButton = findViewById(R.id.delete_event_button);
        deleteButton.setOnClickListener(v -> deleteEvent());

        // Set up the Edit Poster button
        Button editPosterButton = findViewById(R.id.edit_poster_button);
        editPosterButton.setOnClickListener(v -> selectNewPoster());
    }

    /**
     * Inflates the menu and adds items to the action bar.
     *
     * @param menu The menu to be created.
     * @return true if the menu is successfully created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entrant_list_menu, menu);
        return true;
    }

    /**
     * Handles menu item selections for navigating to different event-related lists.
     *
     * @param item The selected menu item.
     * @return true if the menu item is successfully handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (eventId == null) {
            Toast.makeText(this, "Event ID is not available. Cannot navigate.", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }

        // Handle menu item clicks
        if (id == R.id.menu_waiting_list) {
            Intent waitingListIntent = new Intent(EventDetailsActivity.this, OrganizerWaitingListActivity.class);
            waitingListIntent.putExtra("eventId", eventId);
            startActivity(waitingListIntent);
            return true;
        }

        if (id == R.id.menu_selected_list) {
            Intent selectedListIntent = new Intent(EventDetailsActivity.this, OrganiserSelectedListActivity.class);
            selectedListIntent.putExtra("eventId", eventId);
            startActivity(selectedListIntent);
            return true;
        }

        if (id == R.id.menu_cancelled_list) {
            Intent cancelledListIntent = new Intent(EventDetailsActivity.this, OrganiserCancelledListActivity.class);
            cancelledListIntent.putExtra("eventId", eventId);
            startActivity(cancelledListIntent);
            return true;
        }

        if (id == R.id.menu_confirmed_list) {
            Intent confirmedListIntent = new Intent(EventDetailsActivity.this, OrganiserConfirmedListActivity.class);
            confirmedListIntent.putExtra("eventId", eventId);
            startActivity(confirmedListIntent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Fetches event details from Firestore based on the event name and updates the UI.
     *
     * @param eventName The name of the event to fetch details for.
     */
    private void fetchEventDetails(String eventName) {
        db.collection("events")
                .whereEqualTo("name", eventName) // Replace "name" with your Firestore field for event names
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            eventId = document.getId(); // Store the event ID for deletion

                            String eventDate = document.getString("eventDate");
                            String eventLocation = document.getString("location");
                            String eventDescription = document.getString("description");
                            String posterUrl = document.getString("posterUrl"); // Field for poster URL

                            // Set the details in TextViews
                            TextView nameTextView = findViewById(R.id.event_name_textview);
                            TextView dateTextView = findViewById(R.id.event_date_textview);
                            TextView locationTextView = findViewById(R.id.event_location_textview);
                            TextView descriptionTextView = findViewById(R.id.event_description_textview);

                            nameTextView.setText(eventName);
                            dateTextView.setText(eventDate != null ? eventDate : "No date available");
                            locationTextView.setText(eventLocation != null ? eventLocation : "No location available");
                            descriptionTextView.setText(eventDescription != null ? eventDescription : "No description available");

                            // Load the event poster
                            ImageView posterImageView = findViewById(R.id.event_poster_imageview);
                            if (posterUrl != null && !posterUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(posterUrl)
                                        .placeholder(R.drawable.ic_image_placeholder)
                                        .error(R.drawable.ic_image_placeholder)
                                        .into(posterImageView);
                            } else {
                                posterImageView.setImageResource(R.drawable.ic_image_placeholder);
                            }
                        }
                    } else {
                        Log.w(TAG, "No matching documents found.");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching event details", e));
    }

    /**
     * Allows the user to select a new event poster image from their device.
     */
    private void selectNewPoster() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_POSTER_REQUEST);
    }

    /**
     * Handles the result of the activity started for selecting an image.
     *
     * @param requestCode The request code used to start the activity.
     * @param resultCode  The result code returned by the activity.
     * @param data        The data returned by the activity, containing the selected image.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_POSTER_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                uploadNewPoster(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Uploads a new event poster image to Firebase Storage and updates the Firestore document.
     *
     * @param bitmap The bitmap of the new poster image to upload.
     */
    private void uploadNewPoster(Bitmap bitmap) {
        String newPosterId = UUID.randomUUID().toString(); // Generate a unique ID for the new poster
        StorageReference posterRef = storageRef.child("images/events/" + newPosterId + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        posterRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> posterRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String newPosterUrl = uri.toString();
                            updatePosterInFirestore(newPosterUrl);
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to get image URL.", Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Updates the Firestore document with the URL of the new poster image.
     *
     * @param newPosterUrl The URL of the new poster image.
     */
    private void updatePosterInFirestore(String newPosterUrl) {
        if (eventId != null) {
            db.collection("events").document(eventId)
                    .update("posterUrl", newPosterUrl)
                    .addOnSuccessListener(aVoid -> {
                        currentPosterUrl = newPosterUrl;
                        Glide.with(this)
                                .load(newPosterUrl)
                                .placeholder(R.drawable.ic_image_placeholder)
                                .error(R.drawable.ic_image_placeholder)
                                .into(posterImageView);
                        Toast.makeText(this, "Poster updated successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to update poster URL in Firestore.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Event ID not found. Cannot update poster.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Deletes the event from Firestore and provides feedback to the user.
     */
    private void deleteEvent() {
        if (eventId != null) {
            db.collection("events").document(eventId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Event deleted successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to delete event.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error deleting event", e);
                    });
//            eventAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Event ID not found. Cannot delete.", Toast.LENGTH_SHORT).show();
        }
    }
}
