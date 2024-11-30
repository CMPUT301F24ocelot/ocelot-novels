package com.example.ocelotnovels;

import android.content.Intent;
import android.view.View;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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

    private OrganizerMainActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = Mockito.spy(new OrganizerMainActivity());
        activity.db = mockDb;
        activity.organizerRecyclerView = mockRecyclerView;
        activity.eventAdapter = mockAdapter;
        activity.facilityId = "sampleFacilityId";  // Mocked facility ID
    }

    @Test
    public void testLoadEventsFromFirestore_Success() {
        // Setup successful response
        when(mockDocumentSnapshot.getString("name")).thenReturn("Event Name");
        when(mockDocumentSnapshot.getString("eventDate")).thenReturn("2023-12-31");
        when(mockDocumentSnapshot.getString("location")).thenReturn("Event Location");
        when(mockQuerySnapshot.getDocuments()).thenReturn(Arrays.asList(mockDocumentSnapshot));
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockQuerySnapshot);
        when(mockDb.collection("events").whereEqualTo("organizerDeviceId", "sampleFacilityId").get()).thenReturn(mockTask);

        doAnswer(invocation -> {
            ((Task<QuerySnapshot>) invocation.getArgument(0)).getResult();
            return null;
        }).when(mockTask).addOnSuccessListener(any());

        activity.loadEventsFromFirestore();

        verify(mockDb).collection("events").whereEqualTo("organizerDeviceId", "sampleFacilityId").get();
        verify(mockAdapter).notifyDataSetChanged();
    }

    @Test
    public void testAddEventButton_Click() {
        Button mockButton = Mockito.mock(Button.class);

        doReturn(mockButton).when(activity).findViewById(R.id.add_events_button);
        ArgumentCaptor<View.OnClickListener> clickListenerCaptor = ArgumentCaptor.forClass(View.OnClickListener.class);

        verify(mockButton).setOnClickListener(clickListenerCaptor.capture());
        View.OnClickListener capturedListener = clickListenerCaptor.getValue();
        capturedListener.onClick(mockButton);

        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(activity).startActivity(intentCaptor.capture());
        Intent capturedIntent = intentCaptor.getValue();
        assertEquals(CreateEventActivity.class.getName(), capturedIntent.getComponent().getClassName());
        assertEquals("sampleFacilityId", capturedIntent.getStringExtra("facilityId"));
    }
}
