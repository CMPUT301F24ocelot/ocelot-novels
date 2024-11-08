package com.example.ocelotnovels.model;

/**
 * Represents an entrant user in the system with basic information and comparison functionality.
 * Extends the User class and provides a way to compare entrants by email.
 */
public class Entrant extends User implements Comparable<Entrant> {
    private String role;
    /**
     * Constructs an Entrant with the specified first name, last name, and email.
     * @param firstName the entrant's first name
     * @param lastName  the entrant's last name
     * @param email     the entrant's email
     */
    public Entrant(String firstName, String lastName, String email) {
        super(firstName, lastName, email);
        this.role="Entrant";
    }

    /**
     * Constructs an Entrant with the specified first name, last name, email, and phone number.
     * @param firstName   the entrant's first name
     * @param lastName    the entrant's last name
     * @param email       the entrant's email
     * @param phoneNumber the entrant's phone number
     */
    public Entrant(String firstName, String lastName, String email, String phoneNumber) {
        super(firstName, lastName, email, phoneNumber);
        this.role="Entrant";
    }

    /**
     * Compares this Entrant with another Entrant based on their email.
     * @param other the Entrant to compare with
     * @return a negative integer, zero, or a positive integer as this Entrant's email is
     *         lexicographically less than, equal to, or greater than the specified Entrant's email
     */
    @Override
    public int compareTo(Entrant other) {
        return this.getEmail().compareTo(other.getEmail());
    }

    /**
     * Used to check the role.
     * @return role returns Entrant as the role
     */
    public String getRole() {
        return role;
    }
}
