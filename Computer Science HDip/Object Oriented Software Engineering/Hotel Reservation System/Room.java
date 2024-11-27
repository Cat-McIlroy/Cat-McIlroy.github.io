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
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Room {

    // initialise variables
    public int roomNumber;
    public String type;
    public double pricePerNight; 
    public ArrayList<String> availableDates; // Define as instance variable
    public ArrayList<String> reservedDates; // Define as instance variable  
    private Calendar calendar = new GregorianCalendar();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    // constructor
    public Room(String type){
        this.roomNumber = ((int)Math.floor(Math.random() * 100) + 1);
        this.type = type;
        if(type.equalsIgnoreCase("Superior Double")){
            this.pricePerNight = 180;
        }
        else if(type.equalsIgnoreCase("King")){
            this.pricePerNight = 220;
        }
        else if(type.equalsIgnoreCase("Junior Suite")){
            this.pricePerNight = 300;
        }
        else if(type.equalsIgnoreCase("Executive Suite")){
            this.pricePerNight = 350;
        }
        else{
            pricePerNight = 130;
        }

        // initialise availableDates as being all dates between 01/05/2024 and 01/05/2025
        this.availableDates  = new ArrayList<>();
        try {
            Date startDate = sdf.parse("01-05-2024");
            Date endDate = sdf.parse("01-05-2025");{
                calendar.setTime(startDate);
                while (calendar.getTime().before(endDate)){
                    Date date = calendar.getTime();
                    String formattedDate = sdf.format(date);
                    availableDates.add(formattedDate);
                    calendar.add(Calendar.DATE, 1);
                }
            }
        } 
        catch (ParseException e) {

        }

        this.reservedDates = new ArrayList<>();
        try {
            Date startDate = sdf.parse("22-04-2024");
            Date endDate = sdf.parse("01-05-2024");
            calendar.setTime(startDate);
            while (calendar.getTime().before(endDate)){
                Date date = calendar.getTime();
                String formattedDate = sdf.format(date);
                this.reservedDates.add(formattedDate);
                calendar.add(Calendar.DATE, 1);
            }
        } 
        catch (ParseException ex) {
        }
    }

//////////////////////////////////////////////////////////////////// DISPLAY ROOM AVAILABILITY ///////////////////////////////////////////////////////////////

    // this method will return an ArrayList of all currently available dates
    public ArrayList<String> showAvailableDates(Room room) {
        return room.availableDates;
    }


//////////////////////////////////////////////////////////////////// RESERVE A ROOM /////////////////////////////////////////////////////////////////////////

    // this method will accept the list of reservation dates from the CentralBookingSystem class reserveRoom() method.
    // It will add these dates to the reservedDates list, and remove them from the availableDates list.
    public void reserveRoom(Room room, Date startDate, Date endDate) {
        calendar.setTime(startDate);
        while (calendar.getTime().before(endDate)){
            // Create a new instance of Date within the loop
            Date date = calendar.getTime();
            String formattedDate = sdf.format(date);
            room.reservedDates.add(formattedDate);
            room.availableDates.remove(formattedDate);
            calendar.add(Calendar.DATE, 1);
        }
    }
}