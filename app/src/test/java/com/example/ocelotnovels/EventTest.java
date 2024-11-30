package com.example.ocelotnovels;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.ocelotnovels.model.Event;

import java.util.ArrayList;
import java.util.Date;

public class EventTest {

    private Event event;
    private String eventId = "1";
    private String eventName = "Annual Tech Conference";
    private String eventDescription = "This is a description of the annual tech conference.";
    private Date eventDate;
    private Date registrationOpen;
    private String registrationClose = "2025-12-10";
    private long eventCapacity = 100;
    private String eventPosterUrl = "http://example.com/poster.jpg";
    private String organizerId = "organizer123";
    private String eventLocation = "Conference Center, Silicon Valley";
    private String qrHash = "12345ABCDE";
    private Boolean geolocationEnabled = true;
    private ArrayList<String> waitList;
    private ArrayList<String> selectedParticipants;
    private ArrayList<String> cancelledParticipants;

    @Before
    public void setUp() {
        eventDate = new Date();
        registrationOpen = new Date();

        waitList = new ArrayList<>();
        selectedParticipants = new ArrayList<>();
        cancelledParticipants = new ArrayList<>();

        event = new Event(eventId, eventName, eventDescription, eventDate, registrationOpen, registrationClose,
                eventCapacity, eventPosterUrl, organizerId, eventLocation, waitList,
                selectedParticipants, cancelledParticipants, qrHash, geolocationEnabled);
    }

    @Test
    public void constructor_initializesCorrectly() {
        assertEquals(eventName, event.getEventName());
        assertEquals(eventDescription, event.getEventDescription());
        assertEquals(eventDate, event.getEventDate());
        assertEquals(registrationOpen, event.getRegistrationOpen());
        assertEquals(registrationClose, event.getRegistrationClose());
        assertEquals(Long.valueOf(eventCapacity), event.getEventCapacity());
        assertEquals(eventPosterUrl, event.getEventPosterUrl());
        assertEquals(organizerId, event.getOrganizerId());
        assertEquals(eventLocation, event.getEventLocation());
        assertTrue(event.getWaitList().isEmpty());
        assertTrue(event.getSelectedParticipants().isEmpty());
        assertTrue(event.getCancelledParticipants().isEmpty());
        assertEquals(qrHash, event.getQrHash());
        assertEquals(geolocationEnabled, event.getGeolocationEnabled());
    }

    @Test
    public void addEntrantToWaitList_whenCapacityIsAvailable() {
        String mockEntrant = "user1@example.com";
        assertTrue(event.addEntrantToWaitList(mockEntrant));
        assertTrue(event.getWaitList().contains(mockEntrant));
        assertEquals(Long.valueOf(eventCapacity - 1), event.getEventCapacity());
    }

    @Test
    public void addEntrantToWaitList_whenFull() {
        event.setEventCapacity(30L);
        String mockEntrant = "user2@example.com";
        assertFalse(event.addEntrantToWaitList(mockEntrant));
        assertFalse(event.getWaitList().contains(mockEntrant));
    }

    @Test
    public void removeEntrantFromWaitList_whenPresent() {
        String mockEntrant = "user3@example.com";
        event.addEntrantToWaitList(mockEntrant);
        assertTrue(event.removeEntrantFromWaitList(mockEntrant));
        assertFalse(event.getWaitList().contains(mockEntrant));
        assertEquals(Long.valueOf(eventCapacity), event.getEventCapacity());
    }

    @Test
    public void removeEntrantFromWaitList_whenAbsent() {
        String mockEntrant = "user4@example.com";
        assertFalse(event.removeEntrantFromWaitList(mockEntrant));
        assertEquals(Long.valueOf(eventCapacity), event.getEventCapacity());
    }

    @Test
    public void addSelectedParticipant_whenNotAlreadySelected() {
        String participant = "participant@example.com";
        assertTrue(event.addSelectedParticipant(participant));
        assertTrue(event.getSelectedParticipants().contains(participant));
    }

    @Test
    public void addSelectedParticipant_whenAlreadySelected() {
        String participant = "participant@example.com";
        event.addSelectedParticipant(participant);
        assertFalse(event.addSelectedParticipant(participant));
    }

    @Test
    public void removeSelectedParticipant_whenPresent() {
        String participant = "participant@example.com";
        event.addSelectedParticipant(participant);
        assertTrue(event.removeSelectedParticipant(participant));
        assertFalse(event.getSelectedParticipants().contains(participant));
    }

    @Test
    public void removeSelectedParticipant_whenAbsent() {
        String participant = "unknown@example.com";
        assertFalse(event.removeSelectedParticipant(participant));
    }
}
