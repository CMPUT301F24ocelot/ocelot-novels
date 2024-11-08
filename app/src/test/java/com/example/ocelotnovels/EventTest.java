package com.example.ocelotnovels;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.ocelotnovels.model.Event;

import java.util.ArrayList;
import java.util.Date;

public class EventTest {

    private String mockEntrant() {
        return "button@gmail.com";
    }

    @Test
    public void testConstructorNoMax() {
        Event event = new Event("Marathon", new Date(2025 - 1900, 11, 12), new Date(2025 - 1900, 11, 5), new Date(2025 - 1900, 11, 10), new ArrayList<>());
        assertEquals("Marathon", event.getEventName());
        assertEquals(new Date(2025 - 1900, 11, 12), event.getEventDate());
        assertEquals(new Date(2025 - 1900, 11, 5), event.getSignUpStartDate());
        assertEquals(new Date(2025 - 1900, 11, 10), event.getSignUpEndDate());
    }

    @Test
    public void testConstructorWithMax() {
        Event event = new Event("Marathon", new Date(2025 - 1900, 11, 12), new Date(2025 - 1900, 11, 5), new Date(2025 - 1900, 11, 10), new ArrayList<>(), 30);
        assertEquals("Marathon", event.getEventName());
        assertEquals(new Date(2025 - 1900, 11, 12), event.getEventDate());
        assertEquals(new Date(2025 - 1900, 11, 5), event.getSignUpStartDate());
        assertEquals(new Date(2025 - 1900, 11, 10), event.getSignUpEndDate());
        assertEquals(30, event.getWaitListOpenSpots());
    }

    @Test
    public void testAddWaitList() {
        String entrant = mockEntrant();
        Event event = new Event("Marathon", new Date(2025 - 1900, 11, 12), new Date(2025 - 1900, 11, 5), new Date(2025 - 1900, 11, 10), new ArrayList<>(), 1);

        // Check successful addition
        assertTrue(event.addEntrantToWaitList(entrant));
        assertEquals(0, event.getWaitListOpenSpots());

        // Verify the entrant is in the waitlist
        assertTrue(event.inWaitList(entrant));
    }

    @Test
    public void testAddWaitListWhenFull() {
        String entrant1 = mockEntrant();
        String entrant2 = "second_user@gmail.com";

        Event event = new Event("Marathon", new Date(2025 - 1900, 11, 12), new Date(2025 - 1900, 11, 5), new Date(2025 - 1900, 11, 10), new ArrayList<>(), 1);

        // Add first entrant, which should succeed
        assertTrue(event.addEntrantToWaitList(entrant1));
        assertEquals(0, event.getWaitListOpenSpots());

        // Attempt to add a second entrant when full, should fail
        assertFalse(event.addEntrantToWaitList(entrant2));
    }

    @Test
    public void testDeleteEntrant() {
        String entrant = mockEntrant();
        Event event = new Event("Marathon", new Date(2025 - 1900, 11, 12), new Date(2025 - 1900, 11, 5), new Date(2025 - 1900, 11, 10), new ArrayList<>(), 1);

        // Add entrant and check waitlist spot count
        event.addEntrantToWaitList(entrant);
        assertEquals(0, event.getWaitListOpenSpots());

        // Remove the entrant and verify the open spots increment
        assertTrue(event.removeEntrantFromWaitList(entrant));
        assertEquals(1, event.getWaitListOpenSpots());
    }

    @Test
    public void testRemoveNonExistentEntrant() {
        String nonExistentEntrant = "nonexistent@gmail.com";
        Event event = new Event("Marathon", new Date(2025 - 1900, 11, 12), new Date(2025 - 1900, 11, 5), new Date(2025 - 1900, 11, 10), new ArrayList<>(), 1);

        // Attempt to remove an entrant not in the list, should return false
        assertFalse(event.removeEntrantFromWaitList(nonExistentEntrant));
    }
}
