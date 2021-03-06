package com.example.book_master;

import com.example.book_master.models.Message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for MessageList
 * Notice functionality related to Firebase could not be tested (it could not connect to cloud)
 */
public class MessageTest {
    private Message mockMsg() {
        final String sender = "shrike";
        final String receiver = "shrike's friend";
        final String ISBN = "978-3-16-148410-0";
        final String status = "AVAILABLE";
        final String longitude = "2° 29' E";
        final String latitude = "47° 55' W";
        return new Message(sender, receiver, ISBN, status, longitude, latitude);
    }

    /**
     * Test:    getSender(), setSender(String sender),
     * getReceiver(), setReceiver(String receiver)
     * getISBN(), setISBN(String ISBN),
     * getStatus(), setStatus(String status),
     * getLongitude(), setLongitude(String longitude),
     * getLatitude(), setLatitude(String latitude)
     * getShownIndicator(), setShownIndicator(String shownIndicator)
     */
    @Test
    void testSetGet() {
        Message temp = mockMsg();
        assertTrue(temp.getSender().equalsIgnoreCase("shrike"));
        assertTrue(temp.getReceiver().equalsIgnoreCase("shrike's friend"));
        assertTrue(temp.getISBN().equalsIgnoreCase("978-3-16-148410-0"));
        assertTrue(temp.getStatus().equalsIgnoreCase("AVAILABLE"));
        assertTrue(temp.getLongitude().equalsIgnoreCase("2° 29' E"));
        assertTrue(temp.getLatitude().equalsIgnoreCase("47° 55' W"));
        assertTrue(temp.getShownIndicator().equalsIgnoreCase("NOTIFICATION_NOT_SHOWN"));

        temp.setSender("shrike's friend");
        temp.setReceiver("shrike");
        temp.setISBN("978-3-16-148410-2");
        temp.setStatus("REQUESTED");
        temp.setLongitude("47° 55' W");
        temp.setLatitude("2° 29' E");
        temp.setShownIndicator("NOTIFICATION_SHOWN");
        assertTrue(temp.getSender().equalsIgnoreCase("shrike's friend"));
        assertTrue(temp.getReceiver().equalsIgnoreCase("shrike"));
        assertTrue(temp.getISBN().equalsIgnoreCase("978-3-16-148410-2"));
        assertTrue(temp.getStatus().equalsIgnoreCase("REQUESTED"));
        assertTrue(temp.getLongitude().equalsIgnoreCase("47° 55' W"));
        assertTrue(temp.getLatitude().equalsIgnoreCase("2° 29' E"));
        assertTrue(temp.getShownIndicator().equalsIgnoreCase("NOTIFICATION_SHOWN"));
    }

    /**
     * Test: hashCode()
     */
    @Test
    void testHashCode() {
        Message temp1 = mockMsg();
        Message temp2 = mockMsg();
        assertTrue(temp1.hashCode() == temp2.hashCode());

        temp2.setStatus("Borrowed");
        assertTrue(temp1.hashCode() != temp2.hashCode());
    }
}