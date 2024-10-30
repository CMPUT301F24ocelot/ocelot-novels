package com.example.ocelotnovels;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;

public class OrganizerProfile extends AppCompatActivity {

    private EditText facilityName, email, phone, location, description;
    private ImageView profileImage;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_profile_activity);

        // Initialize views
        profileImage = findViewById(R.id.profile_image);
        facilityName = findViewById(R.id.facility_name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        location = findViewById(R.id.location);
        description = findViewById(R.id.description);
        saveButton = findViewById(R.id.save_button);

        // You can add functionality here, e.g.:
        // profileImage.setOnClickListener(...) for selecting a new profile image
        // saveButton.setOnClickListener(...) for saving the profile details
    }
}