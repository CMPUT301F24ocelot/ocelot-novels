package com.example.ocelotnovels;

import androidx.test.espresso.IdlingResource;

import com.google.android.gms.maps.GoogleMap;

public class GoogleMapIdlingResource implements IdlingResource {
    private IdlingResource.ResourceCallback callback;
    private boolean isIdle = false;

    public GoogleMapIdlingResource(GoogleMap map) {
        if (map != null) {
            map.setOnMapLoadedCallback(() -> {
                isIdle = true;
                if (callback != null) {
                    callback.onTransitionToIdle();
                }
            });
        }
    }

    @Override
    public String getName() {
        return GoogleMapIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }
}
