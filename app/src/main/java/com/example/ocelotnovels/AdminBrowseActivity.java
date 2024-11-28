package com.example.ocelotnovels;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is the class used to create the activity for the Admin(s) to browse the all of the different things in the app
 */
public class AdminBrowseActivity extends AppCompatActivity {

    /**
     * When there is a new instance of this activity it will call this method with its creation
     * @param savedInstance
     */
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.admin_browse_layout);

    }
}
