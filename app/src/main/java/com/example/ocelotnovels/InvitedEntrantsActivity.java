//package com.example.ocelotnovels;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.ocelotnovels.view.Common.InvitedEntrantsAdapter;
//import com.google.firebase.firestore.auth.User;
//
//import java.util.List;
//
//private RecyclerView recyclerView;
//private InvitedEntrantsAdapter adapter;
//private List<User> entrantList;
//
//private void fetchChosenEntrants() {
//    eventListsManager.getEventLists(eventID, new EventListsManager.OnEventListsFetchListener() {
//        @Override
//        public void onEventListsFetched(EventLists eventLists) {
//            List<String> chosenUserIds = eventLists.getChosenList();
//
//            // Fetch user details
//            eventListsManager.getUsersByIds(chosenUserIds, new EventListsManager.OnUsersFetchListener() {
//                @Override
//                public void onUsersFetched(List<User> users) {
//                    entrantList = users;
//                    runOnUiThread(() -> setupRecyclerView());
//                }
//
//                @Override
//                public void onUsersFetchError(Exception e) {
//                    // Handle error
//                }
//            });
//        }
//
//        @Override
//        public void onEventListsFetchError(Exception e) {
//            // Handle error
//        }
//    });
//}
//
//private void setupRecyclerView() {
//    recyclerView = findViewById(R.id.recyclerView_invited_entrants);
//    recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    adapter = new InvitedEntrantsAdapter(this, entrantList);
//    recyclerView.setAdapter(adapter);
//}
