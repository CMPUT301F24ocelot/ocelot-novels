package com.example.ocelotnovels;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_maps);

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

        private void centerMapAtUserLocation() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                        } else {
                            Log.w("MapsActivity", "User location is null. Cannot center map.");
                            Toast.makeText(this, "Unable to retrieve your location.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MapsActivity", "Error retrieving user location: ", e);
                        Toast.makeText(this, "Error retrieving your location.", Toast.LENGTH_SHORT).show();
                    });
        }


    private void loadEntrantLocations() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Attempt to retrieve the eventLocations field as a list of GeoPoints
                            List<GeoPoint> eventLocations = (List<GeoPoint>) document.get("eventLocations");

                            if (eventLocations != null) {
                                for (GeoPoint geoPoint : eventLocations) {
                                    if (geoPoint != null) {
                                        LatLng userLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                        if (mMap != null) {
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(userLocation)
                                                    .title("User Location")
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                        }
                                    } else {
                                        Log.w("Firestore", "Null GeoPoint in eventLocations for user: " + document.getId());
                                    }
                                }
                            } else {
                                Log.w("Firestore", "No eventLocations field for user: " + document.getId());
                            }
                        }
                    } else {
                        Log.w("Firestore", "Failed to retrieve users", task.getException());
                        Toast.makeText(this, "Failed to load entrant locations.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
