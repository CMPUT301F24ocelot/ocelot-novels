package com.example.ocelotnovels;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.ocelotnovels.view.Organizer.OrganizerMainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBackUnconditionally;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class QrCodeActivityUITest {

    @Before
    public void setUp() {
        // Initialize Espresso Intents
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Espresso Intents
        Intents.release();
    }

    @Test
    public void testQrCodeActivityDisplaysCorrectData() {
        // Prepare test data
        String eventTitle = "Test Event";
        String eventDescription = "This is a test event description.";
        String eventDeadline = "2023-10-15";
        String eventLocation = "Test Location";

        // Create a sample QR code bitmap and convert to byte[]
        Bitmap testQrBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        byte[] qrCodeBytes = bitmapToByteArray(testQrBitmap);

        // Create an Intent with the test data
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, QrCodeActivity.class);
        intent.putExtra("eventTitle", eventTitle);
        intent.putExtra("eventDescription", eventDescription);
        intent.putExtra("eventDeadline", eventDeadline);
        intent.putExtra("eventLocation", eventLocation);
        intent.putExtra("qrCode", qrCodeBytes);

        // Launch the activity with the intent
        ActivityScenario.launch(intent);

        // Verify that the TextViews display the correct text
        onView(withId(R.id.event_title)).check(matches(withText("Title: " + eventTitle)));
        onView(withId(R.id.event_description)).check(matches(withText("Description: " + eventDescription)));
        onView(withId(R.id.event_deadline)).check(matches(withText("Deadline: " + eventDeadline)));
        onView(withId(R.id.event_status)).check(matches(withText("Location: " + eventLocation)));

        // Verify that the QR code ImageView is displayed and has a drawable
        onView(withId(R.id.event_qr_code_image)).check(matches(isDisplayed()));
        onView(withId(R.id.event_qr_code_image)).check(matches(not(hasNoDrawable())));
    }

    /**
     * Utility method to convert a Bitmap to byte array.
     */
    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    // Define a custom matcher to check if an ImageView has no drawable
    public static Matcher<View> hasNoDrawable() {
        return new TypeSafeMatcher<View>() {

            @Override
            protected boolean matchesSafely(View item) {
                if (!(item instanceof ImageView)) {
                    return true; // Return true if not an ImageView to avoid failing the test
                }
                ImageView imageView = (ImageView) item;
                return imageView.getDrawable() == null;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("ImageView should have a drawable");
            }
        };
    }
}
