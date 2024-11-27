/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ie.ncirl.datastructuresca2024;

/**
 * This main method will demonstrate the methods defined in the PrintServer class.
 * A new instance of class PrintServer will be created.
 * 
 * @author catherinemcilroy
 */
public class PrintServerApp {
    
    public static void main(String[] args){
        
        // instantiate new PrintServer object 
        PrintServer printer = new PrintServer();

        // add three print jobs to the Queue
        printer.submitPrintJob("Document");
        printer.submitPrintJob("Invoice");
        printer.submitPrintJob("Instructions");
        
        // test the size method of the PrintServer class
        System.out.println(printer.size()); // should output 3 to the console
        
        // test the pollNextJob method of the PrintServer class
        System.out.println(printer.pollNextJob()); // should output the first job in the Queue to the console, but not remove it
        
        // show a list of the jobs to be printed in the correct order
        System.out.println(printer.toString()); // should output a String representation of the Queue to the console
        
        // simulate how the print server would process each of the jobs in the correct FIFO sequence 
        /* As can be seen above, "Document" is the first job to be added to the queue, followed by
        "Invoice", then "Instructions" is added last. 
        When calling the printNextJob() method, we would therefore expect "Document" to be printed first,
        followed by "Invoice", then lastly by "Instructions". 
        If printNextJob() is called a fourth time, the method should throw a NoSuchElementException */
        System.out.println(printer.printNextJob()); // should print "Document" to the console
        System.out.println(printer.printNextJob()); // should print "Invoice" to the console
        System.out.println(printer.printNextJob()); // should print "Instructions" to the console
        System.out.println(printer.printNextJob()); // should throw a NoSuchElementException
        
    }
}
