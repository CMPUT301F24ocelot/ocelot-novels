package com.example.ocelotnovels.model;

import java.util.ArrayList;

public class Event {
    ArrayList<Entrant> waitList = new ArrayList<Entrant>();

    public void addEntrantToWaitList(Entrant user){
        if(!waitList.contains(user)){
            waitList.add(user);
        }
    }
    public void removeEntrantFromWaitList(Entrant user){
        waitList.remove(user);
    }
}
