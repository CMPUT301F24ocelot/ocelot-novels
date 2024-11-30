package com.example.ocelotnovels;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_maps);

        // Set the title for the activity
        setTitle("Map of Users");

        // Enable the back button in the action bar
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize the MapFragment with the custom Map ID
        MapFragment mapFragment = MapFragment.newInstance(
                new GoogleMapOptions().mapId(getResources().getString(R.string.map_id))
        );

        // Add the MapFragment to the layout dynamically
        getFragmentManager().beginTransaction()
                .replace(R.id.map, mapFragment)
                .commit();

        // Set up the MapAsync callback
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Enable location layer if permission has been granted (done earlier)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Fetch entrants from Firestore
        loadEntrantLocations();
    }

    private void loadEntrantLocations() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful() && task.getResult() != null) {
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        boolean hasMarkers = false; // Track if there are any markers added

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Attempt to retrieve the eventLocations field as a list of GeoPoints
                            List<GeoPoint> eventLocations = (List<GeoPoint>) document.get("eventLocations");
                            String userName = document.getString("name"); // Retrieve the user's name

                            if (eventLocations != null && userName != null) {
                                for (GeoPoint geoPoint : eventLocations) {
                                    if (geoPoint != null) {
                                        LatLng userLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                        if (mMap != null) {
                                            // Add a marker with the user's name as the title
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(userLocation)
                                                    .title(userName)
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                            boundsBuilder.include(userLocation); // Include this location in the bounds
                                            hasMarkers = true;
                                        }
                                    } else {
                                        Log.w("Firestore", "Null GeoPoint in eventLocations for user: " + document.getId());
                                    }
                                }
                            } else {
                                Log.w("Firestore", "No eventLocations field or user name for user: " + document.getId());
                            }
                        }

                        // If there are markers, move and zoom the camera to fit all markers
                        if (hasMarkers) {
                            LatLngBounds bounds = boundsBuilder.build();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)); // 100 padding around the bounds
                        } else {
                            Log.w("Firestore", "No markers to display on the map.");
                            Toast.makeText(this, "No locations to display.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("Firestore", "Failed to retrieve users", task.getException());
                        Toast.makeText(this, "Failed to load entrant locations.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back button in the action bar
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity and navigate back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
