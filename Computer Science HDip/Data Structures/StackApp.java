/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ie.ncirl.datastructuresca2024;

/**
 *
 * @author catherinemcilroy
 */
public class StackApp {

    /**
     * This main method will demonstrate the methods defined in the Stack class. 
     * A new instance of class Stack will be created using Strings (person names) as input.
     * @param args
     */ 
    public static void main(String[] args) {
        
        // instantiate new Stack object to accept data of type String
        Stack<String> stack = new Stack<>();
        
        // add some names to the Stack object using the push method
        stack.push("Catherine");
        stack.push("Emma");
        stack.push("David");
        stack.push("Niall");
        stack.push("Rebecca");
        
        // print out the contents of the Stack using the toString method
        System.out.println(stack.toString());
        
        // remove and return the top element of the Stack using the pop method
        System.out.println(stack.pop());
        
        // view the top element of the Stack without removing it, using the peek method
        System.out.println(stack.peek());
    }
    
}
