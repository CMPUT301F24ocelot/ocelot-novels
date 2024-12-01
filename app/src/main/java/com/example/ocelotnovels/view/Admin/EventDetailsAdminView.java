package com.example.ocelotnovels.view.Admin;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Event;
import com.example.ocelotnovels.model.User;
import com.example.ocelotnovels.utils.FirebaseUtils;

/**
 * This class is used to display an events information to an admin
 */
public class EventDetailsAdminView extends AppCompatActivity {
    private Event event;
    private FirebaseUtils firebaseUtils;

    /**
     * This function is called when an Intent transitions to this view
     * @param savedInstance
     */
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.event_details_admin);
        Bundle item = getIntent().getExtras();
        if(item != null){
            event = (Event)item.getSerializable("Event");
        }
        initializeFirebase();
        initializeView();
    }

    /**
     * initializes the view for interaction in this activity
     */
    private void initializeView() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ImageView poster = findViewById(R.id.event_poster);
        String posterUrl = event.getEventPosterUrl();
        Glide.with(this)
                .load(posterUrl)
                .placeholder(R.drawable.ic_image_placeholder) // Optional
                .error(R.drawable.ic_image_placeholder) // Optional
                .into(poster);
        poster.setImageResource(R.drawable.ic_image_placeholder);
        TextView eventName = findViewById(R.id.name);
        eventName.setText(event.getEventName());
        TextView eventDescription = findViewById(R.id.description);
        eventDescription.setText(event.getEventDescription());

        Button deleteQRButton = findViewById(R.id.delete_qr_button);
        deleteQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Admin",event.getEventId());
                firebaseUtils.deleteEventQRHash(view.getContext(), event.getEventId());
            }
        });
    }

    /**
     * initializes access to the firastore database
     */
    private void initializeFirebase() {
        try {
            firebaseUtils = FirebaseUtils.getInstance(this);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Firebase", e);
            Toast.makeText(this, "Error initializing app", Toast.LENGTH_SHORT).show();
            finish();
        }
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
