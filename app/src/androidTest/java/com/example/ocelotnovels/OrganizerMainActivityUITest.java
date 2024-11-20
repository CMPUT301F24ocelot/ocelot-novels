package com.example.ocelotnovels;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.ocelotnovels.view.organizer.OrganizerMainActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class OrganizerMainActivityUITest {

    private FirebaseFirestore db;

    @Before
    public void setUp() {
        // Initialize Firestore with Emulator
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setHost("10.0.2.2:8080") // For Firebase Emulator on Android Emulator
                .setSslEnabled(false)
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);

        // Set up mock data in Firestore
        setUpMockData();
    }

    private void setUpMockData() {
        Map<String, Object> event1 = new HashMap<>();
        event1.put("eventName", "Sample Event 1");

        Map<String, Object> event2 = new HashMap<>();
        event2.put("eventName", "Sample Event 2");

        db.collection("events").document("event1").set(event1);
        db.collection("events").document("event2").set(event2);
    }

    @Test
    public void testRecyclerViewDisplaysEvents() {
        // Launch OrganizerMainActivity
        Intent intent = new Intent(Intent.ACTION_MAIN);
        try (ActivityScenario<OrganizerMainActivity> scenario = ActivityScenario.launch(OrganizerMainActivity.class)) {

            // Scroll to and check the first event
            Espresso.onView(ViewMatchers.withId(R.id.OrganizerRecyclerView))
                    .perform(RecyclerViewActions.scrollToPosition(0))
                    .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText("Sample Event 1"))));

            // Scroll to and check the second event
            Espresso.onView(ViewMatchers.withId(R.id.OrganizerRecyclerView))
                    .perform(RecyclerViewActions.scrollToPosition(1))
                    .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText("Sample Event 2"))));
        }
    }
}
