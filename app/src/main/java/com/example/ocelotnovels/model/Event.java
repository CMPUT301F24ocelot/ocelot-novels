package com.example.ocelotnovels.model;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    String eventName;
    Date eventDate;
    Date signUpStartDate;
    Date signUpEndDate;
    int waitListOpenSpots = -1;
    ArrayList<String> waitList ;
    /**
     * Constructs an event for the organizers to be able to put out for people
     * @param eventName this is the name that the event is given by the organizer
     * @param eventDate this is when the event will actually take place
     * @param signUpStartDate this is when entrants can start signing up
     * @param signUpEndDate this is when entrants will no longer be able to sign up for the wait list
     * @param waitlist  this is the list of deviceId (Entrants) that want to join the event
     */
    public Event(String eventName, Date eventDate,Date signUpStartDate, Date signUpEndDate,ArrayList<String> waitlist){
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.signUpStartDate = signUpStartDate;
        this.signUpEndDate = signUpEndDate;
        this.waitList = waitlist;
    }

    /**
     * Constructs an event for the organizers to be able to put out for people that includes a max size for the wait list
     * @param eventName this is the name that the event is given by the organizer
     * @param eventDate this is when the event will actually take place
     * @param signUpStartDate this is when entrants can start signing up
     * @param signUpEndDate this is when entrants will no longer be able to sign up for the wait list
     * @param waitlist  this is the list of deviceIds (Entrants) that want to join the event
     * @param waitListMax this is important if the organizer wants to limit the number of people that can join the waitlist
     *                    waitListMax defaults to -1 if there is no max given so you can test that way
     */
    public Event(String eventName, Date eventDate,Date signUpStartDate, Date signUpEndDate,ArrayList<String> waitlist, int waitListMax){
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.signUpStartDate = signUpStartDate;
        this.signUpEndDate = signUpEndDate;
        this.waitList= waitlist;
        this.waitListOpenSpots = waitListMax;
    }

    /**
     * This will add a user to the events waitList
     * @param user the user that is meant to be added to the waitlist
     * @param user the user to be added to the waitlist, represented by their device ID
     * @return true if the user was successfully added to the waitlist,
     *         false if the waitlist is full or the user is already on the waitlist
     */
    public boolean addEntrantToWaitList(String user) {
        if(waitListOpenSpots != 0){
            if(!waitList.contains(user)){
                waitList.add(user);
                waitListOpenSpots --;
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
            if (waitListOpenSpots != -1) {
                waitListOpenSpots++;
            }
            return true;
        }
        return false;
    }

    /**
     * Retrieves the name of the event.
     *
     * @return the event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the name of the event.
     *
     * @param eventName the new name for the event
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Retrieves the date when sign-ups for the event begin.
     *
     * @return the sign-up start date
     */
    public Date getSignUpStartDate() {
        return signUpStartDate;
    }

    /**
     * Sets the date when sign-ups for the event begin.
     *
     * @param signUpStartDate the new sign-up start date
     */
    public void setSignUpStartDate(Date signUpStartDate) {
        this.signUpStartDate = signUpStartDate;
    }

    /**
     * Retrieves the date of the event.
     *
     * @return the event date
     */
    public Date getEventDate() {
        return eventDate;
    }

    /**
     * Sets the date of the event.
     *
     * @param eventDate the new date for the event
     */
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    /**
     * Retrieves the date when sign-ups for the event end.
     *
     * @return the sign-up end date
     */
    public Date getSignUpEndDate() {
        return signUpEndDate;
    }

    /**
     * Sets the date when sign-ups for the event end.
     *
     * @param signUpEndDate the new sign-up end date
     */
    public void setSignUpEndDate(Date signUpEndDate) {
        this.signUpEndDate = signUpEndDate;
    }

    /**
     * Retrieves the number of open spots remaining on the waitlist.
     *
     * @return the number of open spots on the waitlist, or -1 if no limit is set
     */
    public int getWaitListOpenSpots() {
        return waitListOpenSpots;
    }

    /**
     * Checks if a specific entrant is on the event's waitlist.
     *
     * @param entrant the entrant to check, represented by their device ID
     * @return {@code true} if the entrant is on the waitlist, {@code false} otherwise
     */
    public boolean inWaitList(String entrant) {
        return waitList.contains(entrant);
    }

    /**
     * Retrieves the list of entrants on the waitlist.
     *
     * @return the waitlist, represented as an {@link ArrayList} of device IDs
     */
    public ArrayList<String> getWaitList() {
        return waitList;
    }

    /**
     * Sets a new list of entrants as the event's waitlist.
     *
     * @param waitList the new waitlist, represented as an {@link ArrayList} of device IDs
     */
    public void setWaitList(ArrayList<String> waitList) {
        this.waitList = waitList;
    }

}