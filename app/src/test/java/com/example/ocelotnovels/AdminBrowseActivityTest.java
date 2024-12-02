package com.example.ocelotnovels;

import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.view.Admin.AdminBrowseActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Basic Robolectric test for AdminBrowseActivity.
 * This test checks if the activity initializes correctly and essential views are present.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {32}, manifest = "src/main/AndroidManifest.xml")
public class AdminBrowseActivityTest {

    @Test
    public void testActivityInitialization() {
        // Build the activity
        AdminBrowseActivity activity = Robolectric.buildActivity(AdminBrowseActivity.class)
                .create()
                .start()
                .resume()
                .get();

        // Assert that the activity is not null
        assertNotNull("Activity should not be null", activity);

        // Check if essential views are present
        TextView results = activity.findViewById(R.id.current_list);
        ListView resultsList = activity.findViewById(R.id.list);
        AutoCompleteTextView dropDownMenu = activity.findViewById(R.id.drop_down);

        assertNotNull("Results TextView should not be null", results);
        assertNotNull("ListView should not be null", resultsList);
        assertNotNull("Dropdown Menu should not be null", dropDownMenu);
    }
}