package com.example.ocelotnovels;

import android.view.View;

import com.example.ocelotnovels.view.Entrant.EventDetailsFragment;

public class MockEventDetailsFragment extends EventDetailsFragment {

    @Override
    protected void loadEventDetails() {
        // Simulate data loading for testing
        requireActivity().runOnUiThread(() -> {
            // Mock data
            String mockTitle = "Mock Event Title";
            String mockDescription = "Mock Event Description";
            String mockDeadline = "Mock Deadline: 2023-12-31";

            // Update UI with mock data
            if (eventTitle != null) eventTitle.setText(mockTitle);
            if (eventDescription != null) eventDescription.setText(mockDescription);
            if (registrationDeadline != null) registrationDeadline.setText(mockDeadline);

            // Set geolocation warning visible
            if (geolocationWarning != null) geolocationWarning.setVisibility(View.VISIBLE);
        });
    }
}
