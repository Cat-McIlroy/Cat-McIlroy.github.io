/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ie.ncirl.datastructurestaba2024;

/**
 *
 * @author catherinemcilroy
 */
public class BTNode<T extends Comparable <T>> {
    
    public T element;
    public BTNode<T> left;
    public BTNode<T> right;
    
    public BTNode(T element){
        this.element = element;
        this.left = null;
        this.right = null;
    }
    
    @Override
    public String toString(){
        return String.valueOf(element);
    }  

}
