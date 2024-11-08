package com.example.ocelotnovels.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Represents an event with various details and participant lists.
 */
public class Event {
    /** The name of the event. */
    private String eventName;

    /** A description of the event. */
    private String eventDescription;

    /** The date and time of the event. */
    private Date eventDate;

    /** The date and time when registration opens. */
    private Date registrationOpen;

    /** The date and time when registration closes. */
    private Date registrationClose;

    /** The maximum capacity of participants for the event. */
    private int eventCapacity = -1;

    /** The URL of the event poster image. */
    private String eventPosterUrl;

    /** The ID of the organizer of the event. */
    private String organizerId;

    /** List of users on the waitlist for the event. */
    private ArrayList<String> waitList;

    /** List of selected participants for the event. */
    private ArrayList<String> selectedParticipants;

    /** List of users who have canceled their participation. */
    private ArrayList<String> cancelledParticipants;

    /** The location of the event. */
    private String eventLocation;

    /**
     * Constructs a new Event with all specified details.
     *
     * @param eventName             The name of the event.
     * @param eventDescription      A description of the event.
     * @param eventDate             The date and time of the event.
     * @param registrationOpen      The date and time when registration opens.
     * @param registrationClose     The date and time when registration closes.
     * @param eventCapacity         The maximum capacity of participants for the event, defaults to -1.
     * @param eventPosterUrl        The URL of the event poster image.
     * @param organizerId           The ID of the organizer of the event.
     * @param eventLocation         The location of the event.
     * @param waitList              List of users on the waitlist for the event.
     * @param selectedParticipants  List of selected participants for the event.
     * @param cancelledParticipants List of users who have canceled their participation.
     */
    public Event(String eventName, String eventDescription, Date eventDate, Date registrationOpen, Date registrationClose, int eventCapacity, String eventPosterUrl, String organizerId, String eventLocation, ArrayList<String> waitList, ArrayList<String> selectedParticipants, ArrayList<String> cancelledParticipants) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.registrationOpen = registrationOpen;
        this.registrationClose = registrationClose;
        this.eventCapacity = eventCapacity;
        this.eventPosterUrl = eventPosterUrl;
        this.organizerId = organizerId;
        this.eventLocation = eventLocation;
        this.waitList = waitList != null ? waitList : new ArrayList<>();
        this.selectedParticipants = selectedParticipants != null ? selectedParticipants : new ArrayList<>();
        this.cancelledParticipants = cancelledParticipants != null ? cancelledParticipants : new ArrayList<>();
    }


    /**
     * Constructs a new Event with all specified details except eventCapacity.
     *
     * @param eventName             The name of the event.
     * @param eventDescription      A description of the event.
     * @param eventDate             The date and time of the event.
     * @param registrationOpen      The date and time when registration opens.
     * @param registrationClose     The date and time when registration closes.
     * @param eventPosterUrl        The URL of the event poster image.
     * @param organizerId           The ID of the organizer of the event.
     * @param eventLocation         The location of the event.
     * @param waitList              List of users on the waitlist for the event.
     * @param selectedParticipants  List of selected participants for the event.
     * @param cancelledParticipants List of users who have canceled their participation.
     */
    public Event(String eventName, String eventDescription, Date eventDate, Date registrationOpen, Date registrationClose,  String eventPosterUrl, String organizerId, String eventLocation, ArrayList<String> waitList, ArrayList<String> selectedParticipants, ArrayList<String> cancelledParticipants) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.registrationOpen = registrationOpen;
        this.registrationClose = registrationClose;
        this.eventPosterUrl = eventPosterUrl;
        this.organizerId = organizerId;
        this.eventLocation = eventLocation;
        this.waitList = waitList != null ? waitList : new ArrayList<>();
        this.selectedParticipants = selectedParticipants != null ? selectedParticipants : new ArrayList<>();
        this.cancelledParticipants = cancelledParticipants != null ? cancelledParticipants : new ArrayList<>();
    }

    /**
     * This will add a user to the events waitList
     * @param user the user that is meant to be added to the waitlist
     * @param user the user to be added to the waitlist, represented by their device ID
     * @return true if the user was successfully added to the waitlist,
     *         false if the waitlist is full or the user is already on the waitlist
     */
    public boolean addEntrantToWaitList(String user) {
        if(eventCapacity != 0){
            if(!waitList.contains(user)){
                waitList.add(user);
                eventCapacity --;
                return true;
            }
        }
        return false;
    }
    /**
     * Removes a user from the event's waitlist if they are on it.
     * <p>
     * If the user is successfully removed, the number of open spots is incremented.
     * </p>
     *
     * @param user the user to be removed from the waitlist, represented by their device ID
     * @return {@code true} if the user was successfully removed from the waitlist,
     *         {@code false} if the user was not on the waitlist or if the waitlist was empty
     */
    public boolean removeEntrantFromWaitList(String user) {
        if (!waitList.isEmpty() && waitList.contains(user)) {
            waitList.remove(user);
            if (eventCapacity >= 0) {
                eventCapacity++;
            }
            return true;
        }
        return false;
    }

    // Getter and Setter methods

    /** @return The name of the event. */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the name of the event.
     *
     * @param eventName The new event name.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /** @return A description of the event. */
    public String getEventDescription() {
        return eventDescription;
    }

    /**
     * Sets the description of the event.
     *
     * @param eventDescription The new event description.
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    /** @return The date and time of the event. */
    public Date getEventDate() {
        return eventDate;
    }

    /**
     * Sets the date and time of the event.
     *
     * @param eventDate The new event date and time.
     */
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    /** @return The date and time when registration opens. */
    public Date getRegistrationOpen() {
        return registrationOpen;
    }

    /**
     * Sets the date and time when registration opens.
     *
     * @param registrationOpen The new registration open date and time.
     */
    public void setRegistrationOpen(Date registrationOpen) {
        this.registrationOpen = registrationOpen;
    }

    /** @return The date and time when registration closes. */
    public Date getRegistrationClose() {
        return registrationClose;
    }

    /**
     * Sets the date and time when registration closes.
     *
     * @param registrationClose The new registration close date and time.
     */
    public void setRegistrationClose(Date registrationClose) {
        this.registrationClose = registrationClose;
    }

    /** @return The maximum capacity of participants for the event. */
    public int getEventCapacity() {
        return eventCapacity;
    }

    /**
     * Sets the maximum capacity of participants for the event.
     *
     * @param eventCapacity The new event capacity.
     */
    public void setEventCapacity(int eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    /** @return The URL of the event poster image. */
    public String getEventPosterUrl() {
        return eventPosterUrl;
    }

    /**
     * Sets the URL of the event poster image.
     *
     * @param eventPosterUrl The new event poster URL.
     */
    public void setEventPosterUrl(String eventPosterUrl) {
        this.eventPosterUrl = eventPosterUrl;
    }

    /** @return The ID of the organizer of the event. */
    public String getOrganizerId() {
        return organizerId;
    }

    /**
     * Sets the ID of the organizer of the event.
     *
     * @param organizerId The new organizer ID.
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    /** @return The location of the event. */
    public String getEventLocation() {
        return eventLocation;
    }

    /**
     * Sets the location of the event.
     *
     * @param eventLocation The new event location.
     */
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    /** @return List of users on the waitlist. */
    public ArrayList<String> getWaitList() {
        return waitList;
    }

    /**
     * Sets the waitlist of users.
     *
     * @param waitList The new waitlist.
     */
    public void setWaitList(ArrayList<String> waitList) {
        this.waitList = waitList;
    }


    /** @return List of selected participants. */
    public ArrayList<String> getSelectedParticipants() {
        return selectedParticipants;
    }

    /**
     * Sets the list of selected participants.
     *
     * @param selectedParticipants The new list of selected participants.
     */
    public void setSelectedParticipants(ArrayList<String> selectedParticipants) {
        this.selectedParticipants = selectedParticipants;
    }

    /** @return List of canceled participants. */
    public ArrayList<String> getCancelledParticipants() {
        return cancelledParticipants;
    }

    /**
     * Sets the list of canceled participants.
     *
     * @param cancelledParticipants The new list of canceled participants.
     */
    public void setCancelledParticipants(ArrayList<String> cancelledParticipants) {
        this.cancelledParticipants = cancelledParticipants;
    }

    /**
     * Adds a user to the selected participants list if they are not already on it.
     *
     * @param user The user to add to the selected participants list.
     * @return True if the user was added; false otherwise.
     */
    public boolean addSelectedParticipant(String user) {
        if (!selectedParticipants.contains(user)) {
            selectedParticipants.add(user);
            return true;
        }
        return false;
    }

    /**
     * Removes a user from the selected participants list.
     *
     * @param user The user to remove from the selected participants list.
     * @return True if the user was removed; false otherwise.
     */
    public boolean removeSelectedParticipant(String user) {
        return selectedParticipants.remove(user);
    }

}