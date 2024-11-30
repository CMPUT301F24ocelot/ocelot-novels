package com.example.ocelotnovels.view.Admin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ocelotnovels.R;

public class EntrantProfileAdminView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.entrant_profile_admin_view);
        initializeView();
    }

    private void initializeView() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
