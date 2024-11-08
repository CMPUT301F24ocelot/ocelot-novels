package com.example.ocelotnovels;

import com.example.ocelotnovels.model.Event;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

public class EventTest {

    private Event eventWithCapacity;
    private Event eventWithoutCapacity;
    private String mockEntrant = "user1@gmail.com";
    private Date eventDate;
    private Date registrationOpen;
    private Date registrationClose;

    @Before
    public void setUp() {
        eventDate = new Date(2025 - 1900, 11, 12);
        registrationOpen = new Date(2025 - 1900, 11, 5);
        registrationClose = new Date(2025 - 1900, 11, 10);

        ArrayList<String> waitList = new ArrayList<>();
        ArrayList<String> selectedParticipants = new ArrayList<>();
        ArrayList<String> cancelledParticipants = new ArrayList<>();

        eventWithCapacity = new Event(
                "Conference",
                "Annual Conference",
                eventDate,
                registrationOpen,
                registrationClose,
                30,
                "http://example.com/poster.jpg",
                "organizer123",
                "New York",
                waitList,
                selectedParticipants,
                cancelledParticipants);

        eventWithoutCapacity = new Event(
                "Conference",
                "Annual Conference",
                eventDate,
                registrationOpen,
                registrationClose,
                "http://example.com/poster.jpg",
                "organizer123",
                "New York",
                waitList,
                selectedParticipants,
                cancelledParticipants);
    }

    @Test
    public void testConstructorWithCapacity() {
        assertEquals("Conference", eventWithCapacity.getEventName());
        assertEquals("Annual Conference", eventWithCapacity.getEventDescription());
        assertEquals(eventDate, eventWithCapacity.getEventDate());
        assertEquals(registrationOpen, eventWithCapacity.getRegistrationOpen());
        assertEquals(registrationClose, eventWithCapacity.getRegistrationClose());
        assertEquals(30, eventWithCapacity.getEventCapacity());
        assertEquals("http://example.com/poster.jpg", eventWithCapacity.getEventPosterUrl());
        assertEquals("organizer123", eventWithCapacity.getOrganizerId());
        assertEquals("New York", eventWithCapacity.getEventLocation());
    }

    @Test
    public void testConstructorWithoutCapacity() {
        assertEquals("Conference", eventWithoutCapacity.getEventName());
        assertEquals("Annual Conference", eventWithoutCapacity.getEventDescription());
        assertEquals(eventDate, eventWithoutCapacity.getEventDate());
        assertEquals(registrationOpen, eventWithoutCapacity.getRegistrationOpen());
        assertEquals(registrationClose, eventWithoutCapacity.getRegistrationClose());
        assertEquals(-1, eventWithoutCapacity.getEventCapacity()); // Default capacity
        assertEquals("http://example.com/poster.jpg", eventWithoutCapacity.getEventPosterUrl());
        assertEquals("organizer123", eventWithoutCapacity.getOrganizerId());
        assertEquals("New York", eventWithoutCapacity.getEventLocation());
    }

    @Test
    public void testAddEntrantToWaitList() {
        assertTrue(eventWithCapacity.addEntrantToWaitList(mockEntrant));
        assertTrue(eventWithCapacity.getWaitList().contains(mockEntrant));
        assertEquals(29, eventWithCapacity.getEventCapacity());
    }

    @Test
    public void testAddEntrantToFullWaitList() {
        eventWithCapacity.setEventCapacity(0);
        assertFalse(eventWithCapacity.addEntrantToWaitList(mockEntrant));
        assertFalse(eventWithCapacity.getWaitList().contains(mockEntrant));
    }

    @Test
    public void testRemoveEntrantFromWaitList() {
        eventWithCapacity.addEntrantToWaitList(mockEntrant);
        assertTrue(eventWithCapacity.removeEntrantFromWaitList(mockEntrant));
        assertFalse(eventWithCapacity.getWaitList().contains(mockEntrant));
        assertEquals(30, eventWithCapacity.getEventCapacity());
    }

    @Test
    public void testRemoveNonExistentEntrantFromWaitList() {
        assertFalse(eventWithCapacity.removeEntrantFromWaitList("nonexistent@gmail.com"));
        assertEquals(30, eventWithCapacity.getEventCapacity());
    }

    @Test
    public void testAddSelectedParticipant() {
        assertTrue(eventWithCapacity.addSelectedParticipant(mockEntrant));
        assertTrue(eventWithCapacity.getSelectedParticipants().contains(mockEntrant));
    }

    @Test
    public void testAddDuplicateSelectedParticipant() {
        eventWithCapacity.addSelectedParticipant(mockEntrant);
        assertFalse(eventWithCapacity.addSelectedParticipant(mockEntrant));
    }

    @Test
    public void testRemoveSelectedParticipant() {
        eventWithCapacity.addSelectedParticipant(mockEntrant);
        assertTrue(eventWithCapacity.removeSelectedParticipant(mockEntrant));
        assertFalse(eventWithCapacity.getSelectedParticipants().contains(mockEntrant));
    }

    @Test
    public void testRemoveNonExistentSelectedParticipant() {
        assertFalse(eventWithCapacity.removeSelectedParticipant("nonexistent@gmail.com"));
    }
}
