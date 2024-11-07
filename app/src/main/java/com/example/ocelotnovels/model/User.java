package com.example.ocelotnovels.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a generic user with basic information including first name, last name, email,
 * and an optional phone number. This class is intended to be extended by specific user roles
 * such as Entrant, Organizer, and Admin.
 */
public abstract class User {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber; // Optional

    /**
     * Constructs a User with the specified first name, last name, and email.
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param email     the user's email
     */
    public User(String firstName, String lastName, String email) {
        this(firstName, lastName, email, null);
    }

    /**
     * Constructs a User with the specified first name, last name, email, and phone number.
     * @param firstName   the user's first name
     * @param lastName    the user's last name
     * @param email       the user's email
     * @param phoneNumber the user's phone number (optional)
     */
    public User(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the user's first name.
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name.
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the user's email.
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's phone number.
     * @return the phone number, or null if not set
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number.
     * @param phoneNumber the phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns a string representation of the user, including only non-null attributes.
     * @return a string representation of the user
     */
    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                (phoneNumber != null ? ", phoneNumber='" + phoneNumber + '\'' : "") +
                ", email='" + email + '\'' +
                '}';
    }

    /**
     * Converts the user to a map representation, suitable for database storage.
     * Only includes the phone number if it is not null.
     * @return a map containing the user's details
     */
    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("firstName", firstName);
        userMap.put("lastName", lastName);
        userMap.put("email", email);

        if (phoneNumber != null) {
            userMap.put("phoneNumber", phoneNumber);
        }
        return userMap;
    }

    /**
     * Checks if this user is equal to another object based on first name, last name, and email.
     * @param o the object to compare
     * @return true if the object is a User with the same first name, last name, and email
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(email, user.email);
    }

    /**
     * Computes a hash code for the user based on their first name, last name, and email.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email);
    }
}
