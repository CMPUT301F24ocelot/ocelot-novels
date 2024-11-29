package com.example.ocelotnovels;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This is the class used to create the activity for the Admin(s) to browse the all of the different things in the app
 */
public class AdminBrowseActivity extends AppCompatActivity {
    private TextView results;
    private ListView resultsList;
    private AutoCompleteTextView dropDownMenu;

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
    }

    private void initializeView(){
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        dropDownMenu = findViewById(R.id.drop_down);
        dropDownAdapter = new ArrayAdapter<String>(this,R.layout.admin_dropdown,options);
        dropDownMenu.setAdapter(dropDownAdapter);
        dropDownMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed(); // This navigates back to the parent activity.
        return true;
    }
}
