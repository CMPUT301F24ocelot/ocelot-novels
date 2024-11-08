package com.example.ocelotnovels;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.ocelotnovels.utils.FirebaseUtils;
import com.example.ocelotnovels.view.Entrant.EventDetailsFragment;
import com.example.ocelotnovels.view.organizer.OrganizerMainActivity;
import com.google.android.gms.common.moduleinstall.ModuleInstall;
import com.google.android.gms.common.moduleinstall.ModuleInstallClient;
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest;
import com.google.android.gms.common.moduleinstall.ModuleInstallResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

public class MainActivity extends AppCompatActivity {
    private FirebaseUtils firebaseUtils;
    private Boolean isScannerInstalled = false;
    private Button scanQrBtn;
    private Button organizerBtn;
    private GmsBarcodeScanner scanner;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views and FirebaseUtils
        initVars();
        installGoogleScanner();

        // Set up button click listeners
        registerUIListener();
    }

    private void initVars(){
        scanQrBtn = findViewById(R.id.user_scan_qr);
        organizerBtn = findViewById(R.id.user_organizer);

        GmsBarcodeScannerOptions options = initializeGoogleScanner();
        scanner = GmsBarcodeScanning.getClient(this, options);
        firebaseUtils = new FirebaseUtils(this);
    }

    private GmsBarcodeScannerOptions initializeGoogleScanner(){
        return new GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).enableAutoZoom().build();
    }

    private void installGoogleScanner(){
        ModuleInstallClient moduleInstall = ModuleInstall.getClient(this);
        ModuleInstallRequest moduleInstallRequest = ModuleInstallRequest.newBuilder().addApi(GmsBarcodeScanning.getClient(this)).build();
        moduleInstall.installModules(moduleInstallRequest)
                .addOnSuccessListener(moduleInstallResponse -> isScannerInstalled = true)
                .addOnFailureListener(e -> {
                    isScannerInstalled = false;
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void registerUIListener() {
        scanQrBtn.setOnClickListener(v -> {
            if (isScannerInstalled) {
                startScanning();
            } else {
                Toast.makeText(getApplicationContext(), "Scanner not Installed, Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

        // Organizer button listener to open OrganizerMainActivity
        organizerBtn.setOnClickListener(v -> {
            Intent organizerIntent = new Intent(MainActivity.this, OrganizerMainActivity.class);
            startActivity(organizerIntent);
        });
    }

    private void startScanning(){
        scanner.startScan().addOnSuccessListener(barcode -> {
            String qrContent = barcode.getRawValue();
            if (qrContent != null && !qrContent.isEmpty()) {
                String eventId = qrContent;
                Toast.makeText(MainActivity.this, "Event ID: " + eventId, Toast.LENGTH_SHORT).show();
                runOnUiThread(() -> toEventDetails(eventId));
            } else {
                Toast.makeText(MainActivity.this, "Invalid QR Code content", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Scan failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void toEventDetails(String eventId) {
        if (!isFinishing() && !isDestroyed()) {
            EventDetailsFragment fragment = EventDetailsFragment.newInstance(eventId);
            fragment.show(getSupportFragmentManager(), "eventDetails");
        } else {
            Toast.makeText(MainActivity.this, "Activity is not in a valid state", Toast.LENGTH_SHORT).show();
        }
    }
}