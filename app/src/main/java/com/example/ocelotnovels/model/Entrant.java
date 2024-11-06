package com.example.ocelotnovels.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an entrant with a first name, last name, email, and an optional phone number.
 */
public class Entrant {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber; // Optional

    /**
     * Constructs an Entrant with a specified first name, last name and an email.
     * Since no phone number is provided, it is set to null by default.
     * @param firstName the entrant's first name
     * @param lastName  the entrant's last name
     * @param email     the entrant's email
     */
    public Entrant(String firstName,String lastName,String email){
        this.firstName = firstName;
        this.lastName= lastName;
        this.email= email;
        this.phoneNumber=null; // Null by default
    }

    /**
     * Constructs an Entrant with the specified first name, last name, email, and phone number.
     * @param firstName   the entrant's first name
     * @param lastName    the entrant's last name
     * @param email       the entrant's email
     * @param phoneNumber the entrant's phone number
     */
    public Entrant(String firstName,String lastName,String email, String phoneNumber){
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.phoneNumber=phoneNumber;
    }

    /**
     * Gets the entrant's first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the entrant's first name.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the entrant's last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the entrant's last name.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the entrant's email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the entrant's email.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the entrant's phone number.
     *
     * @return the phone number, or null if not set
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the entrant's phone number.
     *
     * @param phoneNumber the phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns a string representation of the entrant.
     * The phone number is only included if it is not null.
     *
     * @return a string representation of the entrant
     */
    @Override
    public String toString() {
        return "Entrant{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                (phoneNumber != null ? ", phoneNumber='" + phoneNumber + '\'' : "") +
                ", email='" + email + '\'' +
                '}';
    }

    /**
     * Converts the entrant to a map representation, suitable for saving to a database.
     * The phone number is only included in the map if it is not null.
     *
     * @return a map containing the entrant's details
     */
    public Map<String, Object> toMap() {
        Map<String, Object> entrantMap = new HashMap<>();
        entrantMap.put("firstName", firstName);
        entrantMap.put("lastName", lastName);
        entrantMap.put("email", email);

        if (phoneNumber != null) {
            entrantMap.put("phoneNumber", phoneNumber);
        }
        return entrantMap;
    }
}
