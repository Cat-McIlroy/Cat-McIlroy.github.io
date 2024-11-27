/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ie.ncirl.datastructuresca2024;

/**
 * Interface for a print server which uses a Queue, which is a First In First Out (FIFO) structure.
 * Details the methods to be implemented by any print server class.
 * 
 * @author catherinemcilroy
 */
public interface IPrinter {
    
    // enqueue method - adds a print job to the rear of the queue
    /* returns void, accepts an element of type String (job) as a parameter.
       This String element (job) will be enqueued */
    public void submitPrintJob(String job);
    
    // dequeue method - removes and returns the print job from the front of the Queue
    /* returns a value of type String, which will be the element that was removed
       from the Queue. Does not accept any parameters. */
    public String printNextJob();
    
    // size method - returns the size (the number of jobs) currently in the Queue
    // returns a value of type int. Does not accept any parameters.
    public int size();
    
    // peek method - returns the element at the front of the Queue but does not remove it
    /* returns a value of type String, which will be the element at the front of the Queue.
       Does not accept any parameters.*/
    public String pollNextJob();
}
