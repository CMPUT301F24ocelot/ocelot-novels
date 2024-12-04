package com.example.ocelotnovels.view.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.User;
import com.example.ocelotnovels.utils.FirebaseUtils;

public class EntrantProfileAdminView extends AppCompatActivity {
    private ImageView profilePicture;
    private TextView name, email, phone;
    private Button deleteButton;
    private User profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_profile_admin_view);

        // Use savedInstanceState instead of savedInstance for clarity
        Bundle item = getIntent().getExtras();
        if (item != null) {
            profile = (User) item.getSerializable("User");
        }

        initializeView();
    }

    private void initializeView() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        name = findViewById(R.id.name);
        name.setText(profile.getFirstName() + " " + profile.getLastName());

        email = findViewById(R.id.email);
        email.setText(profile.getEmail());

        phone = findViewById(R.id.phone);
        phone.setText(profile.getPhoneNumber() != null ? profile.getPhoneNumber() : "");

        profilePicture = findViewById(R.id.profileImageView);
        String profilePicUrl = profile.getProfilePicture();

        // Remove the redundant setImageResource call
        Glide.with(this)
                .load(profilePicUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(profilePicture);

        deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(view -> {
            FirebaseUtils firebaseutils = FirebaseUtils.getInstance(getApplicationContext());
            firebaseutils.deleteUser(getApplicationContext(), profile);
            Intent toBrowser = new Intent(EntrantProfileAdminView.this, AdminBrowseActivity.class);
            toBrowser.putExtra("from", "Profiles");
            startActivity(toBrowser);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent toBrowser = new Intent(EntrantProfileAdminView.this, AdminBrowseActivity.class);
        toBrowser.putExtra("from", "Profiles");
        startActivity(toBrowser);
        return true;
    }
}