package com.example.ocelotnovels;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class QrCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_qr_code);

        // Get data passed from CreateEventActivity
        String eventTitle = getIntent().getStringExtra("eventTitle");
        String eventDescription = getIntent().getStringExtra("eventDescription");
        String eventDeadline = getIntent().getStringExtra("eventDeadline");
        String eventLocation = getIntent().getStringExtra("eventLocation");
        String qrCodeUrl = getIntent().getStringExtra("qrCodeUrl");

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

        // Load QR code into ImageView
        Glide.with(this).load(qrCodeUrl).into(qrImageView);

        // Back button action
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }
}