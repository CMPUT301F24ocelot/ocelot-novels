package com.example.ocelotnovels;

import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MapsActivityUITest {

    // Automatically grant fine location permission to avoid manual permission handling
    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void testMapAndMarkersLoadCorrectly() {
        // Start the MapsActivity
        try (ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class)) {

            // Check if the map is displayed
            onView(withId(R.id.map)).check(matches(isDisplayed()));

            // Optionally, check for the visibility of any UI elements like a ProgressBar or buttons
            onView(withId(R.id.progressBar)).check(matches(isDisplayed()));

        }
    }

    @Test
    public void testBackButtonFunctionality() {
        // Launch MapsActivity
        try (ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class)) {

            // Simulate the back button press
            onView(withId(android.R.id.home)).perform(click());

            // Verify if the activity is closed or another activity is opened
            // This might require intent verification or checking the activity state if using a more complex navigation logic
        }
    }

    @Test
    public void testActionBarTitle() {
        try (ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class)) {
            scenario.onActivity(activity -> {
                ActionBar actionBar = activity.getSupportActionBar();  // Get the support action bar
                assertNotNull("Action bar should not be null", actionBar);
                assertTrue("Action bar should be displayed", actionBar.isShowing());
                assertEquals("Expected action bar title", "Expected Title", actionBar.getTitle().toString());
            });
        }
    }
}
