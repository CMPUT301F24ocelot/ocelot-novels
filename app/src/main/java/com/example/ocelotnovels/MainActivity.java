package com.example.ocelotnovels;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

public class MainActivity extends AppCompatActivity {
    private FirebaseUtils firebaseUtils;
    private Boolean isScannerInstalled = false;
    private Button scanQrBtn;
    private Button organizerBtn;  // Additional button for Organizer Main Activity
    private GmsBarcodeScanner scanner;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public String deviceId;
    private Button signUpButton;
    private String getUserEmail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views and FirebaseUtils
        initVars();
        installGoogleScanner();

        // Set up button click listeners
        registerUIListener();

        signUpButton = findViewById(R.id.user_sign_up_button);


        db.collection("users").document(deviceId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                signUpButton = findViewById(R.id.user_sign_up_button);

                // Check if document exists and contains an "email" field
                if (documentSnapshot.exists() && documentSnapshot.contains("email")) {
                    getUserEmail = documentSnapshot.getString("email");

                    // Hide sign-up button if user is already signed up
                    signUpButton.setVisibility(View.GONE);
                } else {
                    // Set up onClickListener only if user is not signed up
                    signUpButton.setVisibility(View.VISIBLE);
                    signUpButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        }


    private void initVars(){
        scanQrBtn = findViewById(R.id.user_scan_qr);
        organizerBtn = findViewById(R.id.user_organizer);  // Organizer button initialization

        GmsBarcodeScannerOptions options = initializeGoogleScanner();
        scanner = GmsBarcodeScanning.getClient(this, options);
        firebaseUtils = new FirebaseUtils(this);
        deviceId = firebaseUtils.getDeviceId(this);
        Log.i("Main Activity:deviceId",deviceId);
    }

    private GmsBarcodeScannerOptions initializeGoogleScanner(){
        return new GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).enableAutoZoom().build();
    }

    private void installGoogleScanner(){
        ModuleInstallClient moduleInstall = ModuleInstall.getClient(this);
        ModuleInstallRequest moduleInstallRequest = ModuleInstallRequest.newBuilder().addApi(GmsBarcodeScanning.getClient(this)).build();
        moduleInstall.installModules(moduleInstallRequest)
                .addOnSuccessListener(new OnSuccessListener<ModuleInstallResponse>() {
                    @Override
                    public void onSuccess(ModuleInstallResponse moduleInstallResponse) {
                        isScannerInstalled = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isScannerInstalled = false;
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void registerUIListener() {
        scanQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScannerInstalled) {
                    startScanning();
                } else {
                    Toast.makeText(getApplicationContext(), "Scanner not Installed, Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Organizer button listener to open OrganizerMainActivity
        organizerBtn.setOnClickListener(v -> {
            Intent organizerIntent = new Intent(MainActivity.this, OrganizerMainActivity.class);
            startActivity(organizerIntent);
        });
    }

    private void startScanning(){
        scanner.startScan().addOnSuccessListener(new OnSuccessListener<Barcode>() {
            @Override
            public void onSuccess(Barcode barcode) {
                // Extract the raw value from the scanned QR code
                String qrContent = barcode.getRawValue();

                if (qrContent != null && !qrContent.isEmpty()) {
                    // Assuming that the event ID is the entire content of the QR code
                    String eventId = qrContent;

                    // Display the extracted event ID
                    Toast.makeText(MainActivity.this, "Event ID: " + eventId, Toast.LENGTH_SHORT).show();

                    toEventDetails(eventId,deviceId);
                    //MainActivity.this.runOnUiThread(() -> toEventDetails(eventId,deviceId));

                    // Perform any further actions with the event ID here, like storing it or using it for a database query
                } else {
                    Toast.makeText(MainActivity.this, "Invalid QR Code content", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnCanceledListener(() ->
                Toast.makeText(MainActivity.this, "Scan canceled", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(MainActivity.this, "Scan failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }


    private void toEventDetails(String eventId,String deviceId) {
        EventDetailsFragment fragment = EventDetailsFragment.newInstance(eventId,deviceId);
        getSupportFragmentManager().beginTransaction()
                .add(fragment, "eventDetails")
                .commitAllowingStateLoss();
    }
}
