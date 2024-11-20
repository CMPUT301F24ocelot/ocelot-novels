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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_maps);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Load the map fragment from XML layout
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("MapsActivity", "Error: Map fragment not found");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Fetch entrants from Firestore
        loadEntrantLocations();

        // Check and request location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestLocationPermission();
        }
    }


    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show rationale dialog
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This app requires location access to show your location on the map.")
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            // Directly request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Permission denied permanently
                new AlertDialog.Builder(this)
                        .setTitle("Permission Denied")
                        .setMessage("You have permanently denied location access. Enable it in app settings.")
                        .setPositiveButton("Settings", (dialog, which) -> openAppSettings())
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            } else {
                // Permission denied temporarily
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private void centerMapAtUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            // Get the last known location
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
        } else {
            Log.w("MapsActivity", "Location permission not granted. Cannot center map.");
            Toast.makeText(this, "Location permission required to center the map.", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadEntrantLocations() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful() && task.getResult() != null) {
                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        boolean hasMarkers = false;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            GeoPoint geoPoint = document.getGeoPoint("eventLocation");
                            String eventName = document.getString("eventName");

                            if (geoPoint != null && eventName != null) {
                                LatLng eventLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                mMap.addMarker(new MarkerOptions()
                                        .position(eventLocation)
                                        .title(eventName)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                boundsBuilder.include(eventLocation);
                                hasMarkers = true;
                            } else {
                                Log.w("Firestore", "Invalid data: GeoPoint or eventName is null");
                            }
                        }

                        if (hasMarkers) {
                            // Move the camera to include all markers
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
                        } else {
                            // No points available, center map at user's current location
                            Log.w("MapsActivity", "No valid markers to show. Centering at user's location.");
                            centerMapAtUserLocation();
                        }
                    } else {
                        Log.w("Firestore", "Failed to retrieve event locations", task.getException());
                        Toast.makeText(this, "Failed to load event locations.", Toast.LENGTH_SHORT).show();
                        centerMapAtUserLocation();
                    }
                });
    }
}
