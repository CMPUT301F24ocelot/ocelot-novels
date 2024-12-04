package com.example.ocelotnovels;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.ocelotnovels.model.Entrant;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class WaitingListUITest {

    @Rule
    public ActivityScenarioRule<WaitingListActivity> activityScenarioRule =
            new ActivityScenarioRule<>(WaitingListActivity.class);

    @Test
    public void testRecyclerViewDisplayed() {
        // Verify the RecyclerView is displayed
        onView(withId(R.id.recyclerView_waitinglist_entrants))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testRecyclerViewItemContent() {
        // Mock data for testing
        List<Entrant> mockData = new ArrayList<>();
        mockData.add(new Entrant("John", "Doe", "email@email.com", "123"));
        mockData.add(new Entrant("Jane", "Smith", "email@email.com", "123"));

        // Set mock data in the activity
        activityScenarioRule.getScenario().onActivity(activity -> {
            // Update the entrants list and RecyclerView
            ((WaitingListActivity) activity).setMockData(mockData);
        });

        // Verify the first item in the RecyclerView
        onView(withId(R.id.recyclerView_waitinglist_entrants))
                .perform(scrollToPosition(0)) // Scroll to the first item
                .check(matches(hasDescendant(withText("John Doe"))));

        // Verify the second item in the RecyclerView
        onView(withId(R.id.recyclerView_waitinglist_entrants))
                .perform(scrollToPosition(1)) // Scroll to the second item
                .check(matches(hasDescendant(withText("Jane Smith"))));
    }
}
