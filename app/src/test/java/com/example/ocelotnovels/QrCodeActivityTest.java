package com.example.ocelotnovels;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.example.ocelotnovels.view.Organizer.OrganizerMainActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {32}, manifest = "src/main/AndroidManifest.xml")
public class QrCodeActivityTest {

    /**
     * Utility method to convert a Bitmap to byte array.
     */
    private byte[] bitmapToByteArray(Bitmap bitmap) {
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
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
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), QrCodeActivity.class);
        intent.putExtra("eventTitle", eventTitle);
        intent.putExtra("eventDescription", eventDescription);
        intent.putExtra("eventDeadline", eventDeadline);
        intent.putExtra("eventLocation", eventLocation);
        intent.putExtra("qrCode", qrCodeBytes);

        // Start the activity with the intent
        QrCodeActivity activity = Robolectric.buildActivity(QrCodeActivity.class, intent).create().get();

        // Find views
        TextView titleTextView = activity.findViewById(R.id.event_title);
        TextView descriptionTextView = activity.findViewById(R.id.event_description);
        TextView deadlineTextView = activity.findViewById(R.id.event_deadline);
        TextView locationTextView = activity.findViewById(R.id.event_status);
        ImageView qrImageView = activity.findViewById(R.id.event_qr_code_image);

        // Verify that the TextViews display the correct text
        assertEquals("Title: " + eventTitle, titleTextView.getText().toString());
        assertEquals("Description: " + eventDescription, descriptionTextView.getText().toString());
        assertEquals("Deadline: " + eventDeadline, deadlineTextView.getText().toString());
        assertEquals("Location: " + eventLocation, locationTextView.getText().toString());

        // Verify that the ImageView displays the correct bitmap
        ImageView imageView = activity.findViewById(R.id.event_qr_code_image);
        assertNotNull("ImageView should not be null", imageView);
        assertNotNull("ImageView drawable should not be null", imageView.getDrawable());

        // Since comparing Bitmaps directly can be unreliable, check dimensions as a basic test
        Bitmap displayedBitmap = ((android.graphics.drawable.BitmapDrawable) imageView.getDrawable()).getBitmap();
        assertEquals("Bitmap width should match", testQrBitmap.getWidth(), displayedBitmap.getWidth());
        assertEquals("Bitmap height should match", testQrBitmap.getHeight(), displayedBitmap.getHeight());
    }
}
