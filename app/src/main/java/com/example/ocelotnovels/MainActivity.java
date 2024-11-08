package com.example.ocelotnovels;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ocelotnovels.utils.FirebaseUtils;
import com.example.ocelotnovels.view.Entrant.EventDetailsFragment;
import com.google.android.gms.common.moduleinstall.ModuleInstall;
import com.google.android.gms.common.moduleinstall.ModuleInstallClient;
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest;
import com.google.android.gms.common.moduleinstall.ModuleInstallResponse;
import com.google.android.gms.tasks.OnCanceledListener;
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
    public String deviceId;
    private GmsBarcodeScanner scanner;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initVars();
        installGoogleScanner();

        registerUIListener();



        /*
        if (user == null) {
            // Redirect to SignUpActivity if not signed in
//            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
//            startActivity(intent);
//            finish();
            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            View userSignedInView = findViewById(R.id.user_sign_up_button);
            userSignedInView.setVisibility(View.GONE);
        }*/
    }
    private void initVars(){
        scanQrBtn = findViewById(R.id.user_scan_qr);
        //scannedValueTv =findViewById(R.id.scannedValueTv);
        GmsBarcodeScannerOptions options = initializeGoogleScanner();
        scanner = GmsBarcodeScanning.getClient(this,options);
        firebaseUtils = new FirebaseUtils(this);
        deviceId = firebaseUtils.getDeviceId(this);
    }
    private GmsBarcodeScannerOptions initializeGoogleScanner(){
       return new GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).enableAutoZoom()
               .build();
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
    private void registerUIListener(){
        scanQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScannerInstalled){
                    startScanning();
                } else {
                    Toast.makeText(getApplicationContext(), "Scanner not Installed, Please try again!", Toast.LENGTH_SHORT).show();

                }
            }
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

//                    toEventDetails(eventId);
                    MainActivity.this.runOnUiThread(() -> toEventDetails(eventId));

                    // Perform any further actions with the event ID here, like storing it or using it for a database query
                } else {
                    Toast.makeText(MainActivity.this, "Invalid QR Code content", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(MainActivity.this, "Scan canceled", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Scan failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void toEventDetails(String eventId) {
        // Check if the activity is not in a finishing state
        if (!isFinishing() && !isDestroyed()) {
            // Proceed with fragment transaction if the activity is in a valid state
            EventDetailsFragment fragment = EventDetailsFragment.newInstance(eventId);
            fragment.show(getSupportFragmentManager(), "eventDetails");
        } else {
            // Handle the case where the activity is finishing or destroyed
            Toast.makeText(MainActivity.this, "Activity is not in a valid state", Toast.LENGTH_SHORT).show();
        }
    }

}