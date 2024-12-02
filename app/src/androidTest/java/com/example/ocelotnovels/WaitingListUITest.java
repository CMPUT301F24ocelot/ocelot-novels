package com.example.ocelotnovels;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WaitingListUITest {

    @Rule
    public ActivityScenarioRule<WaitingListActivity> activityScenarioRule =
            new ActivityScenarioRule<>(WaitingListActivity.class);

    @Test
    public void testRecyclerViewDisplayed() {
        // Check if the RecyclerView is displayed
        onView(withId(R.id.recyclerView_waitinglist_entrants))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testRecyclerViewItemContent() {
        // Assuming that the item at position 0 is loaded and its data is as expected
        String expectedFirstName = "John";
        String expectedLastName = "Doe";
        onView(withId(R.id.recyclerView_waitinglist_entrants))
                .perform(scrollToPosition(0))
                .check(matches(hasDescendant(withText(expectedFirstName + " " + expectedLastName))));
    }

    @Test
    public void testBackButtonFunctionality() {
        // Check if pressing back from this activity works correctly
        pressBack();
    }
}
