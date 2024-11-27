/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ie.ncirl.datastructurestaba2024;

/**
 *
 * @author catherinemcilroy
 */
public class Main {

    /**
     * Main class for implementation of methods
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // ************************************* QUESTION 3 *****************************************
        
        // instantiate new RecursiveBinarySearch object
        RecursiveBinarySearch rbs = new RecursiveBinarySearch();
        
        // Assume that the input array collection contains the following values:
        // { 72, 12, 23, 5, 2, 16, 8, 91, 38, 56}.
        int[] collection = {72, 12, 23, 5, 2, 16, 8, 91, 38, 56};
        
        
        // Search for the target value, key is equal to 23.
        // The value returned from the binary search is displayed to the user using the console. 
        System.out.println(rbs.recursiveBinarySearch(collection, 23));
        
        // ******************************************************************************************
        
        // ************************************* QUESTION 4 *****************************************

        // instantiate new BinarySearchTree objects
        BinarySearchTree employeeTree = new BinarySearchTree();
        BinarySearchTree testTree = new BinarySearchTree();
        
        // instantiate new Employee objects
        Employee e1 = new Employee(1000, "Catherine", "Sales");
        Employee e2 = new Employee(1789, "David", "HR");
        Employee e3 = new Employee(6721, "Niall", "Sales");
        Employee e4 = new Employee(4138, "Deborah", "Finance");
        Employee e5 = new Employee(8135, "Lisa", "Development");
        Employee e6 = new Employee(1702, "Philip", "HR");
        Employee e7 = new Employee(2843, "Stephen", "Sales");
        Employee e8 = new Employee(7242, "Niamh", "Development");
        Employee e9 = new Employee(8246, "Sarah", "Finance");
        Employee e10 = new Employee(9192, "Cormac", "Finance");
        
//      DEMONSTRATE EACH OF THE BST METHODS IMPLEMENTED IN PART 4A

        // insert employees into the BST
        employeeTree.insert(e1);
        employeeTree.insert(e2);
        employeeTree.insert(e3);
        employeeTree.insert(e4);
        employeeTree.insert(e5);
        employeeTree.insert(e6);
        employeeTree.insert(e7);
        employeeTree.insert(e8);
        employeeTree.insert(e9);
        employeeTree.insert(e10);
        
        // search for an employee based on their employee ID
        System.out.println(employeeTree.find(e5));
        
        // return all employee records in the BST and print details of each record based on
        // the numerical sequence of their employee ID
        employeeTree.inOrder();
        
        // allow for deleting an employee from the BST based on their employee ID
        employeeTree.remove(e9);
        
//      TEST OPERATION OF BINARY SEARCH TREE

        // test isEmpty method
        // Expected result for employeeTree = false
        // Expected result for testTree = true
        System.out.println("Testing employeeTree.isEmpty(). Expected: false.\nActual: " + employeeTree.isEmpty());
        System.out.println("Testing testTree.isEmpty(). Expected: true.\nActual: " + testTree.isEmpty());

        // test size method
        // Expected result for employeeTree = 9
        // Expected result for testTree = 0
        System.out.println("Testing employeeTree.size(). Expected: 9.\nActual: " + employeeTree.size());
        System.out.println("Testing testTree.size(). Expected: 0.\nActual: " + testTree.size());
        
        // test insert and contains methods
        // I will first test to see if either tree contains employee e9
        // I will then insert employee e9 into each tree, and run contains method again
        // Expected result for both trees = false
        System.out.println("Testing employeeTree.contains(e9). Expected: false.\nActual: " + employeeTree.contains(e9));
        System.out.println("Testing testTree.contains(e9). Expected: false.\nActual: " + testTree.contains(e9));
        // Insert e9 into both trees
        employeeTree.insert(e9);
        testTree.insert(e9);
        // Re-testing contains method for both trees
        // Expected result for both trees = true
        System.out.println("Re-testing employeeTree.contains(e9). Expected: true.\nActual: " + employeeTree.contains(e9));
        System.out.println("Re-testing testTree.contains(e9). Expected: true.\nActual: " + testTree.contains(e9));
        
        // test remove method
        // Expected result for both trees = true
        System.out.println("Testing employeeTree.remove(e9). Expected: true.\nActual: " + employeeTree.remove(e9));
        System.out.println("Testing testTree.remove(e9). Expected: true.\nActual: " + testTree.remove(e9));
        // There is an error here. The node is not being removed properly from testTree (where it is present as the root node)
        // I do not have time left in this assessment to fix this issue.
        
        // test findMax method
        // Expected result for employeeTree = 9192, "Cormac", "Finance"
        // Expected result for testTree = 8246, "Sarah", "Finance";
        System.out.println("Testing employeeTree.findMax(). Expected: 9192, 'Cormac', 'Finance'.\nActual: " + employeeTree.findMax());
        System.out.println("Testing testTree.findMax(). Expected: 8246, 'Sarah', 'Finance'.\nActual: " + testTree.findMax());
        
        // test findMin method
        // Expected result for employeeTree = 1000, "Catherine", "Sales"
        // Expected result for testTree = 8246, "Sarah", "Finance";
        System.out.println("Testing employeeTree.findMin(). Expected: 1000, 'Catherine', 'Sales'.\nActual: " + employeeTree.findMin());
        System.out.println("Testing testTree.findMin(). Expected: 8246, 'Sarah', 'Finance'.\nActual: " + testTree.findMin());
       
        // test inOrder method
        // Expected result for employeeTree = (1000, "Catherine", "Sales"), (1702, "Philip", "HR"), (1789, "David", "HR"),
        // (2843, "Stephen", "Sales"), (4138, "Deborah", "Finance"), (6721, "Niall", "Sales"), (7242, "Niamh", "Development"),
        // (8135, "Lisa", "Development"), (9192, "Cormac", "Finance")
        // Expected result for testTree = 8246, "Sarah", "Finance"
        System.out.println("Testing employeeTree.inOrder(): ");
        employeeTree.inOrder();
        System.out.println("Testing testTree.inOrder(): ");
        testTree.inOrder();
        
        // test preOrder method
        // Expected result for employeeTree = (1000, "Catherine", "Sales"), (1789, "David", "HR"), (1702, "Philip", "HR"),
        // (6721, "Niall", "Sales"), (4138, "Deborah", "Finance"), (2843, "Stephen", "Sales"), (8135, "Lisa", "Development"),
        // (7242, "Niamh", "Development"), (9192, "Cormac", "Finance")
        // Expected result for testTree = 8246, "Sarah", "Finance"
        System.out.println("Testing employeeTree.preOrder(): ");
        employeeTree.preOrder();
        System.out.println("Testing testTree.preOrder(): ");
        testTree.preOrder();
        
        // test postOrder method
        // Expected result for employeeTree = (1702, "Philip", "HR"), (2843, "Stephen", "Sales"), (4138, "Deborah", "Finance"),
        // (7242, "Niamh", "Development"), (9192, "Cormac", "Finance"), (8135, "Lisa", "Development"),
        // (6721, "Niall", "Sales"), (1789, "David", "HR"), (1000, "Catherine", "Sales")
        // Expected result for testTree = 8246, "Sarah", "Finance"
        System.out.println("Testing employeeTree.postOrder(): ");
        employeeTree.postOrder();
        System.out.println("Testing testTree.postOrder(): ");
        testTree.postOrder();
   
    }
}
