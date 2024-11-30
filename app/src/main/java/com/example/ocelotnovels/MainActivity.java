package com.example.ocelotnovels;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Build;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;

import com.example.ocelotnovels.utils.FirebaseUtils;
import com.example.ocelotnovels.view.Entrant.ConfirmedEventsActivity;
import com.example.ocelotnovels.view.Admin.AdminBrowseActivity;
import com.example.ocelotnovels.view.Entrant.EventDetailsFragment;
import com.example.ocelotnovels.view.Entrant.ProfileActivity;
import com.example.ocelotnovels.view.Entrant.SelectedEventsActivity;
import com.example.ocelotnovels.view.Entrant.WaitingListActivity;
import com.example.ocelotnovels.view.Organizer.OrganizerMainActivity;
import com.google.android.gms.common.moduleinstall.ModuleInstall;
import com.google.android.gms.common.moduleinstall.ModuleInstallClient;
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest;
import com.google.android.gms.common.moduleinstall.ModuleInstallResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

/**
 * The MainActivity class represents the main entry point of the application.
 * It handles user interactions, QR code scanning, navigation to other activities,
 * and Firebase Firestore integration.
 */
public class MainActivity extends AppCompatActivity {

    private FirebaseUtils firebaseUtils;
    private Boolean isScannerInstalled = false;
    private Button scanQrBtn;
    private Button organizerBtn;
    private Button adminBtn;
    private GmsBarcodeScanner scanner;
    private FirebaseAuth mAuth;
    private Button signUpButton, eventViewBtn;
    private String deviceId;
    private String getUserEmail;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS";

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Notification permission granted, now request location permission
                    Toast.makeText(this, "Notification Permission Granted", Toast.LENGTH_SHORT).show();
                    requestLocationPermission();
                } else {
                    // Notification permission denied, still proceed to location permission
                    Toast.makeText(this, "Notification Permission Denied", Toast.LENGTH_SHORT).show();
                    requestLocationPermission();
                }
            });


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean isUserSignedUp;

    /**
     * Called when the activity is created. Initializes views, Firebase instances, and
     * handles dynamic UI setup based on user data.
     *
     * @param savedInstanceState the previously saved state of the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // Initialize views and FirebaseUtils
        initVars();
        installGoogleScanner();

        // Set up button click listeners
        registerUIListener();

        // Fetch user data and update the UI accordingly
        fetchUserData();

        askNotificationPermission();
    }

    /**
     * Requests the runtime notification permission for Android 13+.
     */
    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // For Android 13+
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // Notification permission already granted, now request location permission
                requestLocationPermission();
            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                // Show rationale for notification permission
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Notification Permission Required")
                        .setMessage("This app requires notification permissions for better functionality.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            requestPermissionLauncher.launch(POST_NOTIFICATIONS);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            // Even if denied, proceed to request location permission
                            requestLocationPermission();
                        })
                        .create()
                        .show();
            } else {
                // Directly request notification permission
                requestPermissionLauncher.launch(POST_NOTIFICATIONS);
            }
        } else {
            // For Android versions below 13, directly request location permission
            requestLocationPermission();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Control menu visibility based on user sign-up status
        menu.setGroupVisible(R.id.menu_group_signed_in, isUserSignedUp);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle menu item clicks
        if (id == R.id.action_profile) {
            // Navigate to Profile Activity
            Intent profileActivity = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(profileActivity);
        }
        else if (id == R.id.selected_events) {
            // Navigate to Profile Activity
            Intent selectedEventsActivity = new Intent(MainActivity.this, SelectedEventsActivity.class);
            startActivity(selectedEventsActivity);
        }
        else if (id == R.id.confirmed_events) {
            // Navigate to Profile Activity
            Intent confirmedEventsActivity = new Intent(MainActivity.this, ConfirmedEventsActivity.class);
            startActivity(confirmedEventsActivity);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Initializes variables and configures the barcode scanner.
     */
    private void initVars() {
        scanQrBtn = findViewById(R.id.user_scan_qr);
        organizerBtn = findViewById(R.id.user_organizer);
        signUpButton = findViewById(R.id.user_sign_up_button);
        eventViewBtn = findViewById(R.id.user_event_list);
        adminBtn = findViewById(R.id.user_admin);


        GmsBarcodeScannerOptions options = initializeGoogleScanner();
        scanner = GmsBarcodeScanning.getClient(this, options);
        firebaseUtils = new FirebaseUtils(this);
        deviceId = firebaseUtils.getDeviceId(this);
        Log.i("Main Activity:deviceId", deviceId);
    }

    /**
     * Configures the options for the Google Barcode Scanner.
     *
     * @return an instance of GmsBarcodeScannerOptions.
     */
    private GmsBarcodeScannerOptions initializeGoogleScanner() {
        return new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .enableAutoZoom()
                .build();
    }

    /**
     * Installs the Google Barcode Scanner module dynamically.
     */
    private void installGoogleScanner() {
        ModuleInstallClient moduleInstall = ModuleInstall.getClient(this);
        ModuleInstallRequest moduleInstallRequest = ModuleInstallRequest.newBuilder()
                .addApi(GmsBarcodeScanning.getClient(this))
                .build();

        moduleInstall.installModules(moduleInstallRequest)
                .addOnSuccessListener(moduleInstallResponse -> isScannerInstalled = true)
                .addOnFailureListener(e -> {
                    isScannerInstalled = false;
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkOrganizerEligibility() {
        db.collection("facilities")
                .whereEqualTo("ownerId", deviceId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Facility exists, navigate to OrganizerMainActivity
                        Intent intent = new Intent(this, OrganizerMainActivity.class);
                        startActivity(intent);
                    } else {
                        // No facility, prompt to create one
                        new AlertDialog.Builder(this)
                                .setTitle("Create Facility Profile")
                                .setMessage("You need to create a facility profile to become an organizer.")
                                .setPositiveButton("Go to Facility Profile", (dialog, which) -> {
                                    Intent intent = new Intent(this, FacilityProfileActivity.class);
                                    startActivity(intent);
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Sets up button click listeners for various actions.
     */
    private void registerUIListener() {
        scanQrBtn.setOnClickListener(v -> {
            if (isScannerInstalled) {
                startScanning();
            } else {
                Toast.makeText(getApplicationContext(), "Scanner not Installed, Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

        organizerBtn.setOnClickListener(v -> {
            checkOrganizerEligibility();
//            Intent organizerIntent = new Intent(MainActivity.this, OrganizerMainActivity.class);
//            startActivity(organizerIntent);
        });

        adminBtn.setOnClickListener(v -> {
            Intent adminIntent = new Intent(MainActivity.this, AdminBrowseActivity.class);
            startActivity(adminIntent);
        });


    }

    /**
     * Initiates the QR code scanning process.
     */
    private void startScanning() {
        scanner.startScan()
                .addOnSuccessListener(barcode -> {
                    String qrContent = barcode.getRawValue();
                    if (qrContent != null && !qrContent.isEmpty()) {
                        String eventId = qrContent;
                        Toast.makeText(MainActivity.this, "Event ID: " + eventId, Toast.LENGTH_SHORT).show();
                        toEventDetails(eventId, deviceId);
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid QR Code content", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCanceledListener(() -> Toast.makeText(MainActivity.this, "Scan canceled", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Scan failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Navigates to the event details view with the given event ID and device ID.
     *
     * @param eventId   the ID of the event.
     * @param deviceId  the ID of the device.
     */
    private void toEventDetails(String eventId, String deviceId) {
        EventDetailsFragment fragment = EventDetailsFragment.newInstance(eventId, deviceId);
        getSupportFragmentManager().beginTransaction()
                .add(fragment, "eventDetails")
                .commitAllowingStateLoss();
    }

    /**
     * Fetches user data from Firestore and updates the UI.
     */
    private void fetchUserData() {
        db.collection("users").document(deviceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("email")) {
                        getUserEmail = documentSnapshot.getString("email");
                        isUserSignedUp = true;
                        signUpButton.setVisibility(View.GONE);
                        eventViewBtn.setOnClickListener(v -> {
                            Intent intent = new Intent(getApplicationContext(), WaitingListActivity.class);
                            startActivity(intent);
                        });
                    } else {
                        isUserSignedUp = false;
                        signUpButton.setVisibility(View.VISIBLE);
                        signUpButton.setOnClickListener(view -> {
                            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    }
                    invalidateOptionsMenu();
                })
                .addOnFailureListener(e -> {
                    isUserSignedUp = false; // Default to not signed up in case of error
                    Toast.makeText(MainActivity.this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    invalidateOptionsMenu(); // Refresh the menu
                });
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location permission denied. Some features may be limited.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}