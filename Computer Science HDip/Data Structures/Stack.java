/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ie.ncirl.datastructuresca2024;

/**
 * An implementation of the Stack abstract data type, using the IStack interface.
 * This Stack is implemented using the ArrayList data structure.
 * This class uses generic type T, so instances of this class can use any data type.
 * 
 * @author catherinemcilroy
 */

// import classes from the java.util package which are needed for operations performed by this class
import java.util.ArrayList;
import java.util.EmptyStackException;

public class Stack<T> implements IStack<T>{
    
    // declare member variables
    private int topIndex; // keeps track of the position of the top element in the Stack
    private ArrayList<T> items; // the underlying structure which holds the elements in the Stack
    
    // default constructor
    /* a new Stack object initialised using this constructor will be empty */
    public Stack(){
        this.topIndex = -1; // position of the top element is -1 as there are no elements present in the Stack
        this.items = new ArrayList<>(); // a new, empty ArrayList is initialised to hold the elements
    }
    
    // push method - adds an element to the top of the Stack
    /* returns void, accepts an element of generic type T as a parameter.
       This is the element which will be added to the Stack */
    @Override
    public void push(T element){
        items.add(element); /* utilises the add() method of the ArrayList class to append the element 
        which was passed in as a parameter to the end of the list */
        topIndex++; /* topIndex tracker is incremented by 1 as a new element has been added to the list, 
        so the position of the top element has increased by 1 */
    }
    
    // pop method - removes and returns the element at the top of the Stack 
    /* returns a value of generic type T, which will be the element that was 
       removed from the Stack. Does not accept any parameters. */
    @Override
    public T pop(){ 
        T element; // declare local variable to hold the element which is removed from the Stack
        if(topIndex == -1){ /* if the topIndex value is -1, this means the Stack is empty, therefore
            no element can be removed or returned from the Stack */
            throw new EmptyStackException(); // throw an error if the Stack is empty, since nothing can be removed from an empty Stack
        }
        else{ // if the topIndex value is not -1, the Stack is not empty
            element = items.get(topIndex); /* utilises the get() method of the ArrayList class to access the element
            at position topIndex (the element at the top of the Stack). Stores this element in the previously declared local variable */
            items.remove(topIndex); /* uses the remove() method of the ArrayList class to remove the element at position 
            topIndex (the element at the top of the Stack). */
            topIndex--; /* topIndex tracker is decremented by 1 as an element has been removed from the list, 
            so the position of the top element has decreased by 1 */
        }
        return element; // return local variable containing the element which was removed from the top of the Stack
    }
    
    // peek method - returns the element at the top of the Stack but does not remove it
    /* returns a value of generic type T, which will be the element at the top of the Stack.
       Does not accept any parameters. */
    @Override
    public T peek(){
        if(topIndex == -1){ /* if the topIndex value is -1, this means the Stack is empty, therefore
            there is no element to return from the Stack */
            throw new EmptyStackException(); // throw an error if the Stack is empty, since nothing can be returned from an empty Stack
        }
        else{ // if the topIndex value is not -1, the Stack is not empty
            return items.get(topIndex); /* utilises the get() method of the ArrayList class to access the element
            at position topIndex (the element at the top of the Stack). Returns the value of this element. */
        }
    }
    
    // toString method - prints out a String representation of the Stack
    /* uses a for loop to traverse the Stack from top to bottom, using an index tracker i which starts at position topIndex and 
    decrements by 1 with each iteration of the loop, until it reaches index 0. Adds the element at each position to a local String variable
    which is then returned. */
    @Override
    public String toString(){
        String str = "TOP ["; // declare local String variable to hold the String representation of each element in the Stack
        for(int i = topIndex; i >= 0; i--){ /* for loop is initialised at position topIndex, the index decreases by 1 with each loop iteration, 
        and the loop continues until it reaches index 0 inclusive */
            str += (" " + items.get(i)); /* with each iteration of the loop, the get() method of the ArrayList class is utilised to access
            the element at the current index i. This element is added to the previously declared local String variable. */
        }
        return (str += " ] BOTTOM"); /* once the loop has been completed, all elements in the Stack have been added to the previously declared
\       local String variable. This String has been formatted in such a way that it is clear to the user where the top and bottom of the Stack 
        are, and this value is returned */
    }
}
