/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package hotel.reservation.system;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author catherinemcilroy
 */
public class CentralBookingSystemTest {
    
    private CentralBookingSystem cbs = new CentralBookingSystem();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    
    public CentralBookingSystemTest() {
    }

    /**
     * Test of checkAvailability method, of class CentralBookingSystem.
     * All dates between 22-04-2024 and 30-04-2024 inclusive should be unavailable.
     * All dates between 01-05-2024 and 01-05-2025 inclusive should be available.
     * There are three different possible scenarios.
     * Test Case 1: date range falls fully within unavailable dates.
     *      Start Date: 22-04-2024
     *      End Date: 30-04-2024
     *      Expected Result: false
     * Test Case 2: date range falls on both available and unavailable dates.
     *      Start Date: 30-04-2024
     *      End Date: 02-05-2024
     *      Expected Result: false
     * Test Case 3: date range falls fully within available dates.
     *      Start Date: 01-05-2024
     *      End Date: 06-05-2024
     *      Expected Result: true
     */
    @Test
    public void testCheckAvailability() {
        System.out.println("Testing the checkAvailability() method of the CentralBookingSystem class.");
        Room room = new Room("Standard Double");
        //////////////////////////////////////// TEST CASE 1 ///////////////////////////////////////
        String start = "22-04-2024";
        String end = "30-04-2024";
        boolean expResult = false;
        boolean result = cbs.checkAvailability(start, end, room);
        assertTrue(expResult == result);
        //////////////////////////////////////// TEST CASE 2 ////////////////////////////////////
        start = "30-04-2024";
        end = "02-05-2024";
        expResult = false;
        result = cbs.checkAvailability(start, end, room);
        assertTrue(expResult == result);
        //////////////////////////////////////// TEST CASE 3 ////////////////////////////////////
        start = "01-05-2024";
        end = "06-05-2024";
        expResult = true;
        result = cbs.checkAvailability(start, end, room);
        assertTrue(expResult == result);
    }

//    /**
//     * Test of reserveRoom() method, of class CentralBookingSystem.
//     * Test Case 1: Run using userType "Receptionist", with user input "N" to deny reservation.
//     * After method has been called, the dates between start and end inclusive should still be available, as 
//     * reservation has been denied.
//            * userType: "Receptionist"
//            * User Input: "N"
//            * Expected result: true   
//     * Test Case 2: Run using userType "Receptionist", with user input "Y" to confirm reservation.
//     * After method has been called, the dates between start and end inclusive should still be unavailable, as 
//     * reservation has been confirmed.
//            * userType: "Receptionist"
//            * User Input: "Y"
//            * Expected result: false  
//     * Test Case 3: Run using userType "Test", this will automatically confirm reservation without requiring user input
//     * After the method has been run, the dates between start and end inclusive should be unavailable.
//            * userType: "Test"
//            * Expected result: false
//     */
    
    @Test
    public void testReserveRoom() {
        System.out.println("Testing the reserveRoom() method of the CentralBookingSystem class.");
        Room room = new Room("Standard Double");
        String customerName = "Catherine McIlroy";
        String customerEmail = "x23173190@student.ncirl.ie";
        String start = "01-05-2024";
        String end = "06-05-2024";
        ////////////////////////////////////// TEST CASE 1 /////////////////////////////////////////
        mockUserInput("N");
        String userType = "Receptionist";
        cbs.reserveRoom(customerName, customerEmail, start, end, room, userType);
        assertTrue(cbs.checkAvailability("01-05-2024", "06-05-2024", room));
        ////////////////////////////////////// TEST CASE 2 /////////////////////////////////////////
        mockUserInput("Y");
        cbs.reserveRoom(customerName, customerEmail, start, end, room, userType);
        assertFalse(cbs.checkAvailability("01-05-2024", "06-05-2024", room)); 
//      ////////////////////////////////////// TEST CASE 3 ///////////////////////////////////////
        Room room2 = new Room("Standard Double");
        userType = "Test";
        cbs.reserveRoom(customerName, customerEmail, start, end, room2, userType);
        assertFalse(cbs.checkAvailability("01-05-2024", "06-05-2024", room2));    
    }
//
//    /**
//     * Test of createReservation method, of class CentralBookingSystem.
//       * Ensure created Reservation matches expected Reservation.
//       * Each new Reservation object is assigned a random reservationID between 1 and 10,000
//       * Therefore the expected and actual Reservation objects cannot be compared directly.
//       * We will instead compare various details of the expect and actual results including:
//            * Customer Name
//            * Customer Email
//            * Reservation Dates
//            * Room Type
//            * Price per Night
//            * Total Price
//     */
    @Test
    public void testCreateReservation() {
        System.out.println("Testing the createReservation() method of the CentralBookingSystem class.");
        String customerName = "Catherine McIlroy";
        String customerEmail = "x23173190@student.ncirl.ie";
        ArrayList<String> reservationDates = new ArrayList<>(Arrays.asList("01-05-2024", "02-05-2024", "03-05-2024", "04-05-2024", "05-05-2024"));
        try {
        Date startDate = sdf.parse("01-05-2024");
        Date endDate = sdf.parse("06-05-2024");
        Room room = new Room("Standard Double");
        String userType = "Test";
        Reservation reservation = cbs.createReservation(customerName, customerEmail, reservationDates, startDate, endDate, room, userType);
        assertTrue(customerName.equals(reservation.customerName));
        assertTrue(customerEmail.equals(reservation.customerEmail));
        assertTrue(reservationDates == reservation.reservationDates);
        assertTrue(room.type.equals(reservation.roomType));
        // testing price per night and total price for each room type
        // Standard Double (€130pn) for 5 nights
        // Expected Total Price = €650
        assertTrue(reservation.pricePerNight == 130.00);
        assertTrue(reservation.totalPrice == 650.00);
        // Superior Double (€180pn) for 5 nights
        // Expected Total Price = €900
        Room supRoom = new Room("Superior Double");
        reservation = cbs.createReservation(customerName, customerEmail, reservationDates, startDate, endDate, supRoom, userType);
        assertTrue(reservation.pricePerNight == 180.00);
        assertTrue(reservation.totalPrice == 900.00);
        // King (€220pn) for 5 nights
        // Expected Total Price = €1,100
        Room kingRoom = new Room("King");
        reservation = cbs.createReservation(customerName, customerEmail, reservationDates, startDate, endDate, kingRoom, userType);
        assertTrue(reservation.pricePerNight == 220.00);
        assertTrue(reservation.totalPrice == 1100.00);
        // Junior Suite (€300pn) for 5 nights
        // Expected Total Price = €1,500
        Room junSuite = new Room("Junior Suite");
        reservation = cbs.createReservation(customerName, customerEmail, reservationDates, startDate, endDate, junSuite, userType);
        assertTrue(reservation.pricePerNight == 300.00);
        assertTrue(reservation.totalPrice == 1500.00);
        // Executive Suite (€350pn) for 5 nights
        // Expected Total Price = €1,750
        Room execSuite = new Room("Executive Suite");
        reservation = cbs.createReservation(customerName, customerEmail, reservationDates, startDate, endDate, execSuite, userType);
        assertTrue(reservation.pricePerNight == 350.00);
        assertTrue(reservation.totalPrice == 1750.00);
        } catch (ParseException e) {
        }

    }

//    /**
//     * Test of confirmReservation() method of class CentralBookingSystem.
//     * Test Case 1: 
//            * Call method using userType "Receptionist" and user input "Y"
//            * Expected result: true, as user input "Y" has confirmed the booking
//     * Test Case 2: 
//            * Call method using userType "Receptionist" and user input "N"
//            * Expected result: false, as user input "N" has denied the booking
//     * Test Case 3: 
//            * Call method using userType "Test" 
//            * Expected result: true, as no input required and booking automatically confirmed
    
    @Test
    public void testConfirmReservation() {
        System.out.println("Testing the confirmReservation() method of the CentralBookingSystem class.");
        String customerName = "Catherine McIlroy";
        String customerEmail = "x23173190@student.ncirl.ie";
        ArrayList<String> reservationDates = new ArrayList<>(Arrays.asList("01-05-2024", "02-05-2024", "03-05-2024", "04-05-2024", "05-05-2024"));
        try {
        Date startDate = sdf.parse("01-05-2024");
        Date endDate = sdf.parse("06-05-2024");
        Room room = new Room("Standard Double");
//      ////////////////////////////////////// TEST CASE 1 /////////////////////////////////////////
        String userType = "Receptionist";
        mockUserInput("Y");
        Reservation reservation = cbs.createReservation(customerName, customerEmail, reservationDates, startDate, endDate, room, userType);
        // make sure isConfirmed is set to false before running confirmReservation method
        mockUserInput("Y");
        reservation.isConfirmed = false;
        cbs.confirmReservation(reservation, startDate, endDate, room, userType);
        assertTrue(reservation.isConfirmed);
//      ////////////////////////////////////// TEST CASE 2 /////////////////////////////////////////
        mockUserInput("N");
        reservation.isConfirmed = false;
        cbs.confirmReservation(reservation, startDate, endDate, room, userType);
        assertFalse(reservation.isConfirmed);
//      ////////////////////////////////////// TEST CASE 3 /////////////////////////////////////////
        userType = "Test";
        reservation.isConfirmed = false;
        cbs.confirmReservation(reservation, startDate, endDate, room, userType);
        assertTrue(reservation.isConfirmed);
        }
        catch (ParseException e) {
        }
    }

//    /**
//     * Test of emailCustomer method, of class CentralBookingSystem.
//     * Test Case 1:
//          isConfirmed = true
//          Expected result: confirmedMessage
//     * Test Case 2:
//          isConfirmed = false
//          Expected result: deniedMessage
//     */
    @Test
    public void testEmailCustomer() {
        System.out.println("Testing the emailCustomer() method of the CentralBookingSystem class.");
        String customerName = "Catherine McIlroy";
        String customerEmail = "x23173190@student.ncirl.ie";
        ArrayList<String> reservationDates = new ArrayList<>(Arrays.asList("01-05-2024", "02-05-2024", "03-05-2024", "04-05-2024", "05-05-2024"));
        try {
        Date startDate = sdf.parse("01-05-2024");
        Date endDate = sdf.parse("06-05-2024");
        Room room = new Room("Standard Double");
        String userType = "Test";
        Reservation reservation = cbs.createReservation(customerName, customerEmail, reservationDates, startDate, endDate, room, userType);
//      ////////////////////////////////////////////// TEST CASE 1 //////////////////////////////////////////////////////
        boolean isConfirmed = true;
        String confirmedMessage = "** E-MAIL NOTIFICATION **\n\n" + customerEmail + "\n\nDear " + customerName + ",\n\nYour booking has been confirmed. Please find details of your reservation below: \n\n" 
            + reservation.toString();
        String result = cbs.emailCustomer(isConfirmed, reservation);
        assertTrue(confirmedMessage.equals(result));
//      ////////////////////////////////////////////// TEST CASE 2 //////////////////////////////////////////////////////
        isConfirmed = false;    
        String deniedMessage = "** E-MAIL NOTIFICATION **\n\n" + customerEmail + "\n\nDear " + customerName + ",\n\nThe reservation detailed below has been denied. Please contact the hotel at your earliest convenience.\n\n" 
            + reservation.toString();
        result = cbs.emailCustomer(isConfirmed, reservation);
        assertTrue(deniedMessage.equals(result));
        } 
        catch (ParseException e) {
        }
    }

//    /**
//     * Test of getAvailableDates method, of class CentralBookingSystem.
//     * Should return list of dates between 01-05-2024 to 01-05-2025 inclusive
//     * Expected result: true
//     */
    @Test
    public void testGetAvailableDates() {
        System.out.println("Testing the getAvailableDates() method of the CentralBookingSystem class.");
        Calendar calendar = new GregorianCalendar();
        Room room = new Room("Standard Double");
        ArrayList<String> expResult = new ArrayList<>();
        try {
            Date startDate = sdf.parse("01-05-2024");
            Date endDate = sdf.parse("01-05-2025");{
                calendar.setTime(startDate);
                while (calendar.getTime().before(endDate)){
                    Date date = calendar.getTime();
                    String formattedDate = sdf.format(date);
                    expResult.add(formattedDate);
                    calendar.add(Calendar.DATE, 1);
                }
            }
        } 
        catch (ParseException e) {
        }
        ArrayList<String> result = cbs.getAvailableDates(room);
        assertIterableEquals(expResult, result);

    }
    
    private void mockUserInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }
}
