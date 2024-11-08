package com.example.ocelotnovels.model;

import java.util.UUID;

public class Facility {

    // Facility Attributes
    private final String facilityId; // UUID for facility
    private String ownerId; // Owner id referencing userId from users collection of facility owner
    private String facilityName; // The name of the facility
    private String facilityEmail; // The contact email of the facility
    private String facilityPhone; // The contact phone of the facility
    private String facilityLocation; // The physical location of the facility
    private String facilityDescription; // A description of the facility

    // Default constructor for Firebase
    public Facility() {
        this.facilityId = UUID.randomUUID().toString();
    }

    // Constructor with required fields
    public Facility(String ownerId, String facilityName, String facilityEmail, String facilityPhone, String facilityLocation, String facilityDescription) {
        this.facilityId = UUID.randomUUID().toString();
        this.ownerId = ownerId;
        this.facilityName = facilityName;
        this.facilityEmail = facilityEmail;
        this.facilityPhone = facilityPhone;
        this.facilityLocation = facilityLocation;
        this.facilityDescription = facilityDescription;
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
                '}';
    }
}