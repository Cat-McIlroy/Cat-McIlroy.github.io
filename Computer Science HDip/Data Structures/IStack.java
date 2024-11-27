/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ie.ncirl.datastructuresca2024;

/**
 * Interface for the Stack abstract data type, which is a Last In First Out structure (LIFO).
 * Details the methods to be implemented by the Stack. 
 * This interface uses generic type T, which allows it to be implemented using any data type.
 * 
 * @author catherinemcilroy
 */
public interface IStack<T> {
    
    // push method - adds an element to the top of the Stack
    public void push(T element);
    /* returns void, accepts an element of generic type T as a parameter.
       This is the element which will be added to the Stack */
    
    // pop method - removes and returns the element at the top of the Stack 
    public T pop();
    /* returns a value of generic type T, which will be the element that was 
       removed from the Stack. Does not accept any parameters. */
    
    // peek method - returns the element at the top of the Stack but does not remove it
    public T peek();
    /* returns a value of generic type T, which will be the element at the top of the Stack.
       Does not accept any parameters. */
}
