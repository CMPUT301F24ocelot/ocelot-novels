package com.example.ocelotnovels.view.Entrant;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ocelotnovels.R;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Add functionality to manage settings
    }
}
