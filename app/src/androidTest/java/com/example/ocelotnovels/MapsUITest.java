package com.example.ocelotnovels;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.Manifest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapsUITest {

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(
                    Manifest.permission.ACCESS_FINE_LOCATION
            );

    @Before
    public void setUp() {
        // Launch the MapsActivity
        ActivityScenario.launch(MapsActivity.class);
    }

    @Test
    public void testMapIsDisplayed() {
        // Verify that the map container is displayed
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void testMarkersAndZoom() {
        // Simulate adding markers and adjusting the zoom level
        ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class);

        scenario.onActivity(activity -> {
            GoogleMap googleMap = activity.getMapInstance(); // Create a method in MapsActivity to return the GoogleMap instance
            if (googleMap != null) {
                activity.runOnUiThread(() -> {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(37.7749, -122.4194)) // San Francisco
                            .title("User A"));

                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(49.2827, -123.1207)) // Vancouver
                            .title("User B"));

                    googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                            new LatLng(43.0, -120.0), 5 // Center and zoom to fit the markers
                    ));
                });
            }
        });

        // Verify that the map is still displayed
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }
}
