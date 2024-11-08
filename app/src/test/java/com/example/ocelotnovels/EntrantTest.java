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
        Entrant entrant = new Entrant("John","Doe","johndoe@example.com","1234567890");
        assertEquals("John", entrant.getFirstName());
        assertEquals("Doe", entrant.getLastName());
        assertEquals("johndoe@example.com", entrant.getEmail());
        assertNotNull(entrant.getPhoneNumber());
        assertEquals("1234567890",entrant.getPhoneNumber());
    }

    @Test
    public void testSetters(){
        Entrant entrant = new Entrant("John","Doe","johndoe@example.com","1234567890");
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
        Entrant entrant = new Entrant("Herman", "Melville", "herman.melville@example.com", "5556667777");
        Map<String, Object> entrantMap = entrant.toMap();

        assertEquals("Herman", entrantMap.get("firstName"));
        assertEquals("Melville", entrantMap.get("lastName"));
        assertEquals("herman.melville@example.com", entrantMap.get("email"));
        assertEquals("5556667777", entrantMap.get("phoneNumber"));
    }

    @Test
    public void testSetFirstNameInvalid() {
        Entrant entrant = new Entrant("John", "Doe", "johndoe@example.com");
        try {
            entrant.setFirstName(""); // Empty first name
            fail("Expected IllegalArgumentException for empty first name");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid First name!", e.getMessage());
        }

        try {
            entrant.setFirstName(null); // Null first name
            fail("Expected IllegalArgumentException for null first name");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid First name!", e.getMessage());
        }

        try {
            entrant.setFirstName("a".repeat(101)); // Exceeding 100 characters
            fail("Expected IllegalArgumentException for first name too long");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid First name!", e.getMessage());
        }
    }

    @Test
    public void testSetLastNameInvalid() {
        Entrant entrant = new Entrant("John", "Doe", "johndoe@example.com");
        try {
            entrant.setLastName(""); // Empty last name
            fail("Expected IllegalArgumentException for empty last name");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid Last name!", e.getMessage());
        }

        try {
            entrant.setLastName(null); // Null last name
            fail("Expected IllegalArgumentException for null last name");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid Last name!", e.getMessage());
        }

        try {
            entrant.setLastName("a".repeat(101)); // Exceeding 100 characters
            fail("Expected IllegalArgumentException for last name too long");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid Last name!", e.getMessage());
        }
    }

    @Test
    public void testSetEmailInvalid() {
        Entrant entrant = new Entrant("John", "Doe", "johndoe@example.com");
        try {
            entrant.setEmail("invalid-email"); // Invalid email format
            fail("Expected IllegalArgumentException for invalid email");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid email address!", e.getMessage());
        }

        try {
            entrant.setEmail(null); // Null email
            fail("Expected IllegalArgumentException for null email");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid email address!", e.getMessage());
        }
    }

    @Test
    public void testSetPhoneNumberInvalid() {
        Entrant entrant = new Entrant("John", "Doe", "johndoe@example.com");
        try {
            entrant.setPhoneNumber("12345"); // Too short phone number
            fail("Expected IllegalArgumentException for invalid phone number");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid phone number format!", e.getMessage());
        }

        try {
            entrant.setPhoneNumber("123456789012345"); // Too long phone number
            fail("Expected IllegalArgumentException for invalid phone number");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid phone number format!", e.getMessage());
        }

        try {
            entrant.setPhoneNumber("abcde12345"); // Invalid characters in phone number
            fail("Expected IllegalArgumentException for invalid phone number format");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid phone number format!", e.getMessage());
        }
    }

}
