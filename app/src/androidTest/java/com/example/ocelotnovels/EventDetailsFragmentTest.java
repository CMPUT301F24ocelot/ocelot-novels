package com.example.ocelotnovels;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.ocelotnovels.view.Entrant.EventDetailsFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class EventDetailsFragmentTest {

    /**
     * HostFragment is an inner static class used to host the EventDetailsFragment
     * as a DialogFragment within the test environment.
     */
    public static class HostFragment extends Fragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Retrieve arguments passed to the HostFragment
            Bundle args = getArguments();
            String eventId = args != null ? args.getString("eventId") : null;
            String userId = args != null ? args.getString("userId") : null;

            // Create an instance of EventDetailsFragment with the provided arguments
            EventDetailsFragment dialogFragment = EventDetailsFragment.newInstance(eventId, userId);

            // Show the DialogFragment
            dialogFragment.show(getParentFragmentManager(), "EventDetailsFragment");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Return an empty view as this fragment's sole purpose is to host the DialogFragment
            return new View(getContext());
        }
    }

    @Before
    public void setUp() {
        // Initialize any required setup before tests
        // For this basic test, no setup is required
    }

    @After
    public void tearDown() {
        // Clean up after tests if necessary
        // For this basic test, no teardown is required
    }

    /**
     * Test to verify that all required UI components in the EventDetailsFragment are displayed.
     */
    @Test
    public void testUIVisibility() {
        // Prepare arguments to pass to the HostFragment
        Bundle args = new Bundle();
        args.putString("eventId", "515e4f51-1728-495e-8437-0b6862263b23");
        args.putString("userId", "testUserId");

        // Launch the HostFragment with the specified arguments
        FragmentScenario<HostFragment> scenario = FragmentScenario.launchInContainer(
                HostFragment.class,
                args,
                R.style.Theme_OcelotNovels, // Replace with your actual app theme
                Lifecycle.State.RESUMED
        );

        // Optionally, verify that the HostFragment and DialogFragment are not null
        scenario.onFragment(fragment -> {
            assertNotNull(fragment);
            DialogFragment dialogFragment = (DialogFragment) fragment.getParentFragmentManager()
                    .findFragmentByTag("EventDetailsFragment");
            assertNotNull("EventDetailsFragment should be displayed", dialogFragment);
        });

        // Verify that the event title TextView is displayed within the DialogFragment
        Espresso.onView(withId(R.id.user_event_title))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));

        // Verify that the event description TextView is displayed within the DialogFragment
        Espresso.onView(withId(R.id.user_event_description))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));

        // Verify that the registration deadline TextView is displayed within the DialogFragment
        Espresso.onView(withId(R.id.user_event_deadline))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));

        // Verify that the geolocation warning TextView is displayed within the DialogFragment
        Espresso.onView(withId(R.id.warning_text))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));

        // Verify that the event image ImageView is displayed within the DialogFragment
        Espresso.onView(withId(R.id.event_details_poster_image))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));

        // Verify that the positive button (Join) is displayed within the DialogFragment
        Espresso.onView(withText("Join"))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));

        // Verify that the negative button (Cancel) is displayed within the DialogFragment
        Espresso.onView(withText("Cancel"))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
    }
}
