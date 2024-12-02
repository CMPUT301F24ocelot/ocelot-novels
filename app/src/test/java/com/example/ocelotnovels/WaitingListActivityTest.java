package com.example.ocelotnovels;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ocelotnovels.model.Entrant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.stubbing.Answer;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {30})
public class WaitingListActivityTest {

    @Mock
    FirebaseFirestore mockFirestore;

    @Mock
    CollectionReference mockEventsCollection;

    @Mock
    DocumentReference mockEventDocument;

    @Mock
    CollectionReference mockWaitingListCollection;

    @Mock
    Task<QuerySnapshot> mockTask;

    @Mock
    QuerySnapshot mockQuerySnapshot;

    @Captor
    ArgumentCaptor<OnCompleteListener<QuerySnapshot>> onCompleteListenerCaptor;

    private WaitingListActivity activity;
    private MockedStatic<FirebaseFirestore> mockedStaticFirestore;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock static method FirebaseFirestore.getInstance()
        mockedStaticFirestore = Mockito.mockStatic(FirebaseFirestore.class);
        mockedStaticFirestore.when(FirebaseFirestore::getInstance).thenReturn(mockFirestore);

        // Set up the mock Firestore structure
        when(mockFirestore.collection("events")).thenReturn(mockEventsCollection);
        when(mockEventsCollection.document("nwsEG9KZHYrTTAN9azIH")).thenReturn(mockEventDocument);
        when(mockEventDocument.collection("WaitingList")).thenReturn(mockWaitingListCollection);
        when(mockWaitingListCollection.get()).thenReturn(mockTask);

        // Prepare mock data
        Entrant entrant1 = new Entrant("Alice", "Smith", "alice@example.com", "device123");
        Entrant entrant2 = new Entrant("Bob", "Johnson", "bob@example.com", "device456");

        List<DocumentSnapshot> mockDocuments = new ArrayList<>();
        QueryDocumentSnapshot mockDoc1 = mock(QueryDocumentSnapshot.class);
        QueryDocumentSnapshot mockDoc2 = mock(QueryDocumentSnapshot.class);

        when(mockDoc1.toObject(Entrant.class)).thenReturn(entrant1);
        when(mockDoc2.toObject(Entrant.class)).thenReturn(entrant2);

        mockDocuments.add(mockDoc1);
        mockDocuments.add(mockDoc2);

        when(mockQuerySnapshot.getDocuments()).thenReturn(mockDocuments);

        // Simulate the behavior of the mockTask
        doAnswer(invocation -> {
            OnCompleteListener<QuerySnapshot> listener = invocation.getArgument(0);
            // Simulate successful task
            when(mockTask.isSuccessful()).thenReturn(true);
            when(mockTask.getResult()).thenReturn(mockQuerySnapshot);
            listener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));
    }

    @After
    public void tearDown() {
        // Close the mocked static FirebaseFirestore
        mockedStaticFirestore.close();
    }

    @Test
    public void testLoadWaitingListSuccess() {
        // Initialize the activity
        activity = Robolectric.buildActivity(WaitingListActivity.class).create().start().resume().get();

        // Wait for UI thread tasks to complete
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify that RecyclerView is updated
        RecyclerView recyclerView = activity.findViewById(R.id.recyclerView_waitinglist_entrants);
        assertNotNull(recyclerView.getAdapter());
        assertEquals(2, recyclerView.getAdapter().getItemCount());

        // Verify the content of the RecyclerView items
        String name0 = getRecyclerViewItemText(recyclerView, 0, R.id.tv_username);
        assertEquals("Alice Smith", name0);

        String name1 = getRecyclerViewItemText(recyclerView, 1, R.id.tv_username);
        assertEquals("Bob Johnson", name1);
    }

    @Test
    public void testLoadWaitingListFailure() {
        // Adjust the behavior to simulate a failure
        doAnswer(invocation -> {
            OnCompleteListener<QuerySnapshot> listener = invocation.getArgument(0);
            // Simulate failed task
            when(mockTask.isSuccessful()).thenReturn(false);
            when(mockTask.getException()).thenReturn(new Exception("Firestore error"));
            listener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        // Initialize the activity
        activity = Robolectric.buildActivity(WaitingListActivity.class).create().start().resume().get();

        // Wait for UI thread tasks to complete
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        // Verify that the RecyclerView adapter has zero items
        RecyclerView recyclerView = activity.findViewById(R.id.recyclerView_waitinglist_entrants);
        assertNotNull(recyclerView.getAdapter());
        assertEquals(0, recyclerView.getAdapter().getItemCount());

        // Verify that a Toast was shown with the correct message
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertEquals("Error loading list", latestToast);
    }

    // Helper method to get text from RecyclerView item
    private String getRecyclerViewItemText(RecyclerView recyclerView, int position, int textViewId) {
        RecyclerView.ViewHolder viewHolder = recyclerView.getAdapter().createViewHolder(recyclerView, recyclerView.getAdapter().getItemViewType(position));
        recyclerView.getAdapter().bindViewHolder(viewHolder, position);
        TextView textView = viewHolder.itemView.findViewById(textViewId);
        return textView.getText().toString();
    }
}
