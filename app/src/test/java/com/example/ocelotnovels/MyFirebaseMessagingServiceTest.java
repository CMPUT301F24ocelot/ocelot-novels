package com.example.ocelotnovels;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationManagerCompat;

import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.RemoteMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowNotificationManager;

import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {30})
public class MyFirebaseMessagingServiceTest {

    @Mock
    FirebaseFirestore mockFirestore;

    @Mock
    FirebaseUtils mockFirebaseUtils;

    @Mock
    DocumentReference mockUserDocument;

    @Mock
    Task<Void> mockTask;

    @Captor
    ArgumentCaptor<Map<String, Object>> mapCaptor;

    private MyFirebaseMessagingService service;
    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize the service
        service = spy(new MyFirebaseMessagingService());

        // Get the application context
        context = RuntimeEnvironment.getApplication();

        // Mock FirebaseUtils to return a test device ID
        when(mockFirebaseUtils.getDeviceId(any(Context.class))).thenReturn("testDeviceId");

        // Mock Firestore interactions
        CollectionReference mockUsersCollection = mock(CollectionReference.class);
        when(mockFirestore.collection("users")).thenReturn(mockUsersCollection);
        when(mockUsersCollection.document("testDeviceId")).thenReturn(mockUserDocument);
        when(mockUserDocument.update(anyMap())).thenReturn(mockTask);

        // Inject mocks into the service using reflection
        injectPrivateField(service, "firebaseUtils", mockFirebaseUtils);
        injectPrivateField(service, "db", mockFirestore);

        // Mock getApplicationContext()
        doReturn(context).when(service).getApplicationContext();

        // Mock NotificationManager
        NotificationManager mockNotificationManager = mock(NotificationManager.class);
        doReturn(mockNotificationManager).when(service).getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Test
    public void testOnNewToken() {
        String testToken = "testToken";

        // Call the method under test
        service.onNewToken(testToken);

        // Verify that Firestore update is called with correct parameters
        Map<String, Object> expectedData = new HashMap<>();
        expectedData.put("fcmToken", testToken);

        verify(mockUserDocument).update(expectedData);
    }

    @Test
    public void testOnMessageReceived_withNotification() {
        // Mock RemoteMessage.Notification
        RemoteMessage.Notification mockNotification = mock(RemoteMessage.Notification.class);
        when(mockNotification.getTitle()).thenReturn("Test Title");
        when(mockNotification.getBody()).thenReturn("Test Body");

        // Mock RemoteMessage
        RemoteMessage mockMessage = mock(RemoteMessage.class);
        when(mockMessage.getNotification()).thenReturn(mockNotification);
        when(mockMessage.getData()).thenReturn(new HashMap<>());

        // Call the method under test
        service.onMessageReceived(mockMessage);

        // Verify that getNotification() was called on the RemoteMessage
        verify(mockMessage).getNotification();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        verify(notificationManager).notify(anyInt(), any(Notification.class));
    }

    @Test
    public void testOnMessageReceived_withDataPayload() {
        // Create a data payload
        Map<String, String> dataPayload = new HashMap<>();
        dataPayload.put("eventId", "12345");
        dataPayload.put("message", "Test Message");

        // Mock RemoteMessage
        RemoteMessage mockMessage = mock(RemoteMessage.class);
        when(mockMessage.getNotification()).thenReturn(null);
        when(mockMessage.getData()).thenReturn(dataPayload);

        // Spy on the service to verify internal method calls
        MyFirebaseMessagingService spyService = spy(service);

        // Call the method under test
        spyService.onMessageReceived(mockMessage);
    }

    // Helper method to inject private fields using reflection
    private void injectPrivateField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = MyFirebaseMessagingService.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to inject private field '" + fieldName + "'");
        }
    }
}
