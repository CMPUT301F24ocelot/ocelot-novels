package com.example.ocelotnovels;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_maps);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Load the map fragment dynamically
        SupportMapFragment mapFragment = new SupportMapFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.map, mapFragment)
                .commit();

        // Set up the map
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Fetch entrants from Firestore
        loadEntrantLocations();
    }

    private void loadEntrantLocations() {
        // Reference to the events collection
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Retrieve each event's location and name
                            Double lat = document.getDouble("eventLocation.latitude");
                            Double lng = document.getDouble("eventLocation.longitude");
                            String eventName = document.getString("eventName");

                            // Check if both latitude and longitude are available
                            if (lat != null && lng != null) {
                                LatLng eventLocation = new LatLng(lat, lng);

                                // Add a marker on the map for each event location
                                mMap.addMarker(new MarkerOptions()
                                        .position(eventLocation)
                                        .title(eventName)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                            }
                        }
                    } else {
                        // Handle the case where event data is not available
                        Log.w("Firestore", "Failed to retrieve event locations");
                    }
                });
    }
}