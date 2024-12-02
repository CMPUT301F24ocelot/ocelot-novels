/**
 * This activity class represents the admin view of an entrant's profile.
 * It displays the entrant's profile details, including their name, email,
 * phone number, and profile picture. The admin can view this information and
 * navigate back to the previous screen. The profile data is passed as a
 * serialized `User` object through the intent extras.
 */

package com.example.ocelotnovels.view.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.User;
import com.example.ocelotnovels.utils.FirebaseUtils;

/**
 * EntrantProfileAdminView is an activity that displays an entrant's profile
 * to the admin, including their name, email, phone number, and profile picture.
 */
public class EntrantProfileAdminView extends AppCompatActivity {

    private ImageView profilePicture;
    private TextView name, email, phone;
    private Button deleteButton;
    private User profile;

    /**
     * Called when the activity is created. Initializes the view and retrieves
     * the entrant's profile data from the intent extras.
     *
     * @param savedInstance the previously saved instance state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Bundle item = getIntent().getExtras();
        setContentView(R.layout.entrant_profile_admin_view);
        if(item != null){
            profile = (User)item.getSerializable("User");
        }
        initializeView();
    }

    /**
     * Initializes the UI elements and populates them with the entrant's data.
     */
    private void initializeView() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        name = findViewById(R.id.name);
        name.setText(profile.getFirstName()+ " " +profile.getLastName());
        email = findViewById(R.id.email);
        email.setText(profile.getEmail());
        phone = findViewById(R.id.phone);
        if(profile.getPhoneNumber() != null){
            phone.setText(profile.getPhoneNumber());
        }else{
            phone.setText("");
        }
        profilePicture = findViewById(R.id.profileImageView);
        String profilePicUrl = profile.getProfilePicture();
        Glide.with(this)
                .load(profilePicUrl)
                .placeholder(R.drawable.ic_image_placeholder) // Optional
                .error(R.drawable.ic_image_placeholder) // Optional
                .into(profilePicture);
        profilePicture.setImageResource(R.drawable.ic_image_placeholder);

        deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUtils firebaseutils = FirebaseUtils.getInstance(getApplicationContext());
                firebaseutils.deleteUser(getApplicationContext(),profile);
                Intent toBrowser = new Intent(EntrantProfileAdminView.this, AdminBrowseActivity.class);
                toBrowser.putExtra("from", "Profiles");
                startActivity(toBrowser);
            }
        });
    }

    /**
     * allows navigation back to the previous activity
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        Intent toBrowser = new Intent(EntrantProfileAdminView.this, AdminBrowseActivity.class);
        toBrowser.putExtra("from", "Profiles");
        startActivity(toBrowser);
        return true;
    }
}
