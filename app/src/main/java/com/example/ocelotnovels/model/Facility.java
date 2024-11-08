package com.example.ocelotnovels.model;

import java.util.ArrayList;
import java.util.UUID;

public class Facility {
    private final String facilityId; // Unique ID for the facility
    private String ownerId; // ID of the owner of the facility
    private String facilityName; // Name of the facility
    private String facilityEmail; // Email contact for the facility
    private String facilityPhone; // Phone contact for the facility
    private String facilityLocation; // Physical location of the facility
    private String facilityDescription; // Description of the facility
    private ArrayList<String> members; // List of member IDs associated with the facility

    /**
     * Default constructor for Firebase or other ORM systems
     */
    public Facility() {
        this.facilityId = UUID.randomUUID().toString();
        this.members = new ArrayList<>();
    }

    /**
     * Constructs a facility with specified details.
     * @param ownerId           ID of the facility owner
     * @param facilityName      Name of the facility
     * @param facilityEmail     Contact email for the facility
     * @param facilityPhone     Contact phone for the facility
     * @param facilityLocation  Physical location of the facility
     * @param facilityDescription Description of the facility
     */
    public Facility(String ownerId, String facilityName, String facilityEmail, String facilityPhone, String facilityLocation, String facilityDescription) {
        this.facilityId = UUID.randomUUID().toString();
        this.ownerId = ownerId;
        this.facilityName = facilityName;
        this.facilityEmail = facilityEmail;
        this.facilityPhone = facilityPhone;
        this.facilityLocation = facilityLocation;
        this.facilityDescription = facilityDescription;
        this.members = new ArrayList<>();
    }

    // Getters and Setters

    public String getFacilityId() {
        return facilityId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityEmail() {
        return facilityEmail;
    }

    public void setFacilityEmail(String facilityEmail) {
        this.facilityEmail = facilityEmail;
    }

    public String getFacilityPhone() {
        return facilityPhone;
    }

    public void setFacilityPhone(String facilityPhone) {
        this.facilityPhone = facilityPhone;
    }

    public String getFacilityLocation() {
        return facilityLocation;
    }

    public void setFacilityLocation(String facilityLocation) {
        this.facilityLocation = facilityLocation;
    }

    public String getFacilityDescription() {
        return facilityDescription;
    }

    public void setFacilityDescription(String facilityDescription) {
        this.facilityDescription = facilityDescription;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    // Methods to add and remove members

    /**
     * Adds a member to the facility.
     *
     * @param member the ID of the member to be added
     * @return true if the member was added, false if the member was already present
     */
    public boolean addMember(String member) {
        if (!members.contains(member)) {
            members.add(member);
            return true;
        }
        return false;
    }

    /**
     * Removes a member from the facility.
     *
     * @param member the ID of the member to be removed
     * @return true if the member was removed, false if the member was not found
     */
    public boolean removeMember(String member) {
        return members.remove(member);
    }

    /**
     * Checks if a specific member is part of the facility.
     *
     * @param member the ID of the member to check
     * @return true if the member is part of the facility, false otherwise
     */
    public boolean hasMember(String member) {
        return members.contains(member);
    }

    @Override
    public String toString() {
        return "Facility{" +
                "facilityId='" + facilityId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", facilityName='" + facilityName + '\'' +
                ", facilityEmail='" + facilityEmail + '\'' +
                ", facilityPhone='" + facilityPhone + '\'' +
                ", facilityLocation='" + facilityLocation + '\'' +
                ", facilityDescription='" + facilityDescription + '\'' +
                ", members=" + members +
                '}';
    }
}