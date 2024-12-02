/**
 * This class represents the Settings screen for an Entrant in the Ocelot Novels application.
 * It provides an interface for the user to view and modify their settings.
 * The activity is defined by the layout file `entrant_activity_settings.xml`.
 * It also enables the back button in the action bar for navigation purposes.
 */

package com.example.ocelotnovels.view.Entrant;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ocelotnovels.R;

/**
 * The SettingsActivity class extends AppCompatActivity to provide a settings interface
 * for an Entrant user in the Ocelot Novels application. It inflates the associated layout
 * and configures the action bar.
 */
public class SettingsActivity extends AppCompatActivity {
    /**
     * Called when the activity is starting. This method initializes the activity,
     * sets up the layout, and configures the action bar with a back button.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down, this Bundle contains the data it most recently supplied.
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Add functionality to manage settings
    }
}
