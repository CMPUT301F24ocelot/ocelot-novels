package com.example.ocelotnovels;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ocelotnovels.utils.FirebaseUtils;
import com.example.ocelotnovels.view.Entrant.EventDetailsFragment;
import com.example.ocelotnovels.view.Entrant.WaitingListActivity;
import com.example.ocelotnovels.view.Organizer.OrganizerMainActivity;
import com.google.android.gms.common.moduleinstall.ModuleInstall;
import com.google.android.gms.common.moduleinstall.ModuleInstallClient;
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest;
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

    // Firebase related fields
    private FirebaseUtils firebaseUtils;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // UI elements
    private Button scanQrBtn;
    private Button organizerBtn;
    private Button signUpButton;
    private Button eventViewBtn;

    // Scanner related fields
    private GmsBarcodeScanner scanner;
    private boolean isScannerInstalled = false;

    // User related fields
    private String deviceId;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFirebase();
        initializeViews();
        installGoogleScanner();
        setupClickListeners();
        fetchUserData();
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        firebaseUtils = new FirebaseUtils(this);
        deviceId = firebaseUtils.getDeviceId(this);
        Log.i("MainActivity:deviceId", deviceId);
    }

    private void initializeViews() {
        scanQrBtn = findViewById(R.id.user_scan_qr);
        organizerBtn = findViewById(R.id.user_organizer);
        signUpButton = findViewById(R.id.user_sign_up_button);
        eventViewBtn = findViewById(R.id.user_event_list);

        // Initialize scanner with options
        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .enableAutoZoom()
                .build();
        scanner = GmsBarcodeScanning.getClient(this, options);
    }

    private void installGoogleScanner() {
        ModuleInstallClient moduleInstall = ModuleInstall.getClient(this);
        ModuleInstallRequest moduleInstallRequest = ModuleInstallRequest.newBuilder()
                .addApi(GmsBarcodeScanning.getClient(this))
                .build();

        moduleInstall.installModules(moduleInstallRequest)
                .addOnSuccessListener(response -> isScannerInstalled = true)
                .addOnFailureListener(e -> {
                    isScannerInstalled = false;
                    showToast(e.getMessage());
                });
    }

    private void setupClickListeners() {
        scanQrBtn.setOnClickListener(v -> {
            if (isScannerInstalled) {
                startScanning();
            } else {
                showToast("Scanner not installed. Please try again!");
            }
        });

        organizerBtn.setOnClickListener(v -> {
            Intent organizerIntent = new Intent(MainActivity.this, OrganizerMainActivity.class);
            startActivity(organizerIntent);
        });
    }

    private void fetchUserData() {
        db.collection("users").document(deviceId).get()
                .addOnSuccessListener(this::handleUserDataResponse)
                .addOnFailureListener(e -> showToast("Error fetching user data: " + e.getMessage()));
    }

    private void handleUserDataResponse(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists() && documentSnapshot.contains("email")) {
            userEmail = documentSnapshot.getString("email");
            setupSignedInUser();
        } else {
            setupNewUser();
        }
    }

    private void setupSignedInUser() {
        signUpButton.setVisibility(View.GONE);
        eventViewBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), WaitingListActivity.class);
            startActivity(intent);
        });
    }

    private void setupNewUser() {
        signUpButton.setVisibility(View.VISIBLE);
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void startScanning() {
        scanner.startScan()
                .addOnSuccessListener(barcode -> {
                    String qrContent = barcode.getRawValue();
                    if (qrContent != null && !qrContent.isEmpty()) {
                        showToast("Event ID: " + qrContent);
                        toEventDetails(qrContent, deviceId);
                    } else {
                        showToast("Invalid QR Code content");
                    }
                })
                .addOnCanceledListener(() -> showToast("Scan canceled"))
                .addOnFailureListener(e -> showToast("Scan failed: " + e.getMessage()));
    }

    private void toEventDetails(String eventId, String deviceId) {
        EventDetailsFragment fragment = EventDetailsFragment.newInstance(eventId, deviceId);
        getSupportFragmentManager().beginTransaction()
                .add(fragment, "eventDetails")
                .commitAllowingStateLoss();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}