package com.example.ocelotnovels.model;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    String eventName;
    Date eventDate;
    Date signUpStartDate;
    Date signUpEndDate;
    int waitListOpenSpots = -1;
    ArrayList<Entrant> waitList = new ArrayList<Entrant>();
    /**
     * Constructs an event for the organizers to be able to put out for people
     * @param eventName this is the name that the event is given by the organizer
     * @param eventDate this is when the event will actually take place
     * @param signUpStartDate this is when entrants can start signing up
     * @param signUpEndDate this is when entrants will no longer be able to sign up for the wait list
     */
    public Event(String eventName, Date eventDate,Date signUpStartDate, Date signUpEndDate){
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.signUpStartDate = signUpStartDate;
        this.signUpEndDate = signUpEndDate;
    }

    /**
     * Constructs an event for the organizers to be able to put out for people that includes a max size for the wait list
     * @param eventName this is the name that the event is given by the organizer
     * @param eventDate this is when the event will actually take place
     * @param signUpStartDate this is when entrants can start signing up
     * @param signUpEndDate this is when entrants will no longer be able to sign up for the wait list
     * @param waitListMax this is important if the organizer wants to limit the number of people that can join the waitlist
     *                    waitListMax defaults to -1 if there is no max given so you can test that way
     */
    public Event(String eventName, Date eventDate,Date signUpStartDate, Date signUpEndDate, int waitListMax){
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.signUpStartDate = signUpStartDate;
        this.signUpEndDate = signUpEndDate;
        this.waitListOpenSpots = waitListMax;
    }

    /**
     * This will add a user to the events waitList
     * @param user the user that is meant to be added to the waitlist
     * @throws Exception throws a generic exception if there is no room in the list
     */
    public void addEntrantToWaitList(Entrant user) {
        if(waitListOpenSpots != 0){
            if(!waitList.contains(user)){
                waitList.add(user);
                waitListOpenSpots --;
            }
        }else{
            throw new RuntimeException();
        }
    }
    public void removeEntrantFromWaitList(Entrant user){
        waitList.remove(user);
        if(waitListOpenSpots!=-1){
            waitListOpenSpots++;
        }
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getSignUpStartDate() {
        return signUpStartDate;
    }

    public void setSignUpStartDate(Date signUpStartDate) {
        this.signUpStartDate = signUpStartDate;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getSignUpEndDate() {
        return signUpEndDate;
    }

    public void setSignUpEndDate(Date signUpEndDate) {
        this.signUpEndDate = signUpEndDate;
    }

    public int getWaitListOpenSpots() {
        return waitListOpenSpots;
    }

    public boolean inWaitList(Entrant entrant){
        return waitList.contains(entrant);
    }

}
