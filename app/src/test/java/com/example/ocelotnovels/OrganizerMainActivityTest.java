package com.example.ocelotnovels;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowSystemClock;
import com.example.ocelotnovels.view.Organizer.OrganizerEventAdapter;
import com.example.ocelotnovels.view.Organizer.OrganizerMainActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {32}, manifest = "src/main/AndroidManifest.xml",shadows = {ShadowSystemClock.class})
public class OrganizerMainActivityTest {
    @Mock
    private FirebaseFirestore mockDb;
    @Mock
    private RecyclerView mockRecyclerView;
    @Mock
    private OrganizerEventAdapter mockAdapter;

    private OrganizerMainActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Build the activity with Robolectric
        activity = Robolectric.buildActivity(OrganizerMainActivity.class)
                .create()
                .resume()
                .get();
        activity.db = mockDb;
        activity.organizerRecyclerView = mockRecyclerView;
        activity.eventAdapter = mockAdapter;
        activity.facilityId = "sampleFacilityId";
    }

    @Test
    public void testLoadEventsFromFirestore_Success() {
        Task<QuerySnapshot> mockTask = mock(Task.class);
        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
        QueryDocumentSnapshot mockDocumentSnapshot = mock(QueryDocumentSnapshot.class);

        when(mockDocumentSnapshot.getString("name")).thenReturn("Event Name");
        when(mockDocumentSnapshot.getString("eventDate")).thenReturn("2023-12-31");
        when(mockDocumentSnapshot.getString("location")).thenReturn("Event Location");
        when(mockQuerySnapshot.getDocuments()).thenReturn(Arrays.asList(mockDocumentSnapshot));
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockQuerySnapshot);
        when(mockDb.collection("events").whereEqualTo("organizerDeviceId", "sampleFacilityId").get()).thenReturn(mockTask);

        activity.loadEventsFromFirestore();

        verify(mockDb).collection("events").whereEqualTo("organizerDeviceId", "sampleFacilityId").get();
        verify(mockAdapter).notifyDataSetChanged();
    }

    @Test
    public void testAddEventButton_Click() {
        Button addEventButton = new Button(activity);
        addEventButton.setId(R.id.add_events_button); // Assuming R.id.add_events_button is a valid ID
        activity.setContentView(addEventButton); // Set the button as the content view for the activity

        addEventButton.performClick();

        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(activity).startActivity(intentCaptor.capture());
        Intent capturedIntent = intentCaptor.getValue();

        assertEquals(CreateEventActivity.class.getName(), capturedIntent.getComponent().getClassName());
        assertEquals("sampleFacilityId", capturedIntent.getStringExtra("facilityId"));
    }
}
