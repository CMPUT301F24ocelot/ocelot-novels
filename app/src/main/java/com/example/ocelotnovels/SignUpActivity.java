package com.example.ocelotnovels;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ocelotnovels.utils.FirebaseUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, nameEditText, phoneEditText;
    private Button signUpButton;
    private FirebaseFirestore db;
    private FirebaseUtils firebaseUtils;
    private String  deviceId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_signup_activity);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        emailEditText = findViewById(R.id.editTextEmail);
        nameEditText = findViewById(R.id.editTextName);
        phoneEditText = findViewById(R.id.editPhoneNum);
        signUpButton = findViewById(R.id.buttonSignUp);
        firebaseUtils = new FirebaseUtils(this);
        deviceId = firebaseUtils.getDeviceId(this);
        // Remove password EditText from layout since we don't need it anymore

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String name = nameEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();

                // Validate inputs
                if (email.isEmpty() || name.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Split name into first and last name
                String[] nameParts = name.split(" ", 2);
                String firstName = nameParts[0];
                String lastName = nameParts.length > 1 ? nameParts[1] : "";

                // Create user data map according to your Firestore structure
                Map<String, Object> userData = new HashMap<>();
                userData.put("name", firstName + " " + lastName);
                userData.put("email", email);
                if (!phone.isEmpty()) {
                    userData.put("phone", phone);
                }
                userData.put("role", "entrant"); // Default role
                userData.put("notificationsEnabled", true); // Default setting
                userData.put("eventsJoined", new ArrayList<>()); // Empty array for events

                // Add user to Firestore
                db.collection("users")
                        .document(deviceId).set(userData)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(SignUpActivity.this, "Successfully signed up!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}