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
import java.util.Scanner;

public class UserInterface{

    // initialise variables
    private CentralBookingSystem cbs;
    private String userType;
    private int accessLevel;

    // constructor
    public UserInterface(String userType){
        this.cbs = new CentralBookingSystem();
        this.userType = userType;
        if(userType.equals("Customer")){
            accessLevel = 1;
        }
        else if(userType.equals("Receptionist")){
            accessLevel = 2;
        }
        else if(userType.equals("Manager")){
            accessLevel = 3;
        }
        else if(userType.equals("Test")){
            accessLevel = 4;
        }
        else{
            accessLevel = 0;
        }
    }

    public String getRequiredRoomType() {
        Scanner sc = new Scanner(System.in);
        System.out.println("***************************************\n\nWELCOME TO THE HOTEL RESERVATION SYSTEM\n\n***************************************");
        System.out.println("\n\nBefore we begin, please enter the type of room you wish to enquire about (Standard Double €130pn / Superior Double €180pn / King €220pn / Junior Suite €300pn / Executive Suite €350pn): ");
        String roomType = sc.nextLine();
        if(!(roomType.equalsIgnoreCase("Standard Double")||roomType.equalsIgnoreCase("Superior Double")||roomType.equalsIgnoreCase("King")||roomType.equalsIgnoreCase("Junior Suite")||roomType.equalsIgnoreCase("Executive Suite"))){
            System.out.println("Error: Invalid Input.\nPlease enter a valid room type.");
            getRequiredRoomType();
        }
        return roomType;
    }

    public void welcomeMenu(Room room){
        Scanner sc = new Scanner(System.in);
        String userInput;
        do {
            System.out.println("""
                In order for us to accurately direct your request, please select from the following options:\n\n
                1) Reserve Room\n
                2) Check Room Availability\n
                3) Display Available Dates\n\n
                4) Exit
                """);
            userInput = sc.nextLine();
            if (userInput.equals("1") || userInput.equalsIgnoreCase("reserve Room")) {
                reserveRoom(room);
            } else if (userInput.equals("2") || userInput.equalsIgnoreCase("Check Room Availability")) {
                checkAvailability(room);
            } else if (userInput.equals("3") || userInput.equalsIgnoreCase("Display Available Dates")) {
                displayAvailableDates(room);
            } else if (userInput.equals("4") || userInput.equalsIgnoreCase("Exit")) {
                System.out.println("Thank you for using the Hotel Reservation System.\nWe hope to see you again soon!");
                return;
            } else {
                System.out.println("Error: Invalid Input.\nPlease enter a valid option from the menu.");
            }
        } while (!userInput.equals("4") && !userInput.equalsIgnoreCase("Exit"));
    }

//////////////////////////////////////////////////////////////////// CHECK ROOM AVAILABILITY ///////////////////////////////////////////////////////////////


    // first we need to check if the room is available on the customer's selected dates
    // this method will pass the entered dates to the CentralBookingSystem class, retrieve 
    // and return the boolean value from the CentralBookingSystem class checkAvailability() method
    public void checkAvailability(Room room) {
        System.out.println("Hi! 👋\nI'm the Availability Checking Assistant.\nI just need to get a few details from you before I can process your request.\n\n");
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter check-in date in the format DD-MM-YYYY: ");
        String startDate = sc.nextLine();
        System.out.println("Please enter check-out date in the format DD-MM-YYYY: ");
        String endDate = sc.nextLine();
	boolean isAvailable = cbs.checkAvailability(startDate, endDate, room);
        // since this class is a user interface, we need to display a message to the user
        if(isAvailable){
            System.out.println("The dates(s) you have selected are available.");
        }
        else{
            System.out.println("The date(s) you have selected are unavailable.\nPlease select 'Display Available Dates' to see a list of all available dates.");
        }
    }

    public boolean checkAvailability(String startDate, String endDate, Room room){
	boolean isAvailable = cbs.checkAvailability(startDate, endDate, room);
        return isAvailable;
    }

//////////////////////////////////////////////////////////////////// RESERVE A ROOM /////////////////////////////////////////////////////////////////////////

    // this method will take in the customer's name, the start date and end date of the reservation
    // it will first check that there is availability on these dates by using the checkAvailability method
    // if there is availability, this method will then pass the customer name, start date and end date to 
    // the CentralBookingSystem class reserveRoom() method
	public void reserveRoom(Room room) {
        System.out.println("Hi! 👋\nI'm the Reservation Assistant.\nI just need to get a few details from you before I can process your request.\n\n");
        Scanner sc = new Scanner(System.in);
        if(userType.equals("Customer")||userType.equals("Test")){
            System.out.println("Please enter your name: ");
        }
        else if(userType.equals("Receptionist")){
            System.out.println("Please enter the guest name: ");
        }
        String customerName = sc.nextLine();
        System.out.println("Please enter a contact e-mail address: ");
        String customerEmail = sc.nextLine();
        System.out.println("Please enter check-in date in the format DD-MM-YYYY: ");
        String startDate = sc.nextLine();
        System.out.println("Please enter check-out date in the format DD-MM-YYYY: ");
        String endDate = sc.nextLine();
	// check availability
        boolean isAvailable = checkAvailability(startDate, endDate, room);
        // available, pass details to the CentralBookingSystem
        if(isAvailable){
            cbs.reserveRoom(customerName, customerEmail, startDate, endDate, room, this.userType);
        }
        else{
            System.out.println("The date(s) you have selected are unavailable.\nPlease select 'Display Available Dates' to see a list of all available dates.");
        }
    }

//////////////////////////////////////////////////////////////////// CONFIRM RESERVATION ///////////////////////////////////////////////////////////////////////

    // this method will accept a Reservation object as a parameter. 
    // if user is logged in as a receptionist, request confirmation via user input
    public boolean requestConfirmation(Reservation reservation) {
        boolean isConfirmed = true;
        if(!(userType.equals("Test"))){
        isConfirmed = false;
        }
        if(userType.equals("Receptionist")) {
            Scanner sc = new Scanner(System.in);
            System.out.println("\n***********\n\nNOTIFICATION\n\n***********\n\nA new reservation has been made:\n\n" + reservation.toString() + "\n\nConfirm?\nY/N");
            String userInput = sc.nextLine(); // Store the user input
            // if user input is neither "Y" nor "N", display error message and try again
            while(!(userInput.equalsIgnoreCase("Y") || userInput.equalsIgnoreCase("N"))) {
                System.out.println("Error: Invalid Input.\nPlease enter either 'Y' to confirm, or 'N' to deny.\nNew reservation: " + reservation.toString() + "\nConfirm? Y/N");
                userInput = sc.nextLine(); // Prompt again and store the new input
            }
            // if receptionist confirms booking, set isConfirmed = true
            if(userInput.equalsIgnoreCase("Y")) {
                isConfirmed = true;
            }
            // if receptionist rejects booking, set isConfirmed = false
            else {
                isConfirmed = false;
            }
        }
        else if(userType.equals("Customer")){
            System.out.println("Awaiting action by the hotel.\nYou will be notified once the reservation has been confirmed.");
        }
        return isConfirmed;
    }

    // this method will accept the result of the confirmation process, and the reservation details.
    // It will display a message to the user to let them know the outcome of the confirmation process.
    public void confirmReservation(boolean isConfirmed, Reservation reservation) {
	if(isConfirmed){
            System.out.println("** USER INTERFACE NOTIFICATION **\n\nYour booking has been confirmed.\nPlease find details of your reservation below\n\n" + reservation.toString());
        }
        else{
            System.out.println("** USER INTERFACE NOTIFICATION **\n\nThe reservation detailed below has been denied.\nPlease contact the hotel at your earliest convenience.\n\n" + reservation.toString());
        }
    }

//////////////////////////////////////////////////////////////////// DISPLAY AVAILABLE DATES ////////////////////////////////////////////////////////////////////

    // this method will call the showAvailableDates() method of the CentralBookingSystem class
    // and print a list of available dates to the console
    public void displayAvailableDates(Room room) {
	System.out.println("Current Availability: \n\nAvailable Dates:\n" + cbs.getAvailableDates(room) + "\n\nUnavailable Dates:\n" + room.reservedDates);
    }
        
/////////////////////////////////// THE FOLLOWING METHODS ARE NOT REQUIRED FOR THE MAKE RESERVATION USE CASE /////////////////////////////////////////////////////////////
        
    // The following methods have not been developed as they are not required for this use case.
    // They are included for display purposes only. All are listed as return type void, this does not
    // reflect the final return type once the methods have been developed
    
    public void accessReservation(){
        // not required
    }

    public void payInvoice(){
        // not required
    }

    public void displayHistory(){
        // not required
    }

    public void writeReview(){
        // not required
    }
    
    public void modifyReservation(){
        // not required
    }

    public void cancelReservation(){
    // not required
    }
}