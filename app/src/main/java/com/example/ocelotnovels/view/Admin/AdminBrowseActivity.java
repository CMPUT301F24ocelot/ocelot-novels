package com.example.ocelotnovels.view.Admin;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.Event;
import com.example.ocelotnovels.model.User;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This is the class used to create the activity for the Admin(s) to browse the all of the different things in the app
 */
public class AdminBrowseActivity extends AppCompatActivity {
    private TextView results;
    private ListView resultsList;
    private AutoCompleteTextView dropDownMenu;
    private FirebaseUtils firebaseUtils;

    ArrayAdapter<User> profilesAdapter;//Will only be used if the admin wants to look at the profiles
    ArrayAdapter<Event> eventsAdapter;//Will only be used if the admin wants to browse the events


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

        initializeView();

        initializeFirebase();
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
        ArrayList<User> profiles = new ArrayList<User>();
        profilesAdapter = new ProfileAdapterAdmin(this, profiles);
        firebaseUtils.getDb().collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getString("name");
                        Log.d("Admin", name);
                        String[] nameParts = name.split(" ", 2);
                        String firstName = nameParts[0];
                        String lastName;
                        if(nameParts.length == 2){
                            lastName = nameParts[1];
                        }else{
                            lastName = "nobody";
                        }
                        if(lastName == null || lastName.trim().isEmpty() || lastName.length() > 100){
                            lastName = "nobody";
                        }
                        String email = document.getString("email");
                        String phone = document.getString("phone");
                        User user;
                        if (phone != null && !phone.equals("")) {
                            user = new User(firstName, lastName, email, phone);
                        } else {
                            user = new User(firstName, lastName, email);
                        }
                        profiles.add(user);
                        profilesAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("Admin", "is empty");
                }
            }
        });
        //Log.d("Admin",profiles.get(1).toString());
        resultsList.setAdapter(profilesAdapter);

    }

    /**
     * This will load all of the events for the admin to be able to browse
     */
    private void loadEvents(){
        firebaseUtils.getAllEvent();
    }

    /**
     * This will load all of the facilities for the admin to be able to browse
     */
    private void loadFacilities(){

    }
}
