package com.example.ocelotnovels.view.Admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.User;

public class EntrantProfileAdminView extends AppCompatActivity {

    private ImageView profilePicture;
    private TextView name, email, phone;
    private Button deleteButton;
    private User profile;

    @Override
    protected void onCreate(Bundle savedInstance){
        Bundle item = getIntent().getExtras();
        if(item != null){
            profile = (User)item.getSerializable("User");
        }
        super.onCreate(savedInstance);
        setContentView(R.layout.entrant_profile_admin_view);
        initializeView();
    }

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
