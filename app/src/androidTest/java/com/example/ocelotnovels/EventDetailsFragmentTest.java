package com.example.ocelotnovels;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNotNull;

import com.example.ocelotnovels.view.Entrant.EventDetailsFragment;

@RunWith(AndroidJUnit4.class)
public class EventDetailsFragmentTest {

    @Test
    public void testEventDetailsFragmentUI() {
        // Prepare mock arguments for the EventDetailsFragment
        Bundle args = new Bundle();
        args.putString("eventId", "mockEventId");
        args.putString("userId", "mockUserId");

        // Launch the EventDetailsFragment
        FragmentScenario<EventDetailsFragment> scenario = FragmentScenario.launchInContainer(
                EventDetailsFragment.class,
                args,
                R.style.Theme_OcelotNovels // Replace with your app's theme
        );

        // Ensure the fragment's view hierarchy is ready
        scenario.onFragment(fragment -> {
            assertNotNull("Fragment view should not be null", fragment.getView());
        });

        // Verify the UI elements
        onView(withId(R.id.user_event_title))
                .check(matches(isDisplayed()));
        onView(withId(R.id.user_event_description))
                .check(matches(isDisplayed()));
        onView(withId(R.id.user_event_deadline))
                .check(matches(isDisplayed()));
    }


}
