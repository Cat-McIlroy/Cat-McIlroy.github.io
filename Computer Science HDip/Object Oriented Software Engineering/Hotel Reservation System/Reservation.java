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
import java.text.DecimalFormat;

public class Reservation {

    // initialise variables
    public int reservationID;
    public String customerName;
    public String customerEmail;
    public String roomType;
    public ArrayList<String> reservationDates;
    public double pricePerNight;
    public double totalPrice;
    public int roomNumber;
    public boolean isConfirmed;

    // constructor
    public Reservation(String customerName, String customerEmail, ArrayList<String> reservationDates, int roomNumber, String roomType, double pricePerNight, double totalPrice){
        this.reservationID = (int)Math.floor((Math.random() * 10000) + 1);
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.reservationDates = reservationDates;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.totalPrice = totalPrice;
        this.isConfirmed = false;
    }

//////////////////////////////////////////////////////////////////// CONFIRM RESERVATION ///////////////////////////////////////////////////////////////////////

    // this method will take in a Reservation object as a parameter and change the isConfirmed attribute to true
    public void confirmReservation(Reservation reservation){
        reservation.isConfirmed = true;
    }

//////////////////////////////////////////////////////////////////// OVERRIDE TO STRING ///////////////////////////////////////////////////////////////////////

    // this method will output the reservation details as a String
    @Override
    public String toString(){
        DecimalFormat f = new DecimalFormat("##.00");
        String str = "";
        str += "Reservation Details\n\n********************\n\n";
        str += "Reservation ID: " + this.reservationID;
        str += "\nName: " + this.customerName;
        str += "\nE-mail: " + this.customerEmail;
        str += "\nDates: " + this.reservationDates;
        str += "\nRoom Number: " + this.roomNumber;
        str += "\nRoom Type: " + this.roomType;
        str += "\nPrice Per Night: €" + f.format(this.pricePerNight);
        str += "\n\nTotal Price: €" + f.format(this.totalPrice);
        return str;
    }
    
/////////////////////////////////// THE FOLLOWING METHODS ARE NOT REQUIRED FOR THE MAKE RESERVATION USE CASE /////////////////////////////////////////////////////////////
        
    // The following methods have not been developed as they are not required for this use case.
    // They are included for display purposes only. All are listed as return type void, this does not
    // reflect the final return type once the methods have been developed
    
    public void generateInvoice(){
        // not required
    }
}
