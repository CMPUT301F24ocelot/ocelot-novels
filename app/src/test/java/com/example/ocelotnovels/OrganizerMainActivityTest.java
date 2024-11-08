/*
package com.example.ocelotnovels;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.ocelotnovels.view.organizer.OrganizerMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.tasks.Task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

public class OrganizerMainActivityTest {

    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private Task<QuerySnapshot> mockTask;

    @Mock
    private QuerySnapshot mockQuerySnapshot;

    private OrganizerMainActivity organizerMainActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        organizerMainActivity = new OrganizerMainActivity(mockFirestore);

        // Mock Firestore collection and get() method
        when(mockFirestore.collection("events").get()).thenReturn(mockTask);
    }

    @Test
    public void testLoadEventsFromFirestore() {
        // Mock a document with a single event name
        QueryDocumentSnapshot mockDoc = mock(QueryDocumentSnapshot.class);
        when(mockDoc.getString("eventName")).thenReturn("Sample Event");

        // Simulate Firestore success response with one document
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockQuerySnapshot);
        when(mockQuerySnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDoc));

        // Simulate Firestore callback
        doAnswer(invocation -> {
            ((OnCompleteListener<QuerySnapshot>) invocation.getArgument(0)).onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any());

        // Call the method and check the result
        organizerMainActivity.loadEventsFromFirestore();
        assertEquals(1, organizerMainActivity.eventNames.size());
        assertEquals("Sample Event", organizerMainActivity.eventNames.get(0));
    }
}

*/
