package com.example.ocelotnovels;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static java.util.regex.Pattern.matches;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.ocelotnovels.view.Admin.AdminBrowseActivity;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminUITests {
    @Rule
    public ActivityScenarioRule<AdminBrowseActivity> scenario = new ActivityScenarioRule<AdminBrowseActivity>(AdminBrowseActivity.class);

    @Test
    public void checkMainBrowser(){
        onView(withText("Hello!")).check(ViewAssertions.matches(isDisplayed()));
        //check that results are there so that you know you are in the browser view
        onView(withText("Results:")).check(ViewAssertions.matches(isDisplayed()));
        //put Profiles into the browser
        onView(withId(R.id.drop_down)).perform(ViewActions.typeText("Profiles"));
        onView(withId(R.id.drop_down)).perform(click());
        //onView(withText("Gareth War")).check(ViewAssertions.matches(isDisplayed()));


    }

}
