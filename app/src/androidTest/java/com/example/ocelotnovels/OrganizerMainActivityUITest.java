package com.example.ocelotnovels;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.ocelotnovels.view.Organizer.OrganizerMainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OrganizerMainActivityUITest {
    @Rule
    public ActivityScenarioRule<OrganizerMainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(OrganizerMainActivity.class);

    @Before
    public void setUp() {
        Intents.init(); // Initialize Espresso Intents before each test
    }

    @After
    public void tearDown() {
        Intents.release(); // Clean up Espresso Intents after each test
    }

    @Test
    public void testAddEventButtonNavigation() {
        // Check if the "Add Event" button is displayed and clickable
        onView(withId(R.id.add_events_button))
                .check(matches(isDisplayed()))
                .perform(click());

        // Verify that clicking the "Add Event" button navigates to the CreateEventActivity
        intended(hasComponent(CreateEventActivity.class.getName()));
    }

    @Test
    public void testEntrantMapButtonNavigation() {
        // Check if the "Entrant Map" button is displayed and clickable
        onView(withId(R.id.entrant_map))
                .check(matches(isDisplayed()))
                .perform(click());

        // Verify that clicking the "Entrant Map" button navigates to the MapsActivity
        intended(hasComponent(MapsActivity.class.getName()));
    }

    @Test
    public void testRecyclerViewVisibility() {
        // Verify that the RecyclerView for displaying events is visible
        onView(withId(R.id.OrganizerRecyclerView))
                .check(matches(isDisplayed()));
    }
}
