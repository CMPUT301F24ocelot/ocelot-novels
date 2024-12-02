package com.example.ocelotnovels;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.test.core.app.ApplicationProvider;

import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {30},manifest = "src/main/AndroidManifest.xml")
public class SignUpActivityTest {

    @Mock
    FirebaseFirestore mockFirestore;

    @Mock
    CollectionReference mockUsersCollection;

    @Mock
    DocumentReference mockUserDocument;

    @Mock
    Task<Void> mockTask;

    @Captor
    ArgumentCaptor<OnCompleteListener<Void>> onCompleteListenerCaptor;

    private SignUpActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock FirebaseUtils
        FirebaseUtils mockFirebaseUtils = mock(FirebaseUtils.class);
        when(mockFirebaseUtils.getDeviceId(any())).thenReturn("testDeviceId");

        // Initialize the activity
        activity = Robolectric.buildActivity(SignUpActivity.class).create().get();

        // Set the private fields using reflection
        setPrivateField(activity, "db", mockFirestore);
        setPrivateField(activity, "firebaseUtils", mockFirebaseUtils);
        setPrivateField(activity, "deviceId", "testDeviceId");

        // Mock Firestore structure
        when(mockFirestore.collection("users")).thenReturn(mockUsersCollection);
        when(mockUsersCollection.document("testDeviceId")).thenReturn(mockUserDocument);
        when(mockUserDocument.set(any())).thenReturn(mockTask);
    }

    @Test
    public void testEmptyEmailAndName() {
        // Set empty email and name
        EditText emailEditText = activity.findViewById(R.id.editTextEmail);
        EditText nameEditText = activity.findViewById(R.id.editTextName);
        Button signUpButton = activity.findViewById(R.id.buttonSignUp);

        emailEditText.setText("");
        nameEditText.setText("");

        // Click the sign-up button
        signUpButton.performClick();

        // Run pending tasks
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify that a Toast is shown
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertEquals("Please fill in all required fields", latestToast);
    }

    @Test
    public void testValidSignUpWithoutPhone() {
        // Set valid email and name without phone number
        EditText emailEditText = activity.findViewById(R.id.editTextEmail);
        EditText nameEditText = activity.findViewById(R.id.editTextName);
        EditText phoneEditText = activity.findViewById(R.id.editPhoneNum);
        Button signUpButton = activity.findViewById(R.id.buttonSignUp);

        emailEditText.setText("test@example.com");
        nameEditText.setText("John Doe");
        phoneEditText.setText("");

        // Click the sign-up button
        signUpButton.performClick();

        // Run pending tasks
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Capture the data passed to Firestore
        ArgumentCaptor<Map<String, Object>> userDataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockUserDocument).set(userDataCaptor.capture());
        Map<String, Object> userData = userDataCaptor.getValue();

        // Verify user data
        assertEquals("John Doe", userData.get("name"));
        assertEquals("test@example.com", userData.get("email"));
        assertNull(userData.get("phone"));
        assertEquals("entrant", userData.get("role"));
        assertEquals(true, userData.get("notificationsEnabled"));
        assertNotNull(userData.get("eventsJoined"));

        // Simulate successful Firestore operation
        when(mockTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockTask;
        });

        // Run pending tasks
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify that a Toast is shown
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertEquals("Successfully signed up!", latestToast);

        // Verify that the MainActivity is started
        Intent expectedIntent = new Intent(activity, MainActivity.class);
        Intent actualIntent = Shadows.shadowOf(activity).getNextStartedActivity();
        assertTrue(actualIntent.filterEquals(expectedIntent));
    }

    @Test
    public void testValidSignUpWithPhone() {
        // Set valid email, name, and phone number
        EditText emailEditText = activity.findViewById(R.id.editTextEmail);
        EditText nameEditText = activity.findViewById(R.id.editTextName);
        EditText phoneEditText = activity.findViewById(R.id.editPhoneNum);
        Button signUpButton = activity.findViewById(R.id.buttonSignUp);

        emailEditText.setText("test@example.com");
        nameEditText.setText("Jane Smith");
        phoneEditText.setText("1234567890");

        // Click the sign-up button
        signUpButton.performClick();

        // Run pending tasks
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Capture the data passed to Firestore
        ArgumentCaptor<Map<String, Object>> userDataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockUserDocument).set(userDataCaptor.capture());
        Map<String, Object> userData = userDataCaptor.getValue();

        // Verify user data
        assertEquals("Jane Smith", userData.get("name"));
        assertEquals("test@example.com", userData.get("email"));
        assertEquals("1234567890", userData.get("phone"));
        assertEquals("entrant", userData.get("role"));
        assertEquals(true, userData.get("notificationsEnabled"));
        assertNotNull(userData.get("eventsJoined"));

        // Simulate successful Firestore operation
        when(mockTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> listener = invocation.getArgument(0);
            listener.onSuccess(null);
            return mockTask;
        });

        // Run pending tasks
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify that a Toast is shown
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertEquals("Successfully signed up!", latestToast);

        // Verify that the MainActivity is started
        Intent expectedIntent = new Intent(activity, MainActivity.class);
        Intent actualIntent = Shadows.shadowOf(activity).getNextStartedActivity();
        assertTrue(actualIntent.filterEquals(expectedIntent));
    }

    @Test
    public void testSignUpFailure() {
        // Set valid email and name
        EditText emailEditText = activity.findViewById(R.id.editTextEmail);
        EditText nameEditText = activity.findViewById(R.id.editTextName);
        Button signUpButton = activity.findViewById(R.id.buttonSignUp);

        emailEditText.setText("test@example.com");
        nameEditText.setText("John Doe");

        // Click the sign-up button
        signUpButton.performClick();

        // Run pending tasks
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Capture the data passed to Firestore
        verify(mockUserDocument).set(any());

        // Simulate Firestore failure
        when(mockTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            OnFailureListener listener = invocation.getArgument(0);
            listener.onFailure(new Exception("Firestore error"));
            return mockTask;
        });

        // Run pending tasks
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify that a Toast is shown with the error message
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertEquals("Error: Firestore error", latestToast);
    }

    // Helper method to set private fields using reflection
    private void setPrivateField(Object object, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to set private field '" + fieldName + "'");
        }
    }
}
