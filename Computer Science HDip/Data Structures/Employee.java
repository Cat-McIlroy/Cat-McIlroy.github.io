/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ie.ncirl.datastructurestaba2024;

/**
 * 4a) Develop a custom class called Employee that will hold employee information. 
 * 
 * @author catherinemcilroy
 */

// implements Comparable Java interface
public class Employee implements Comparable<Employee>{
    
    // declare member variables
    
    // unique employee ID (a 4-digit number e.g. 1234)
    public int employeeID;
    // employee's name
    public String name;
    // employee's department (valid departments are, “HR”, “Development”, “Sales”, and “Finance”)
    public String department;
    
    // constructor
    public Employee(int employeeID, String name, String department){
        this.employeeID = employeeID;
        this.name = name;
        this.department = department;
    }
    
    // compareTo method, implements natural ordering for employees based on employeeID
    // returns an int, accepts as a parameter an object of class Employee
    @Override
    public int compareTo(Employee e) {
        // if the employeeIDs are equal, return 0
        if(this.employeeID == e.employeeID){
            return 0;
        }
        // if this employeeID is greater than the argument employeeID, return 1
        else if(this.employeeID > e.employeeID){
            return 1;
        }
        // if this employeeID is less than the argument employeeID, return -1
        else{
            return -1;
        }
    }
    
    // toString method, which will return the Employee object as a String
    @Override
    public String toString(){
        return "Employee ID: " + this.employeeID + "\nName: " + this.name + "\nDepartment: " + this.department;
    }
}
