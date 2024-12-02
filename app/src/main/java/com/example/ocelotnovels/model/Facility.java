/**
 * This class represents a Facility in the system. A Facility is an entity
 * with a unique ID, associated owner, name, contact details, location, and a
 * description. Each Facility also maintains a list of members (represented
 * as member IDs). The class provides functionality to manage Facility details
 * and its associated members, including adding, removing, and checking members.
 *
 * Key Features:
 * - Unique facility ID generation.
 * - Getter and setter methods for facility attributes.
 * - Member management: add, remove, and check membership.
 * - Default and parameterized constructors for flexibility.
 * - A string representation of the facility for debugging and display.
 *
 * Usage:
 * Use this class to represent facilities in applications where such entities
 * are part of the system, e.g., event management, resource allocation, etc.
 */

package com.example.ocelotnovels.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Facility implements Serializable {
    private final String facilityId; // Unique ID for the facility
    private String ownerId; // ID of the owner of the facility
    private String facilityName; // Name of the facility
    private String facilityEmail; // Email contact for the facility
    private String facilityPhone; // Phone contact for the facility
    private String facilityLocation; // Physical location of the facility
    private String facilityDescription; // Description of the facility
    private ArrayList<String> members; // List of member IDs associated with the facility
    private ArrayList<String> eventIds;// A list of all the events that a facility is hosting
    private String facilityPicUrl;

    /**
     * This is a constructor that can be used to simplify the data that is needed to delete a facility fully
     * @param facilityId
     * @param eventIds
     */
    public Facility(String facilityId, ArrayList<String> eventIds){
        this.facilityId = facilityId;
        this.eventIds = eventIds;
    }

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

    /**
     * Used by Admin when getting the list of facilities so that they have their specified Id in the class
     * @param facilityId        The Id of the facility as found in Firestore
     * @param ownerId           ID of the facility owner
     * @param facilityName      Name of the facility
     * @param facilityEmail     Contact email for the facility
     * @param facilityPhone     Contact phone for the facility
     * @param facilityLocation  Physical location of the facility
     * @param facilityDescription Description of the facility
     * @param
     */
    public Facility(String facilityId, String ownerId, String facilityName, String facilityEmail, String facilityPhone, String facilityLocation, String facilityDescription, ArrayList<String> events) {
        this.facilityId = facilityId;
        this.ownerId = ownerId;
        this.facilityName = facilityName;
        this.facilityEmail = facilityEmail;
        this.facilityPhone = facilityPhone;
        this.facilityLocation = facilityLocation;
        this.facilityDescription = facilityDescription;
        this.eventIds = events;
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

    /**
     * This sets the Url for the facility picture so that the Admin can use it later
     * @param facilityPicUrl A string representing the Url to the facility picture
     */
    public void setFacilityPicUrl(String facilityPicUrl){this.facilityPicUrl = facilityPicUrl;}

    /**
     * This returns the Url of the facility picture
     * @return
     */
    public String getFacilityPicUrl(){return facilityPicUrl;}

    /**
     * This return the list of events that this facility has organized
     * @return
     */
    public ArrayList<String> getEventIds() {return eventIds;}

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