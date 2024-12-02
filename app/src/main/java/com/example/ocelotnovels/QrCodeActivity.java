/**
 * QrCodeActivity displays the details of an event along with a QR code.
 * The event data and QR code are passed as extras from the previous activity.
 * Users can view the event details and navigate back to the OrganizerMainActivity.
 */

package com.example.ocelotnovels;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ocelotnovels.view.Organizer.OrganizerMainActivity;

public class QrCodeActivity extends AppCompatActivity {

    /**
     * Initializes the activity, sets the layout, and populates the UI with event details
     * and a QR code image. Also handles the back button functionality to navigate
     * back to the OrganizerMainActivity.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_qr_code);

        // Get data passed from CreateEventActivity
        String eventTitle = getIntent().getStringExtra("eventTitle");
        String eventDescription = getIntent().getStringExtra("eventDescription");
        String eventDeadline = getIntent().getStringExtra("eventDeadline");
        String eventLocation = getIntent().getStringExtra("eventLocation");
        byte[] qrCodeBytes = getIntent().getByteArrayExtra("qrCode");

        // Decode QR Code Bitmap
        Bitmap qrCodeBitmap = BitmapFactory.decodeByteArray(qrCodeBytes, 0, qrCodeBytes.length);

        // Set views with event data
        TextView titleTextView = findViewById(R.id.event_title);
        TextView descriptionTextView = findViewById(R.id.event_description);
        TextView deadlineTextView = findViewById(R.id.event_deadline);
        TextView locationTextView = findViewById(R.id.event_status);
        ImageView qrImageView = findViewById(R.id.event_qr_code_image);

        titleTextView.setText("Title: " + eventTitle);
        descriptionTextView.setText("Description: " + eventDescription);
        deadlineTextView.setText("Deadline: " + eventDeadline);
        locationTextView.setText("Location: " + eventLocation);

        // Display QR Code
        qrImageView.setImageBitmap(qrCodeBitmap);

        // Back button action
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            // Navigate back to the main events page
            Intent intent = new Intent(QrCodeActivity.this, OrganizerMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
