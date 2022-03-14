/*
 * Author: Miles Gendreau
 * File: ArrList.java
 * Course: CSC 345
 * Assignment: Project 2
 * 
 * Purpose: This file contains an implementation of a generic ArrayList using a dynamic
 * circular array. The class contains methods to perform various operations on the list,
 * including methods for getting, adding, removing, and finding elements.
 */

public class ArrList<T> {
	private static final int DEFAULT_CAPACITY = 10;
	
	private T[] arr;
	private int capacity = DEFAULT_CAPACITY;
	private int size = 0;
	
	// index in arr of first element
	private int start = 0;
	//index immediately after final element
	private int end = 0;
	
	public ArrList() {
		arr = (T[]) new Object[capacity];
	}
	
	public ArrList(int cap) {
		capacity = cap;
		arr = (T[]) new Object[capacity];
	}
	
	// increments index i by 1, wrapping around at the end.
	private int increment(int i) {
		return (i+1) % capacity;
	}
	
	// decrements index i by 1, wrapping around at the start.
	private int decrement(int i) {
		return ((i-1) < 0) ? capacity-1 : i-1;
	}
	
	/*
	 * Converts an index of the ArrList to an index of the internal 
	 * circular array.
	 * 
	 * @param i - index of the ArrList
	 */
	private int ind(int i) {
		return (start+i) % capacity;
	}
	
	/*
	 * Resizes the array by creating a new array of size new_cap and 
	 * copying over the elements of arr, and then setting arr equal to the new array.
	 * 
	 * @param new_cap - new capacity for the array
	 */ 
	private void resize(int new_cap) {
		T[] new_arr = (T[]) new Object[new_cap];
		new_arr[0] = arr[start];
		int count = 1;
		for(int i = this.increment(start); i != end; i = this.increment(i)) {
			new_arr[count] = arr[i];
			count++;
		}
		arr = new_arr;
		capacity = new_cap;
		start = 0;
		end = size;
	}
	
	/*
	 * Shifts every element to the right starting from index and ending
	 * at last_ind. These represent actual indices in the internal array, not
	 * indices of the user-facing ArrList.
	 * 
	 * @param index - starting point
	 * @param last_ind - ending point
	 */
	private void shiftRight(int index, int last_ind) {
		for(int i = last_ind; i > index; i = this.decrement(i)) {
			arr[i] = arr[this.decrement(i)];
		}
	}
	
	/*
	 * Adds a new element to the end of the list. If the internal
	 * array is full, then it is resized to double its capacity. 
	 * 
	 * @param t - object to append
	 */
	public void add(T t) {
		if(size == capacity) {
			this.resize(2*capacity);
		}
		arr[end] = t;
		end = this.increment(end);
		size++;
	}
	
	/*
	 * Adds a new element at index i to the list. If the internal
	 * array is full, then it is resized to double its capacity.
	 * Starting at index i, elements are shifted to the right
	 * and the value of arr at i is set to the new element t.
	 * 
	 * @param i - index to add at
	 * @param t - object to add
	 */
	public void add(int i, T t) {
		if(size == capacity) {
			this.resize(2*capacity);
		}
		shiftRight(this.ind(i), end);
		arr[this.ind(i)] = t;
		end = this.increment(end);
		size++;
	}
	
	// removes all elements from the array and resets its capacity.
	public void clear() {
		arr = (T[]) new Object[DEFAULT_CAPACITY];
		capacity = DEFAULT_CAPACITY;
		size = 0;
		start = 0;
		end = 0;
	}
	
	// returns the element at index i in the list.
	public T get(int i) {
		return arr[this.ind(i)];
	}
	
	/*
	 * Searches the list for o, returns the index of first occurrence if it exists,
	 * if not, -1.
	 * 
	 * @param o - object to find
	 */
	public int indexOf(Object o) {
		for(int i = 0; i < size; i++) {
			if(o.equals(arr[this.ind(i)])) {
				return i;
			}
		}
		return -1;
	}
	
	/*
	 * Calls indexOf, if indexOf returns -1, then list does not contain o, so function returns
	 * false, else returns true.
	 * 
	 * @param o - object to find
	 */
	public boolean contains(Object o) {
		if(this.indexOf(o) != -1) {
			return true;
		}
		return false;
	}
	
	// returns true if there are no elements in the list.
	public boolean isEmpty() {
		if(size == 0) {
			return true;
		}
		return false;
	}
	
	/*
	 * Searches the list backwards for o, returns the index of last 
	 * occurrence if it exists, if not, -1.
	 * 
	 * @param o - object to find
	 */
	public int lastIndexOf(Object o) {
		for(int i = size-1; i >= 0; i--) {
			if(o.equals(arr[ind(i)])) {
				return i;
			}
		}
		return -1;
	}
	
	/*
	 * Removes the element at index i and returns it. The remaining
	 * elements are shifted over to fill the gap. If the size shrinks 
	 * below one quarter of the capacity, then the internal array is
	 * resized to half its current capacity.
	 * 
	 * @param i - index to remove at
	 */
	public T remove(int i) {
		T temp = arr[ind(i)];
		shiftRight(start, ind(i));
		start = this.increment(start);
		size--;
		
		if(size < Math.floor(capacity/4)) {
			this.resize((int) Math.floor(Math.max(capacity/2, 10)));
		}
		
		return temp;
	}
	
	/*
	 * Removes o if it exists in the list. Returns true if an element was
	 * removed, otherwise false.
	 * 
	 * @param o - object to remove
	 */
	public boolean remove(Object o) {
		int ind_of = this.indexOf(o);
		if(ind_of != -1) {
			this.remove(this.indexOf(o));
			return true;
		}
		return false;
	}
	
	/*
	 * Removes the elements in a range of indices: from - to, where
	 * from is inclusive and to is exclusive. Shifts the remaining elements
	 * over to fill the gap.
	 * 
	 * @param from - start of removal range (inclusive)
	 * @param to - end of removal range (exclusive)
	 */
	public void removeRange(int from, int to) {
		int range = to - from;
		for(int i = from; i < size-range; i++) {
			int shift = this.ind(i+range);
			arr[this.ind(i)] = arr[shift];
		}
		size -= range;
		end = this.ind(size);
		
		if(size < Math.floor(capacity/4)) {
			this.resize((int) Math.floor(capacity/2));
		}
	}
	
	// sets the element at index to item.
	public T set(int index, T item) {
		int i = this.ind(index);
		T prev = arr[i];
		arr[i] = item;
		return prev;
	}
	
	// returns the number of elements in the list.
	public int size() {
		return size;
	}
	
	// resizes arr so it has a capacity equal to the number of elements.
	public void trimToSize() {
		this.resize(size);
	}
	
	@Override
	// toString function for debugging purposes.
	public String toString() {
		String ret = "[";
		ret += arr[start] + ", ";
		for(int i = this.increment(start); i != end; i = this.increment(i)) {
			ret += arr[i] + ", ";
		}
		ret += "]\n";
		return ret;
	}
	
}
