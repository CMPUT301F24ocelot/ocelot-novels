package com.example.ocelotnovels;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.ocelotnovels.model.Entrant;

import java.util.Map;
public class EntrantTest {
    @Test
    public void testConstructor(){
        Entrant entrant = new Entrant("John","Doe","johndoe@example.com");
        assertEquals("John", entrant.getFirstName());
        assertEquals("Doe", entrant.getLastName());
        assertEquals("johndoe@example.com", entrant.getEmail());
        assertNull(entrant.getPhoneNumber());
    }

    @Test
    public void testConstructorWithPhone(){
        Entrant entrant = new Entrant("John","Doe","johndoe@example.com","+123456789");
        assertEquals("John", entrant.getFirstName());
        assertEquals("Doe", entrant.getLastName());
        assertEquals("johndoe@example.com", entrant.getEmail());
        assertNotNull(entrant.getPhoneNumber());
        assertEquals("+123456789",entrant.getPhoneNumber());
    }

    @Test
    public void testSetters(){
        Entrant entrant = new Entrant("John","Doe","johndoe@example.com","+123456789");
        entrant.setFirstName("Jane");
        entrant.setLastName("Lane");
        entrant.setEmail("janeDaLane@example.com");
        entrant.setPhoneNumber(null);
        assertNotEquals("John",entrant.getFirstName());
        assertEquals("Jane",entrant.getFirstName());
        assertNotEquals("Doe",entrant.getLastName());
        assertEquals("Lane",entrant.getLastName());
        assertEquals("janeDaLane@example.com",entrant.getEmail());
        assertNull(entrant.getPhoneNumber());
    }

    @Test
    public void testToString(){
        Entrant entrant = new Entrant("Mark", "Twain", "mark.twain@example.com");
        String expectedOutput = "Entrant{firstName='Mark', lastName='Twain', email='mark.twain@example.com'}";

        assertEquals(expectedOutput, entrant.toString());
    }
    @Test
    public void testToStringWithPhoneNumber() {
        Entrant entrant = new Entrant("Abra", "Cadabra", "abra.Cadabra@example.com", "2223334444");
        String expectedOutput = "Entrant{firstName='Abra', lastName='Cadabra', phoneNumber='2223334444', email='abra.Cadabra@example.com'}";

        assertEquals(expectedOutput, entrant.toString());
    }

    @Test
    public void testToMapWithoutPhoneNumber() {
        Entrant entrant = new Entrant("Charles", "Dickens", "charles.dickens@example.com");
        Map<String, Object> entrantMap = entrant.toMap();

        assertEquals("Charles", entrantMap.get("firstName"));
        assertEquals("Dickens", entrantMap.get("lastName"));
        assertEquals("charles.dickens@example.com", entrantMap.get("email"));
        assertFalse(entrantMap.containsKey("phoneNumber"));
    }

    @Test
    public void testToMapWithPhoneNumber() {
        Entrant entrant = new Entrant("Herman", "Melville", "herman.melville@example.com", "555-666-7777");
        Map<String, Object> entrantMap = entrant.toMap();

        assertEquals("Herman", entrantMap.get("firstName"));
        assertEquals("Melville", entrantMap.get("lastName"));
        assertEquals("herman.melville@example.com", entrantMap.get("email"));
        assertEquals("555-666-7777", entrantMap.get("phoneNumber"));
    }

}
