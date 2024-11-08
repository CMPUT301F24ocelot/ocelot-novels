package com.example.ocelotnovels;

import org.junit.Test;

import static org.junit.Assert.*;
import com.example.ocelotnovels.model.User;
import com.example.ocelotnovels.model.Entrant;

import java.util.Map;

public class UserTest {

    @Test
    public void testConstructor() {
        User entrant = new Entrant("John", "Doe", "johndoe@example.com");
        assertEquals("John", entrant.getFirstName());
        assertEquals("Doe", entrant.getLastName());
        assertEquals("johndoe@example.com", entrant.getEmail());
        assertNull(entrant.getPhoneNumber());
    }

    @Test
    public void testConstructorWithPhone() {
        User entrant = new Entrant("John", "Doe", "johndoe@example.com", "1234567890");
        assertEquals("John", entrant.getFirstName());
        assertEquals("Doe", entrant.getLastName());
        assertEquals("johndoe@example.com", entrant.getEmail());
        assertNotNull(entrant.getPhoneNumber());
        assertEquals("1234567890", entrant.getPhoneNumber());
    }

    @Test
    public void testSetters() {
        User entrant = new Entrant("John", "Doe", "johndoe@example.com", "1234567890");
        entrant.setFirstName("Jane");
        entrant.setLastName("Lane");
        entrant.setEmail("janedoe@example.com");
        entrant.setPhoneNumber(null);

        assertNotEquals("John", entrant.getFirstName());
        assertEquals("Jane", entrant.getFirstName());
        assertNotEquals("Doe", entrant.getLastName());
        assertEquals("Lane", entrant.getLastName());
        assertEquals("janedoe@example.com", entrant.getEmail());
        assertNull(entrant.getPhoneNumber());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInvalidFirstName() {
        User entrant = new Entrant("John", "Doe", "johndoe@example.com");
        entrant.setFirstName("");  // Should throw IllegalArgumentException
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInvalidLastName() {
        User entrant = new Entrant("John", "Doe", "johndoe@example.com");
        entrant.setLastName("");  // Should throw IllegalArgumentException
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInvalidEmail() {
        User entrant = new Entrant("John", "Doe", "johndoe@example.com");
        entrant.setEmail("invalid-email");  // Should throw IllegalArgumentException
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInvalidPhoneNumber() {
        User entrant = new Entrant("John", "Doe", "johndoe@example.com");
        entrant.setPhoneNumber("123");  // Should throw IllegalArgumentException (invalid phone number)
    }

    @Test
    public void testToString() {
        User entrant = new Entrant("Mark", "Twain", "mark.twain@example.com");
        String expectedOutput = "Entrant{firstName='Mark', lastName='Twain', email='mark.twain@example.com'}";
        assertEquals(expectedOutput, entrant.toString());
    }

    @Test
    public void testToStringWithPhoneNumber() {
        User entrant = new Entrant("Abra", "Cadabra", "abra.cadabra@example.com", "2223334444");
        String expectedOutput = "Entrant{firstName='Abra', lastName='Cadabra', phoneNumber='2223334444', email='abra.cadabra@example.com'}";
        assertEquals(expectedOutput, entrant.toString());
    }

    @Test
    public void testToMapWithoutPhoneNumber() {
        User entrant = new Entrant("Charles", "Dickens", "charles.dickens@example.com");
        Map<String, Object> entrantMap = entrant.toMap();

        assertEquals("Charles", entrantMap.get("firstName"));
        assertEquals("Dickens", entrantMap.get("lastName"));
        assertEquals("charles.dickens@example.com", entrantMap.get("email"));
        assertFalse(entrantMap.containsKey("phoneNumber"));
    }

    @Test
    public void testToMapWithPhoneNumber() {
        User entrant = new Entrant("Herman", "Melville", "herman.melville@example.com", "5556667777");
        Map<String, Object> entrantMap = entrant.toMap();

        assertEquals("Herman", entrantMap.get("firstName"));
        assertEquals("Melville", entrantMap.get("lastName"));
        assertEquals("herman.melville@example.com", entrantMap.get("email"));
        assertEquals("5556667777", entrantMap.get("phoneNumber"));
    }

    @Test
    public void testEqualsAndHashCode() {
        User entrant1 = new Entrant("John", "Doe", "johndoe@example.com");
        User entrant2 = new Entrant("John", "Doe", "johndoe@example.com");
        User entrant3 = new Entrant("Jane", "Doe", "janedoe@example.com");

        assertTrue(entrant1.equals(entrant2));  // Same first name, last name, and email
        assertFalse(entrant1.equals(entrant3)); // Different first name and email
        assertEquals(entrant1.hashCode(), entrant2.hashCode());  // Same hash code
        assertNotEquals(entrant1.hashCode(), entrant3.hashCode()); // Different hash codes
    }
}
