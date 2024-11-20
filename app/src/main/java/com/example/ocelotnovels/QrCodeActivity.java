package com.example.ocelotnovels;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class QrCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_qr_code);

        // Get data passed from CreateEventActivity
        Intent intent = getIntent();
        String eventTitle = intent.getStringExtra("eventTitle");
        String eventDescription = intent.getStringExtra("eventDescription");
        String eventDeadline = intent.getStringExtra("eventDeadline");
        String eventLocation = intent.getStringExtra("eventLocation");
        String qrHash = intent.getStringExtra("qrHash");

        // Set views with event data
        TextView titleTextView = findViewById(R.id.event_title);
        TextView descriptionTextView = findViewById(R.id.event_description);
        TextView deadlineTextView = findViewById(R.id.event_deadline);
        TextView locationTextView = findViewById(R.id.event_status); // Use appropriate TextView IDs
        ImageView qrImageView = findViewById(R.id.event_qr_code_image);

        titleTextView.setText("Title: " + eventTitle);
        descriptionTextView.setText("Description: " + eventDescription);
        deadlineTextView.setText("Deadline: " + eventDeadline);
        locationTextView.setText("Location: " + eventLocation);

        // Retrieve QR code bitmap passed from CreateEventActivity
        qrImageView.setImageBitmap(getIntent().getParcelableExtra("qrCodeBitmap"));

        // Back button to return to the main events page
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish()); // Finish activity to return to previous screen
    }
}
