/**
 * This class represents a generic user with essential attributes such as first name, last name,
 * email, and optional attributes like phone number, device ID, and profile picture.
 *
 * It provides methods to manage and validate user details, ensuring proper formatting for
 * fields like email and phone number. This class is designed to be extended for specific
 * user roles such as Entrant, Organizer, or Admin in the context of an event management
 * application.
 *
 * The class also includes utility methods for representing user information as a string or
 * as a map, which can be utilized for database storage or logging purposes. Additionally,
 * methods are provided to set and get optional attributes like device ID and profile picture,
 * facilitating operations such as user deletion or profile viewing by an admin.
 *
 * Validation rules are implemented to maintain data integrity, including checks for name
 * length, email format, and phone number format.
 *
 * Implements `Serializable` to allow instances to be easily persisted or transferred.
 */

package com.example.ocelotnovels.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a generic user with basic information including first name, last name, email,
 * and an optional phone number. This class is intended to be extended by specific user roles
 * such as Entrant, Organizer, and Admin.
 */
public class User implements Serializable {
    private String[] roles = {"entrant"};

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber; // Optional

    private String device_ID;
    private String profilePicture;
    private Facility facility; // this will be used when the admin is deleting a user so that their facility is deleted as well

    /**
     * Constructs a User with the specified first name, last name, and email.
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param email     the user's email
     */
    public User(String firstName, String lastName, String email) {
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        this.phoneNumber=null;
    }

    /**
     * Constructs a User with the specified first name, last name, email, and phone number.
     * @param firstName   the user's first name
     * @param lastName    the user's last name
     * @param email       the user's email
     * @param phoneNumber the user's phone number (optional)
     */
    public User(String firstName, String lastName, String email, String phoneNumber) {
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPhoneNumber(phoneNumber);
    }

    /**
     * Gets the user's first name.
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name. Throws an exception if the first name is null, empty,
     * or exceeds 100 characters.
     *
     * @param firstName the first name to set
     * @throws IllegalArgumentException if the first name is invalid
     */
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty() || firstName.length() > 100) {
            throw new IllegalArgumentException("Invalid First name!");
        }
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name. Throws an exception if the last name is null, empty,
     * or exceeds 100 characters.
     *
     * @param lastName the last name to set
     * @throws IllegalArgumentException if the last name is invalid
     */
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty() || lastName.length() > 100) {
            throw new IllegalArgumentException("Invalid Last name!");
        }
        this.lastName = lastName;
    }

    /**
     * Gets the user's email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email. Throws an exception if the email is null or does not
     * match a valid email format.
     *
     * @param email the email to set
     * @throws IllegalArgumentException if the email is invalid
     */
    public void setEmail(String email) {
        if (email == null || !isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email address!");
        }
        this.email = email;
    }

    /**
     * Gets the user's phone number.
     *
     * @return the phone number, or null if not set
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number. Throws an exception if the phone number is not null
     * but does not match a valid phone number format (10 digits).
     *
     * @param phoneNumber the phone number to set
     * @throws IllegalArgumentException if the phone number is invalid
     */
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Invalid phone number format!");
        }
        this.phoneNumber = phoneNumber;
    }

    /**
     * this is used to get the device ID so that when the admin deletes a user from the  database they have the device ID to find their profile
     * it will be set when admin retrieves the user from the database
     * @param device_ID the device ID from a given user
     */
    public void setDevice_ID(String device_ID){
        this.device_ID = device_ID;
    }

    /**
     * this will return the device ID and will be called on when an admin has to delete a user from the database
     * @return device_ID
     */
    public String getDevice_ID(){
        return device_ID;
    }

    /**
     * this will set the profile Picture for the user and will be used when admin is viewing the user profile
     * @param profilePicture
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * This is used when the admin is going to view the profile of an entrant
     * @return
     */
    public String getProfilePicture(){
        return profilePicture;
    }

    /**
     * this sets the id of the facility if the user has a facility linked to them
     * @param facility
     */
    public void setFacility(Facility facility){
        this.facility = facility;
    }

    /**
     * this returns the facility Id associated with the user
     * @return
     */
    public Facility getFacility(){
        return facility;
    }

    /**
     * Validates the email format using a regular expression.
     *
     * @param email the email to validate
     * @return true if the email matches the format, false otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    /**
     * Validates the phone number format. A valid phone number must contain exactly
     * 10 digits.
     *
     * @param phoneNumber the phone number to validate
     * @return true if the phone number matches the format, false otherwise
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "\\d{10}";
        return phoneNumber.matches(phoneRegex);
    }


    /**
     * Returns a string representation of the user, including only non-null attributes.
     * @return a string representation of the user
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
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
