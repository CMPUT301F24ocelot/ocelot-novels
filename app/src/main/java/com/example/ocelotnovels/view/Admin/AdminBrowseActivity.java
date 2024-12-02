/**
 * This class represents the activity for the Admin to browse various entities in the application,
 * such as user profiles, events, facilities, and images. The Admin can select an option from a
 * dropdown menu to view the desired data, and the activity dynamically updates the displayed
 * information based on the selection. The data is fetched from the Firebase Firestore database,
 * and the interface provides intuitive navigation for the Admin. The activity also includes error
 * handling for Firebase initialization and ensures data is presented effectively through adapters.
 */

package com.example.ocelotnovels.view.Admin;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Event;
import com.example.ocelotnovels.model.Facility;
import com.example.ocelotnovels.model.Image;
import com.example.ocelotnovels.model.User;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * This is the class used to create the activity for the Admin(s) to browse the all of the different things in the app
 */
public class AdminBrowseActivity extends AppCompatActivity {
    private TextView results;
    private ListView resultsList;
    private Button detailsButton;
    private AutoCompleteTextView dropDownMenu;
    private FirebaseUtils firebaseUtils;

    ArrayAdapter<User> profilesAdapter;//Will only be used if the admin wants to look at the profiles
    ArrayList<User> profiles = new ArrayList<User>();

    ArrayAdapter<Event> eventsAdapter;//Will only be used if the admin wants to browse the events
    ArrayList<Event> events = new ArrayList<Event>();

    ArrayAdapter<Facility> facilitiesAdapter;//Will only be used if the admin wants to browse the facilities
    ArrayList<Facility> facilities = new ArrayList<Facility>();

    ArrayAdapter<Image> imageAdapter;//Will only be used if the admin wants to browse the facilities
    ArrayList<Image> images = new ArrayList<Image>();

    String[] options = {"Profiles", "Events", "Facilities", "Images"};
    ArrayAdapter<String> dropDownAdapter;

    /**
     * When there is a new instance of this activity it will call this method with its creation
     * @param savedInstance
     */
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.admin_browse_layout);
        Bundle item = getIntent().getExtras();

        initializeView();

        initializeFirebase();

        if(item != null){
            String from = item.getString("from");
            if(from.equals("Profiles")){
                loadProfiles();
            } else if (from.equals("Events")) {
                loadEvents();
            } else if (from.equals("Facilities")){
                loadFacilities();
            }
        }

    }

    /**
     * This gets all of the resources from the view and creates variables that can be accessed later from them
     */
    private void initializeView(){
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        results = findViewById(R.id.current_list);
        resultsList = findViewById(R.id.list);


        dropDownMenu = findViewById(R.id.drop_down);
        dropDownAdapter = new ArrayAdapter<String>(this,R.layout.admin_dropdown,options);
        dropDownMenu.setAdapter(dropDownAdapter);
        dropDownMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                if(item.equals("Profiles")){
                    loadProfiles();
                } else if (item.equals("Events")) {
                    loadEvents();
                } else if (item.equals("Facilities")) {
                    loadFacilities();
                } else if (item.equals("Images")){
                    loadAllImages();
                }
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
     * Handles navigation back to the parent activity.
     *
     * @return true if navigation is successful.
     */
    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed(); // This navigates back to the parent activity.
        return true;
    }

    /**
     * This will load all of the profiles for the admin to be able to browse
     */
    private void loadProfiles(){
        results.setText("Results: Profiles");
        profiles.clear();
        profilesAdapter = new ProfileAdapterAdmin(this, profiles);
        firebaseUtils.getAllUsers(this,profilesAdapter,profiles);
        resultsList.setAdapter(profilesAdapter);
    }

    /**
     * This will load all of the events for the admin to be able to browse
     */
    private void loadEvents(){
        results.setText("Results: Events");
        eventsAdapter = new EventAdapterAdmin(this, events);
        events.clear();
        firebaseUtils.getAllEvents(this,eventsAdapter,events);
        resultsList.setAdapter(eventsAdapter);
    }

    /**
     * This will load all of the facilities for the admin to be able to browse
     */
    private void loadFacilities(){
        results.setText("Results: Facilities");
        facilitiesAdapter = new FacilityAdapterAdmin(this, facilities);
        facilities.clear();
        firebaseUtils.getAllFacilities(this,facilitiesAdapter,facilities);
        resultsList.setAdapter(facilitiesAdapter);
    }

    /**
     * This is a method that will load and show all the images for the admin to browse
     */
    private void loadAllImages(){
        results.setText("Results: Images");
        imageAdapter = new ImageAdapterAdmin(this, images);
        images.clear();
        firebaseUtils.getAllImages(this,imageAdapter, images);
        resultsList.setAdapter(imageAdapter);
    }
}
