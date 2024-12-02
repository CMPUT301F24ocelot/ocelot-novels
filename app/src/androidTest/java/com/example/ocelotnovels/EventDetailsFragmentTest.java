package com.example.ocelotnovels;

import android.os.Bundle;

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

import com.example.ocelotnovels.MockEventDetailsFragment;

@RunWith(AndroidJUnit4.class)
public class EventDetailsFragmentTest {

    @Test
    public void testEventDetailsFragmentUIWithMockData() {
        // Prepare mock arguments for the MockEventDetailsFragment
        Bundle args = new Bundle();
        args.putString("eventId", "mockEventId");
        args.putString("userId", "mockUserId");

        // Launch the MockEventDetailsFragment
        FragmentScenario<MockEventDetailsFragment> scenario = FragmentScenario.launchInContainer(
                MockEventDetailsFragment.class,
                args,
                R.style.Theme_OcelotNovels, // Replace with your app's theme
                Lifecycle.State.RESUMED
        );

        // Ensure the fragment is properly initialized
        scenario.onFragment(fragment -> {
            assertNotNull("Fragment should not be null", fragment);
            assertNotNull("Fragment view should not be null", fragment.getView());
        });

        // Verify the mock data in UI elements
        onView(withId(R.id.user_event_title))
                .check(matches(isDisplayed()))
                .check(matches(withText("Mock Event Title")));

        onView(withId(R.id.user_event_description))
                .check(matches(isDisplayed()))
                .check(matches(withText("Mock Event Description")));

        onView(withId(R.id.user_event_deadline))
                .check(matches(isDisplayed()))
                .check(matches(withText("Mock Deadline: 2023-12-31")));

        onView(withId(R.id.warning_text))
                .check(matches(isDisplayed()));
    }
}
