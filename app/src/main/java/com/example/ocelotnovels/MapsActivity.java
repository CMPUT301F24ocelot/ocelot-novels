/**
 * MapsActivity displays a Google Map populated with markers representing
 * user event locations retrieved from Firebase Firestore. The map allows
 * organizers to view the geographical locations of entrants for specific events.
 *
 * Key Features:
 * - Displays user locations as markers on a map.
 * - Dynamically fetches user data from Firestore.
 * - Automatically adjusts the map view to include all markers.
 * - Provides a back button for navigation.
 */
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

    /**
     * Initializes the activity, sets up the UI, and initializes the Firestore
     * database and Google Map fragment.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains
     *                           the most recent data supplied to onSaveInstanceState.
     */
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

    /**
     * Callback triggered when the Google Map is ready to use.
     *
     * @param googleMap The Google Map instance.
     */
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

    /**
     * Fetches entrant locations from Firestore and displays them as markers on the map.
     * Adjusts the map's camera to fit all markers. Displays a progress bar while loading data.
     */
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

    /**
     * Handles options item selection, including navigation back to the previous activity.
     *
     * @param item The selected menu item.
     * @return true if the item was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back button in the action bar
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity and navigate back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public GoogleMap getMapInstance() {
        return mMap;
    }
}
