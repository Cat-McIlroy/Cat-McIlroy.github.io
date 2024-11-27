package ie.ncirl.datastructurestaba2024;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * A recursive implementation of the binary search algorithm.
 * 
 * @author catherinemcilroy
 */
public class RecursiveBinarySearch {
    
    // Write a Java method recursiveBinarySearch(int[] collection, int key)
    // The method should return the index of the target if found, or -1 if the target is not present in the array
    // (i.e. The method should return an int)
    
    // public wrapper method
    // this method will ensure that the input data is suitably prepared for the search (i.e. it is sorted)
    // it will then call the private method which will perform the recursive binary search
    public int recursiveBinarySearch(int[] collection, int key){
        // check if the array is sorted in ascending order
        boolean isSortedAscending = true;
        // iterate through the entire input array collection
        for (int i = 0; i < collection.length - 1; i++) {
            // if the element at the current index is greater than the element at index + 1,
            // the array is not sorted in ascending order, set isSortedAscending to be false
            if(collection[i] > collection[i + 1]){
                isSortedAscending = false;
            }
        }
        // if the input array collection is not sorted in ascending order, sort the array
        // in ascending order before passing it to the private recursiveBinarySearch method
        if(isSortedAscending == false){
            collection = insertionSort(collection);
        }
        // as this method is searching the entire array,
        // the initial start index is passed as index 0 (the first element in the array) and
        // the initial end index is passed as the length of the collection - 1 (the last element in the array) 
        return recursiveBinarySearch(collection, key, 0, collection.length-1);
    }

    private int recursiveBinarySearch(int[] collection, int key, int start, int end){
        // middle index is the sum of the start and end indices, divided by 2
        int middle = (start + end) / 2;
        
        // BASE CASES
        // 1) end of collection has been reached
        if(start > end){
            // return -1 as the target is not present in the array
            return -1;
        }
        // 2) element matching the key found
        if(collection[middle] == key){
            // return the index of the target
            return middle;
        }
        
        // RECURSIVE CASES
        // 1) key is lower than current middle, search in lower half
        if(collection[middle] > key){
            // set end of search interval to be current middle - 1, no change to start index
            return recursiveBinarySearch(collection, key, start, middle - 1);
        }
        // 2) key is greater than current middle, search in upper half
        else{
            // set start of search interval to be current middle + 1, no change to end index
            return recursiveBinarySearch(collection, key, middle + 1, end);
        }
    }
    
    // sorting method
    // uses insertion sort method to sort input array into ascending order
    public int[] insertionSort(int[] collection){
        int i, j;
        // outer loop, iterates over each element in the array
        for(i = 1; i < collection.length; i++){
            // store the element at index i in a variable key
            int key = collection[i];
            // inner loop, checks if the key (element at index i) is in the correct position
            for(j = (i - 1); j >= 0 && collection[j] > key; j--){
                // if key not in correct position (i.e. element at j is greater than the key)
                // elements are shifted to the right until either index 0 is reached, 
                // or the correct position for the key is found 
                // (i.e. element at j is smaller than or equal to the key)
                collection[j + 1] = collection[j];
            }
            // insert the key into the correct position in the array
            collection[j + 1] = key;
        }
        // return the collection, now sorted in ascending order
        return collection;
    }
}
