// File: src/androidTest/java/com/example/ocelotnovels/view/Admin/EntrantProfileAdminViewTest.java

package com.example.ocelotnovels;

import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.example.ocelotnovels.R;
import com.example.ocelotnovels.model.User;
import com.example.ocelotnovels.view.Admin.EntrantProfileAdminView;

import org.checkerframework.checker.units.qual.C;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Basic instrumented test for EntrantProfileAdminView.
 * This test checks if the activity launches successfully, receives a User object,
 * and initializes essential views correctly.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {32}, manifest = "src/main/AndroidManifest.xml")
public class EntrantProfileAdminViewTest {

    private User testUser;

    @Before
    public void setUp() {
        // Instantiate the User object using its available constructor
        testUser = new User(
                "user123", "Jane", "Doe", "jane.doe@example.com", "0987654321");
    }

    @Test
    public void testEntrantProfileAdminViewInitialization() {
        // Create an Intent with the User object as extra
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EntrantProfileAdminView.class);
        intent.putExtra("User", testUser);

        // Build the activity using Robolectric
        EntrantProfileAdminView activity = Robolectric.buildActivity(EntrantProfileAdminView.class, intent)
                .create()
                .resume()
                .get();

        // Verify that the activity is not null
        assertNotNull("Activity should not be null", activity);

        // Find the views by their IDs
        TextView nameTextView = activity.findViewById(R.id.name);
        TextView emailTextView = activity.findViewById(R.id.email);
        TextView phoneTextView = activity.findViewById(R.id.phone);
        ImageView profileImageView = activity.findViewById(R.id.profileImageView);
        Button deleteButton = activity.findViewById(R.id.delete_button);

        // Verify that the views are not null
        assertNotNull("Name TextView should not be null", nameTextView);
        assertNotNull("Email TextView should not be null", emailTextView);
        assertNotNull("Phone TextView should not be null", phoneTextView);
        assertNotNull("Profile ImageView should not be null", profileImageView);
        assertNotNull("Delete Button should not be null", deleteButton);

        // Verify that the TextViews display the correct data
        String expectedName = "Jane Doe";
        String expectedEmail = "jane.doe@example.com";
        String expectedPhone = "0987654321";

        assertEquals("Name TextView should display correct name", expectedName, nameTextView.getText().toString());
        assertEquals("Email TextView should display correct email", expectedEmail, emailTextView.getText().toString());
        assertEquals("Phone TextView should display correct phone number", expectedPhone, phoneTextView.getText().toString());

        // Note: Verifying ImageView content (e.g., Glide loading) requires more advanced testing.
        // For basic testing, ensure that the ImageView has a drawable set (placeholder in this case)
        assertNotNull("Profile ImageView should have a drawable", profileImageView.getDrawable());

        // Verify that the Delete Button is clickable
        assertTrue("Delete Button should be clickable", deleteButton.isClickable());
    }
}
