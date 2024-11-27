/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ie.ncirl.datastructurestaba2024;

/**
 *
 * @author catherinemcilroy
 * @param <T>
 */
public class BinarySearchTree<T extends Comparable<T>> implements IBinaryTree<T>{

    // declare member variable
    public BTNode<T> root;
    
//    ********************************** isEmpty ***************************************
    
    // implement isEmpty method
    // returns true if the tree has a size of zero (root is null), false otherwise
    @Override
    public boolean isEmpty() {
       return root == null;
    }
    
//    ************************************ size ****************************************
    
    // implement size method, returns the number of nodes contained in the tree
    // public wrapper method
    @Override
    public int size() {
        // check if the tree is empty
        if(isEmpty()){
            return 0;
        }
        else{
            // otherwise call recursive method
            return size(root); 
        }
    }

    // private recursive method
    private int size(BTNode current){
        // base case
        if(current == null){
            return 0;
        }
        // recursive case
        return (1 + size(current.left)+ size(current.right));
    }
    
//    ********************************** insert ***************************************
    
    // implement insert method, accepts a value of type T, adds a new node containing this element to the tree
    // in a binary search tree arrangement.
    // public wrapper method
    @Override
    public void insert(T element) {
        // if tree is empty, insert element at the root
        if(isEmpty()){
            root = new BTNode<>(element);
        }
        else{
            // otherwise call recursive method
            insert(element, root);
        }
    }

    // private recursive method
    private void insert(T element, BTNode<T> current){
        // compare element with current node
        if(element.compareTo(current.element) < 0){
            // go left
            if(current.left == null){ // if empty, insert value
                current.left = new BTNode(element);
            }
            else{
            // if not empty
            insert(element, current.left);
            }
        }
        else{
            if(current.right == null){ // if empty, insert value
                current.right = new BTNode(element);
            }
            else{
            // if not empty
            insert(element, current.right);
            }
        }
    }

//    ********************************** contains *************************************
    
    // implement contains method, accepts a value of type T, then searches the tree for a match
    // if a match is found, returns true, otherwise returns false
    @Override
    public boolean contains(T element){
        return contains(element, root);
    }
    
    private boolean contains(T element, BTNode<T> current){
        // base case
        // if no match is found, return null
        if(current == null){
            return false;
        }
        // three recursive cases
        // if element is less than the current node element, go left
        else if(element.compareTo(current.element) < 0){
            return contains(element, current.left);
        }
        // if element is greater than the current node element, go right
        else if(element.compareTo(current.element) > 0){
            return contains(element, current.right);
        }
        // if element is equal to the current node element, a match is found, return true 
        else{
            return true;
        }
    }

//    ********************************** find *************************************
    
    // implement find method, accepts a value of type T, then searches the tree for a match
    // if a match is found, returns the node containing this value, otherwise returns null
    @Override
    public BTNode<T> find(T element){
        return find(element, root);
    }
    
    private BTNode<T> find(T element, BTNode<T> current){
        // base case
        // if no match is found, return null
        if(current == null){
            return null;
        }
        // three recursive cases
        // if element is less than the current node element, go left
        else if(element.compareTo(current.element) < 0){
            return find(element, current.left);
        }
        // if element is greater than the current node element, go right
        else if(element.compareTo(current.element) > 0){
            return find(element, current.right);
        }
        // if element is equal to the current node element, a match is found, return true 
        else{
            return current;
        }
    }
          
    
//    ********************************** remove ***************************************
    
    // implement remove method, takes in a value of type T, then searches the tree for this value. 
    // If found, removes the node with the corresponding value and returns true, otherwise returns false
    // public wrapper method
    @Override
    public boolean remove(T element){
        // check if tree is empty, if it is, print message to the console
        if(isEmpty()){
            System.out.println("Tree is empty!");
            return false;
        }
        // otherwise, call the recursive method
        else if (remove(element, root) == null){
            return false;
        }
        else{
            return true;
        }
        
    }
    
    // private recursive method
    private BTNode<T> remove(T element, BTNode<T> current){
        // base case
        // if no match is found, return null
        if(current == null){
            return null;
        }
        // three recursive cases
        // if element is less than the current node element, go left
        else if(element.compareTo(current.element) < 0){
            current.left = remove(element, current.left);
        }
        // if element is greater than the current node element, go right
        else if(element.compareTo(current.element) > 0){
            current.right = remove(element, current.right);
        }
        // if element is equal to the current node element, 
        else{
            // remove the matching node. There are four scenarios
            
            // Scenario 1: The matched node is a leaf node
            if(current.left == null && current.right == null){
                // set the current node element to null. No need to deal with child nodes
                current = null;
            }
            
            // Scenario 2: The matched node has a child on the left only
            else if(current.left != null && current.right == null){
                current = current.left;
            }
            
            // Scenario 3: The matched node has a child on the right only
            else if(current.left == null && current.right != null){
                current = current.right;
            }
            
            // Scenario 4: The matched node has children on both left and right
            else{
                // a. keep a temporary copy of the current node
                BTNode<T> temp = current;
                
                // b. find the node with minimum value on the right subtree
                T minNodeValueFromRight = findMin(temp.right);
                
                // c. replace current node with the minimum value node from the right subtree
                current.element = minNodeValueFromRight; 
                
                // d. remove the minimum value node from the right subtree
                current.right = remove(minNodeValueFromRight, current.right);
            }
        }
        return current;
    }

//    ********************************** findMax ***************************************
    
    // implement findMax method, returns the maximum value in the tree
    @Override
    public T findMax(){
        // check if tree is empty, if so print message to console and return null
        if(isEmpty()){
            System.out.println("The tree is empty.");
            return null;
        }
        // otherwise call recursive method
        return findMax(root);
    }
    
    private T findMax(BTNode current){
        if(current.right == null){
            return (T)current.element;
        }
        return findMax(current.right);
    }

//    ********************************** findMin ***************************************  
    
    // implement findMin method, returns the minimum value in the tree
    @Override
    public T findMin(){
        // check if tree is empty, if so print message to console and return null
        if(isEmpty()){
            System.out.println("The tree is empty.");
            return null;
        }
        // otherwise call recursive method
        return findMin(root);
    }
    
    private T findMin(BTNode current){
        if(current.left == null){
            return (T)current.element;
        }
        return findMin(current.left);
    }

//    ********************************** inOrder Traversal ***************************************  
    
    // implement inOrder method, traverses the tree in in-order pattern (left, root, right)
    // and prints the contents of each node to the console
    @Override
    public void inOrder() {
        inOrder(root);
    }
    
    private void inOrder(BTNode current){
        // base case
        if(current == null){
            return;
        }
        
        // recursive case
        inOrder(current.left);
        System.out.println(current.element);
        inOrder(current.right);
    }

//    ********************************** preOrder Traversal ***************************************
    
    // implement preOrder method, traverses the tree in pre-order pattern (root, left, right)
    // and prints the contents of each node to the console
    @Override
    public void preOrder(){
        if(root == null){
            System.out.println("The tree is empty.");
        }
        preOrder(root);
    }
    
    private void preOrder(BTNode current){
        // base case 
        if(current == null){
            return;
        }
        
        // recursive case
        System.out.println(current.element);
        preOrder(current.left);
        preOrder(current.right);
    }
    
//    ********************************** postOrder Traversal ***************************************
    
    // implement postOrder method, traverses the tree in post-order pattern (left, right, root)
    // and prints the contents of each node to the console
    @Override
    public void postOrder(){
        postOrder(root);
    }
    
    private void postOrder(BTNode current){
        // base case 
        if(current == null){
            return;
        }
        
        // recursive case
        postOrder(current.left);
        postOrder(current.right);
        System.out.println(current.element);
    }
}
