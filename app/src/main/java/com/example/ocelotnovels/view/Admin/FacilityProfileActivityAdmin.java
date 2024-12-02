package com.example.ocelotnovels.view.Admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Facility;
import com.example.ocelotnovels.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class FacilityProfileActivityAdmin extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText facilityName, facilityEmail, facilityPhone, facilityLocation, facilityDescription;
    private ImageView facilityProfileImage;
    private Button deleteButton;
    private Facility facility;

    private FirebaseFirestore db;
    private String facilityId; // Facility ID for Firestore document
    private String ownerId;    // Owner ID of the facility

    /**
     * This function will be called when this activity is used
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_profile_activity_admin);
        Bundle item = getIntent().getExtras();
        if(item != null){
            facility = (Facility)item.getSerializable("Facility");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        initializeView();

    }

    /**
     * This function will get all of the view objects and set them to the correct values for the facility.
     */
    private void initializeView(){
        facilityName = findViewById(R.id.facility_name);
        facilityName.setText(facility.getFacilityName());
        facilityEmail = findViewById(R.id.email);
        facilityEmail.setText(facility.getFacilityEmail());
        facilityPhone = findViewById(R.id.phone);
        facilityPhone.setText(facility.getFacilityPhone());
        facilityLocation = findViewById(R.id.location);
        facilityLocation.setText(facility.getFacilityLocation());
        facilityDescription = findViewById(R.id.description);
        facilityDescription.setText(facility.getFacilityDescription());
        facilityProfileImage = findViewById(R.id.profile_image);
        if (facility.getFacilityPicUrl() != null && !facility.getFacilityPicUrl().isEmpty()) {
            Glide.with(this)
                    .load(facility.getFacilityPicUrl())
                    .placeholder(R.drawable.ic_image_placeholder) // Placeholder while loading
                    .error(R.drawable.ic_image_placeholder)      // Fallback image on error
                    .into(facilityProfileImage);
        } else {
            // Default picture logic
            facilityProfileImage.setImageResource(R.drawable.ic_image_placeholder);
        }
        deleteButton = findViewById(R.id.facility_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /**
     * allows navigation back to the previous activity
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed(); // This navigates back to the parent activity.
        return true;
    }

}