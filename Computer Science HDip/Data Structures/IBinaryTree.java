/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package ie.ncirl.datastructurestaba2024;

/**
 *
 * @author catherinemcilroy
 */
public interface IBinaryTree<T extends Comparable<T>> {
    
    // returns the number of nodes contained in the tree e.g. size
    public int size();
    
    // returns true if the tree has a size of zero, false otherwise
    public boolean isEmpty();
    
    // accepts a value of type T, adds a new node containing this element to the tree
    // in a binary search tree arrangement
    public void insert(T element);
    
    // find the maximum value in the tree
    public T findMax();
    
    // find the minimum value in the tree
    public T findMin();
    
    // visits each node in the tree using an in ordered pattern and prints contents
    // of each node to the console
    public void inOrder();
    
    // visits each node in the tree using a pre-ordered pattern and prints contents
    // of each node to the console
    public void preOrder();
    
    // visits each node in the tree using a post-ordered pattern and prints contents
    // of each node to the console
    public void postOrder();
    
    // accepts a value of type T, then searches the tree for this value.
    // returns true if the value is found, otherwise returns false
    public boolean contains(T element);
    
    // returns the node in the tree which contains the target element,
    // otherwise returns null if the element is not found
    public BTNode<T> find(T element);
    
    // takes in a value of type T, then searches the tree for this value. If found, removes
    // the node with the corresponding value and returns true, otherwise returns false
    public boolean remove(T element);
}

