package com.example.ocelotnovels;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowLooper;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {32}, manifest = "src/main/AndroidManifest.xml")
public class FacilityProfileActivityTest {

    private FacilityProfileActivity facilityProfileActivity;

    @Mock
    FirebaseFirestore mockDb;

    @Mock
    CollectionReference mockCollection;

    @Mock
    DocumentReference mockDocument;

    private MockedStatic<FirebaseFirestore> mockedFirestore;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Mock FirebaseFirestore.getInstance()
        mockedFirestore = mockStatic(FirebaseFirestore.class);
        mockedFirestore.when(FirebaseFirestore::getInstance).thenReturn(mockDb);

        // Mock Firestore behavior
        when(mockDb.collection("facilities")).thenReturn(mockCollection);
        when(mockCollection.document(anyString())).thenReturn(mockDocument);

        // Build the activity using Robolectric
        facilityProfileActivity = Robolectric.buildActivity(FacilityProfileActivity.class).create().get();
    }

    @After
    public void tearDown() {
        mockedFirestore.close();
    }

    private <T> T getPrivateField(Object object, String fieldName, Class<T> fieldType) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return fieldType.cast(field.get(object));
    }

    @Test
    public void testSaveFacilityProfileWithValidData() throws Exception {
        // Access private fields using reflection
        EditText facilityName = getPrivateField(facilityProfileActivity, "facilityName", EditText.class);
        EditText facilityEmail = getPrivateField(facilityProfileActivity, "facilityEmail", EditText.class);
        EditText facilityPhone = getPrivateField(facilityProfileActivity, "facilityPhone", EditText.class);
        EditText facilityLocation = getPrivateField(facilityProfileActivity, "facilityLocation", EditText.class);
        EditText facilityDescription = getPrivateField(facilityProfileActivity, "facilityDescription", EditText.class);

        // Set valid data
        facilityProfileActivity.runOnUiThread(() -> {
            facilityName.setText("Sample Facility");
            facilityEmail.setText("sample@example.com");
            facilityPhone.setText("1234567890");
            facilityLocation.setText("Sample Location");
            facilityDescription.setText("This is a valid description with more than 20 characters.");
        });

        // Process UI thread tasks
        ShadowLooper.runUiThreadTasks();

        // Invoke saveFacilityProfile
        callSaveFacilityProfileUsingReflection();

        // Verify Firestore interaction
        verify(mockDocument, times(1)).set(anyMap());
    }

    private void callSaveFacilityProfileUsingReflection() {
        try {
            Method method = FacilityProfileActivity.class.getDeclaredMethod("saveFacilityProfile");
            method.setAccessible(true);
            method.invoke(facilityProfileActivity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke method via reflection", e);
        }
    }

    // Validation tests
    private String validateFacilityData(String name, String email, String phone, String location, String description) {
        if (name == null || name.isEmpty() || name.length() > 100) {
            return "Facility name must be between 1 and 100 characters.";
        }

        if (email == null || email.isEmpty() || !email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w+$")) {
            return "Please enter a valid email address.";
        }

        if (phone == null || phone.length() != 10 || !phone.matches("\\d+")) {
            return "Phone number must be 10 digits.";
        }

        if (location == null || location.isEmpty()) {
            return "Location cannot be empty.";
        }

        if (description == null || description.length() < 20) {
            return "Description should be at least 20 characters.";
        }

        return "Valid";
    }

    @Test
    public void testValidData() {
        String result = validateFacilityData(
                "Sample Facility", "sample@example.com", "1234567890", "Sample Location",
                "This is a valid description with more than 20 characters."
        );
        assertEquals("Valid", result);
    }

    @Test
    public void testEmptyName() {
        String result = validateFacilityData(
                "", "sample@example.com", "1234567890", "Sample Location", "Valid description."
        );
        assertEquals("Facility name must be between 1 and 100 characters.", result);
    }

    @Test
    public void testInvalidEmail() {
        String result = validateFacilityData(
                "Sample Facility", "invalid-email", "1234567890", "Sample Location", "Valid description."
        );
        assertEquals("Please enter a valid email address.", result);
    }

    @Test
    public void testInvalidPhoneNumber() {
        String result = validateFacilityData(
                "Sample Facility", "sample@example.com", "12345", "Sample Location", "Valid description."
        );
        assertEquals("Phone number must be 10 digits.", result);
    }

    @Test
    public void testEmptyLocation() {
        String result = validateFacilityData(
                "Sample Facility", "sample@example.com", "1234567890", "", "Valid description."
        );
        assertEquals("Location cannot be empty.", result);
    }

    @Test
    public void testShortDescription() {
        String result = validateFacilityData(
                "Sample Facility", "sample@example.com", "1234567890", "Sample Location", "Too short"
        );
        assertEquals("Description should be at least 20 characters.", result);
    }
}
