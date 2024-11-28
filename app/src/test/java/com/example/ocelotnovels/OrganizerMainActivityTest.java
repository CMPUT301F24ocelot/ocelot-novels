package com.example.ocelotnovels;

import android.content.Intent;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class OrganizerMainActivityTest {
    @Mock
    private FirebaseFirestore mockDb;
    @Mock
    private Task<QuerySnapshot> mockTask;
    @Mock
    private QuerySnapshot mockQuerySnapshot;
    @Mock
    private RecyclerView mockRecyclerView;
    @Mock
    private OrganizerEventAdapter mockAdapter;
    @Mock
    private QueryDocumentSnapshot mockDocumentSnapshot;
    @InjectMocks
    OrganizerMainActivity activity;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Prepare the activity with mocked Firestore instance
        activity = new OrganizerMainActivity();
        activity.db = mockDb;
        activity.organizerRecyclerView = mockRecyclerView;
        activity.eventAdapter = mockAdapter;

        // Assume that the event names are managed within the adapter
        when(mockRecyclerView.getAdapter()).thenReturn(mockAdapter);
    }

    @Test
    public void testLoadEventsFromFirestore_Success() {
        // Setup successful response
        when(mockDocumentSnapshot.getString("name")).thenReturn("Event Name");
        when(mockQuerySnapshot.getDocuments()).thenReturn(Arrays.asList(mockDocumentSnapshot));
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockQuerySnapshot);

        doAnswer(invocation -> {
            Task<QuerySnapshot> task = invocation.getArgument(0);
            task.getResult();
            return null;
        }).when(mockTask).addOnSuccessListener(any());

        // Execute the method under test
        activity.loadEventsFromFirestore();

        // Verify interactions and check the result
        verify(mockDb).collection("events").get();
        verify(activity.eventAdapter).notifyDataSetChanged();
        assertTrue(activity.eventNames.contains("Event Name"));
    }

    @Test
    public void testLoadEventsFromFirestore_Failure() {
        // Setup failure response
        when(mockTask.isSuccessful()).thenReturn(false);

        doAnswer(invocation -> {
            Task<QuerySnapshot> task = invocation.getArgument(0);
            task.getResult();
            return null;
        }).when(mockTask).addOnFailureListener(any());

        // Execute the method under test
        activity.loadEventsFromFirestore();

        // Verify interactions and ensure no data was added on failure
        verify(mockDb).collection("events").get();
        verify(activity.eventAdapter, never()).notifyDataSetChanged();
        assertTrue(activity.eventNames.isEmpty());
    }

    @Test
    public void testAddEventButton_Click() {
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);

        // Use the injected activity to perform click
        activity.findViewById(R.id.add_events_button).performClick();

        // Verify that the correct activity is started
        verify(activity).startActivity(intentCaptor.capture());
        assertEquals(CreateEventActivity.class.getName(), intentCaptor.getValue().getComponent().getClassName());
    }


    @Test
    public void testEntrantListButton_Click() {
        // Create a spy of the actual activity to monitor its interactions
        OrganizerMainActivity spyActivity = spy(new OrganizerMainActivity());

        // Assume the findViewById correctly initializes the button
        Button mockButton = mock(Button.class);
        when(spyActivity.findViewById(R.id.entrant_list)).thenReturn(mockButton);

        // Simulate button click that should trigger the showEntrantListDropdown method
        spyActivity.findViewById(R.id.entrant_list).performClick();

        verify(mockButton).performClick();
    }

}
