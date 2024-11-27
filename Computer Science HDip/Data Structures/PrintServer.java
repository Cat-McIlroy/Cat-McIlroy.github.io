/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ie.ncirl.datastructuresca2024;

/**
 * An implementation of a print server based on a Queue (FIFO Structure).
 * Implements the IPrinter interface.
 * Uses the ArrayList class from the built-in Java Collections Framework.
 * 
 * @author catherinemcilroy
 */

// import the ArrayList class from the java.util package
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class PrintServer implements IPrinter{
    
    // declare member variables
    private ArrayList<String> printQueue;
    
    // default constructor
    /* a new PrintServer object initialised using this constructor will be empty */
    public PrintServer(){
        this.printQueue = new ArrayList<>(); // a new, empty ArrayList is initialised to hold the elements
    }
    
    // submitPrintJob method - adds a print job to the rear of the Queue
    /* returns void, accepts an element of type String (job) as a parameter.
       This String element (job) will be enqueued */
    @Override
    public void submitPrintJob(String job){
        printQueue.add(job); /* utilises the add() method of the ArrayList class to append the element 
        which was passed in as a parameter to the end of the list */
    }
    
    // printNextJob method - removes and returns the print job from the front of the Queue
    /* returns a value of type String, which will be the element that was dequeued. 
    Does not accept any parameters. */
    @Override
    public String printNextJob(){
        // if queue is empty, no job can be removed or returned from the queue
        if(printQueue.isEmpty()){
            throw new NoSuchElementException("Queue is empty.");
        }
        else{
            return printQueue.remove(0); /* utilises the remove() method of the ArrayList class to remove
            and return the element at the front of the Queue (index 0 in the ArrayList) */
        }
    }
    
    // size method - returns the size (the number of jobs) currently in the Queue
    // returns a value of type int. Does not accept any parameters.
    @Override
    public int size(){
        return printQueue.size(); /* utilises the size() method of the ArrayList class to return the number of 
        elements present in the Queue */
    }
    
    // peek method - returns the element at the front of the Queue but does not remove it
    /* returns a value of type String, which will be the element at the front of the Queue.
       Does not accept any parameters.*/
    @Override
    public String pollNextJob(){
        return printQueue.get(0); /* utilises the get() method of the ArrayList class to return the 
        element at the front of the Queue without removing it */
    }
    
    // toString method - prints out a String representation of the Queue
    /* uses a for loop to traverse the Queue from front to rear, using an index tracker i which starts at position 0 and 
    increments by 1 with each iteration of the loop, until it reaches the end of the Queue. Adds the element at each 
    position to a local String variable which is then returned. */
    @Override
    public String toString(){
        String str = "FRONT [ "; // declare local String variable to hold the String representation of each element in the Queue
        for(int i = 0; i < printQueue.size(); i++){ /* for loop is initialised at index 0, the index increases by 1 with each loop 
            iteration, the loop continues until it reaches the end of the Queue */
            str += (printQueue.get(i) + " "); /* with each iteration of the loop, the get() method of the ArrayList class is utilised to access
            the element at the current index i. This element is added to the previously declared local String variable. */
        }
        return str += "] REAR"; /* once the loop has been completed, all elements in the Queue have been added to the previously declared
\       local String variable. This String has been formatted in such a way that it is clear to the user where the front and rear of the Queue 
        are, and this value is returned */
    }
}
