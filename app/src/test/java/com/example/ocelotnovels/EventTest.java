package com.example.ocelotnovels;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.ocelotnovels.model.*;

import java.util.Date;

public class EventTest {

    private Entrant mockEntrant(){
        Entrant entrant = new Entrant("Arnold","Button","button@gmail.com");
        return entrant;
    }
    @Test
    public void testConstructorNoMax(){
        Event event = new Event("Marathon",new Date(2025,11,12),new Date(2025,11,5),new Date(2025,11,10));
        assertEquals("Marathon", event.getEventName());
        assertEquals(new Date(2025,11,12),event.getEventDate());
        assertEquals(new Date(2025,11,5),event.getSignUpStartDate());
        assertEquals(new Date(2025,11,10),event.getSignUpEndDate());
    }

    @Test
    public void testConstructorWithMax(){
        Event event = new Event("Marathon",new Date(2025,11,12),new Date(2025,11,5),new Date(2025,11,10),30);
        assertEquals("Marathon", event.getEventName());
        assertEquals(new Date(2025,11,12),event.getEventDate());
        assertEquals(new Date(2025,11,5),event.getSignUpStartDate());
        assertEquals(new Date(2025,11,10),event.getSignUpEndDate());
        assertEquals(30,event.getWaitListOpenSpots());
    }
    @Test
    public void testAddWaitList(){
        Entrant entrant = mockEntrant();
        Event event = new Event("Marathon",new Date(2025,11,12),new Date(2025,11,5),new Date(2025,11,10),1);
        event.addEntrantToWaitList(entrant);
        assertEquals(0,event.getWaitListOpenSpots());
        entrant = mockEntrant();
        assertTrue(event.inWaitList(entrant));
    }

    @Test
    public void testAddWaitListException(){
        Entrant entrant = mockEntrant();
        Event event = new Event("Marathon",new Date(2025,11,12),new Date(2025,11,5),new Date(2025,11,10),1);
        event.addEntrantToWaitList(entrant);
        assertEquals(0,event.getWaitListOpenSpots());
        entrant = mockEntrant();
        assertTrue(event.inWaitList(entrant));
        Entrant finalEntrant = entrant;
        assertThrows(RuntimeException.class, ()->event.addEntrantToWaitList(finalEntrant));
    }

    @Test
    public void testDeleteEntrant(){
        Entrant entrant = mockEntrant();
        Event event = new Event("Marathon",new Date(2025,11,12),new Date(2025,11,5),new Date(2025,11,10),1);
        event.addEntrantToWaitList(entrant);
        assertEquals(0,event.getWaitListOpenSpots());
        event.removeEntrantFromWaitList(mockEntrant());
        assertEquals(1,event.getWaitListOpenSpots());
    }
}
