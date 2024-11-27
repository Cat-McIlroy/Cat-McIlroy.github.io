/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotel.reservation.system;

/**
 *
 * @author catherinemcilroy
 */
// import necessary classes
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class CentralBookingSystem {

    private Calendar calendar = new GregorianCalendar();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    private UserInterface ui;

    // constructor
    public CentralBookingSystem(){
    }

//////////////////////////////////////////////////////////////////// CHECK ROOM AVAILABILITY ///////////////////////////////////////////////////////////////

    // this method will take in two dates as parameters, startDate and endDate
    // it will get the list of available dates from the Room class and compare
    // these to the list of dates for reservation
    // it will return a boolean value is Available
    public boolean checkAvailability(String start, String end, Room room) {
    // initialise isAvailable as true
        boolean isAvailable = true;
        // get list of available dates from Room class
        ArrayList<String> availableDates = room.showAvailableDates(room);
        // create list of dates to be reserved, inclusive of startDate and endDate
        ArrayList<String> reservationDates = new ArrayList<>();
        try {
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);
            calendar.setTime(startDate);

            while (calendar.getTime().before(endDate)){
                Date date = calendar.getTime();
                String formattedDate = sdf.format(date);
                reservationDates.add(formattedDate);
                calendar.add(Calendar.DATE, 1);
            }
        } 
        catch (ParseException e) {
        }
        // compare dates to be reserved with list of available dates
        // for each date in list of dates to be reserved, if it is not 
        // contained in the list of available dates, the room is not available
        // for the entirety of the desired reservation period, set isAvailable = false
        if(!(availableDates.containsAll(reservationDates))){
            isAvailable = false;
        }
        return isAvailable;
    }

//////////////////////////////////////////////////////////////////// RESERVE A ROOM /////////////////////////////////////////////////////////////////////////

    // this method will accept the customer name, start date and end date of reservation 
    // from the UserInterface class. It will create a list of dates inclusive of the start
    // and end dates, and pass this list to the Room class reserveRoom() method.
    // The customer name will be passed to the createReservation method.
    public void reserveRoom(String customerName, String customerEmail, String start, String end, Room room, String userType) {
        // create list of dates to be reserved, inclusive of startDate and endDate
	ArrayList<String> reservationDates = new ArrayList<String>();
        try {
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);
            calendar.setTime(startDate);
            while (calendar.getTime().before(endDate)){
                Date date = calendar.getTime();
                String formattedDate = sdf.format(date);
                reservationDates.add(formattedDate);
                calendar.add(Calendar.DATE, 1);
            }
            // pass customer name and reservation dates to the createReservation() method
            createReservation(customerName, customerEmail, reservationDates, startDate, endDate, room, userType);
        } 
        catch (ParseException e) {
        }
    }

//////////////////////////////////////////////////////////////////// CREATE NEW RESERVATION ///////////////////////////////////////////////////////////////////////

    // this method will create a new instance of the Reservation class, with the attributes
    // customer name, reservation dates, room number, room type, price per night and total price.

    public Reservation createReservation(String customerName, String customerEmail, ArrayList<String> reservationDates, Date startDate, Date endDate, Room room, String userType) {
        String name = customerName;
        String email = customerEmail;
        ArrayList<String> dates = reservationDates;
        int roomNumber = room.roomNumber;
        String roomType = room.type;
        double pricePerNight = room.pricePerNight;
        // total price is pricePerNight multiplied by number of reservation dates -1 as the guest 
        // does not stay overnight on the last date
        double totalPrice = pricePerNight * (reservationDates.size());
        // instantiate new Reservation object with the specified attributes
        Reservation reservation = new Reservation(name, email, dates, roomNumber, roomType, pricePerNight, totalPrice);
        // pass reservation details to confirmReservation method
        confirmReservation(reservation, startDate, endDate, room, userType);
        return reservation;
    }

//////////////////////////////////////////////////////////////////// CONFIRM RESERVATION ///////////////////////////////////////////////////////////////////////

    // this method will communicate with the UserInterface class to trigger request of confirmation 
    // by a receptionist. It accepts a Reservation object as a parameter.
    // It will call the confirmReservation() method of the UserInterface class to display the
    // outcome to the customer, and will call emailCustomer() method to send this message as an email
    public void confirmReservation(Reservation reservation, Date startDate, Date endDate, Room room, String userType) {
        ui = new UserInterface(userType);
        boolean isConfirmed = ui.requestConfirmation(reservation);
        // if reservation is confirmed, call confirmReservation method of Reservation class to 
        // change reservation isConfirmed attribute to true.
        if(isConfirmed){
            reservation.confirmReservation(reservation);
            // pass this list to the Room class reserveRoom() method
            room.reserveRoom(room, startDate, endDate);
        }
        // call confirmReservation() method of UserInterface class
        ui.confirmReservation(isConfirmed, reservation);
        // call emailCustomer() method, to communicate result of reservation process to customer
        emailCustomer(isConfirmed, reservation);
    }

/////////////////////////////////////////////////////////// EMAIL RESERVATION OUTCOME TO CUSTOMER ///////////////////////////////////////////////////////////////

    public String emailCustomer(boolean isConfirmed, Reservation reservation) {
	// get email address associated with reservation
        String customerEmail = reservation.customerEmail;
        String customerName = reservation.customerName;
        String message;
        // for the purposes of this project, the email to the customer will be displayed as 
        // a message to the console
        if(isConfirmed){
            message = "** E-MAIL NOTIFICATION **\n\n" + customerEmail + "\n\nDear " + customerName + ",\n\nYour booking has been confirmed. Please find details of your reservation below: \n\n" 
            + reservation.toString();
        }
        else{
            message = "** E-MAIL NOTIFICATION **\n\n" + customerEmail + "\n\nDear " + customerName + ",\n\nThe reservation detailed below has been denied. Please contact the hotel at your earliest convenience.\n\n" 
            + reservation.toString();
        }
        System.out.println(message);
        return message;
    }

//////////////////////////////////////////////////////////////////// DISPLAY AVAILABLE DATES ////////////////////////////////////////////////////////////////////

    // this method will call the showAvailableDates() method of the Room class
    // and will return a list of available dates
    public ArrayList<String> getAvailableDates(Room room) {
	ArrayList<String> availableDates = room.showAvailableDates(room);
        return availableDates;
    }
}
