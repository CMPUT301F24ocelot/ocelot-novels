package com.example.ocelotnovels.model;

import com.google.firebase.firestore.CollectionReference;

public class Admin extends Entrant{
    /**
     * Constructs an Entrant with the specified first name, last name, and email.
     *
     * @param firstName the entrant's first name
     * @param lastName  the entrant's last name
     * @param email     the entrant's email
     */
    public Admin(String firstName, String lastName, String email, String deviceId) {
        super(firstName, lastName, email, deviceId);
    }

    /**
     * Constructs an Entrant with the specified first name, last name, email, and phone number.
     * @param firstName   the entrant's first name
     * @param lastName    the entrant's last name
     * @param email       the entrant's email
     * @param phoneNumber the entrant's phone number
     */
    public Admin(String firstName, String lastName, String email, String phoneNumber, String deviceId) {
        super(firstName, lastName, email, phoneNumber, deviceId);
    }
}
