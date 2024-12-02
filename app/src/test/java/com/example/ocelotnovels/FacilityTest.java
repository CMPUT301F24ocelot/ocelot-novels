package com.example.ocelotnovels;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.ocelotnovels.model.Facility;

import org.junit.Before;
import org.junit.Test;

public class FacilityTest {

    private Facility facility;

    @Before
    public void setUp() {
        // Initialize a Facility object with sample data for testing
        facility = new Facility("owner123", "Test Facility", "test@example.com", "1234567890", "Test Location", "A spacious facility for testing.");
    }

    @Test
    public void testConstructorInitialization() {
        // Verify that the Facility object is initialized correctly
        assertEquals("owner123", facility.getOwnerId());
        assertEquals("Test Facility", facility.getFacilityName());
        assertEquals("test@example.com", facility.getFacilityEmail());
        assertEquals("1234567890", facility.getFacilityPhone());
        assertEquals("Test Location", facility.getFacilityLocation());
        assertEquals("A spacious facility for testing.", facility.getFacilityDescription());
        assertNotNull(facility.getFacilityId());  // Verify the facility ID is automatically generated
    }

    @Test
    public void testSetFacilityName() {
        // Update facility name and verify the change
        facility.setFacilityName("Updated Facility Name");
        assertEquals("Updated Facility Name", facility.getFacilityName());
    }

    @Test
    public void testSetFacilityEmail() {
        // Update facility email and verify the change
        facility.setFacilityEmail("updated@example.com");
        assertEquals("updated@example.com", facility.getFacilityEmail());
    }

    @Test
    public void testSetFacilityPhone() {
        // Update facility phone and verify the change
        facility.setFacilityPhone("0987654321");
        assertEquals("0987654321", facility.getFacilityPhone());
    }

    @Test
    public void testSetFacilityLocation() {
        // Update facility location and verify the change
        facility.setFacilityLocation("Updated Location");
        assertEquals("Updated Location", facility.getFacilityLocation());
    }

    @Test
    public void testSetFacilityDescription() {
        // Update facility description and verify the change
        facility.setFacilityDescription("Updated description for the facility.");
        assertEquals("Updated description for the facility.", facility.getFacilityDescription());
    }

    @Test
    public void testToString() {
        // Verify the toString method returns a non-null string representation
        assertNotNull(facility.toString());
    }
}