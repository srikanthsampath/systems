package systems.LRU;

import java.util.HashMap;
import java.util.Map;



public class LRU<K, V>  {

    // Class that holds an element - containing a key and a value
    private static class Element<K, V> {
        K k;
        V v;
        Element<K, V> next;
        Element<K, V> prev;
     
        public Element(K k, V v, Element<K, V> next, Element<K, V> prev) {
            this.k = k;
            this.v = v;
            this.next = next;
            this.prev = prev;
        }

    }

    // Underlying storage is a hashmap. The value is a datastructure of the above type
    // We maintain a MRU-LRU Chain.  The list is maintained on the access sequence
    private int capacity;
    private Element<K, V> head = null;
    private Element<K, V> tail = null;
    private Map<K, Element<K, V>>  hashMap = new HashMap<>(); 


    public LRU(int capacity) {
        this.capacity = capacity;
    }

    private void setHead(Element<K, V> elem) {
        this.head = elem;
    }

    private void replaceOldest() {
        // fix the new tail
        Element<K, V> curTail = this.tail;
        Element<K, V> newTail = this.tail.prev;
        newTail.next = null;
        this.tail = newTail;

        // Remove the element from the LRU end
        this.hashMap.remove(curTail.k);
    }


    // Adding a key-value pair
    public synchronized void put(K k, V v) {

        // If we have reached capacity
        if (this.hashMap.size() == capacity) {
            replaceOldest();
        }
        
        Element<K, V> curHead = this.head;
        Element<K, V> elem = null;

        // Insert if only element
        if (curHead == null) {
           elem = new Element(k, v, null, null);
           this.tail = elem;
        }
        else  {
           // else put it at the head
           elem = new Element(k, v, curHead, null);
           curHead.prev = elem;
        }
   
        setHead(elem);

        // Insert into the hash map
        hashMap.put(k, elem);
    }

    public synchronized V get(K k) {
        Element<K, V> v = hashMap.get(k);

        if (v == null)
            return null;

         // If this is at the head nothing to do
        if (v == this.head)
            return v.v; 

         // If this is the tail, fix the tail
        if (v == this.tail)
            this.tail = v.prev;

        // else move it to the head
        // First fix the next and prev of the neighbors
        if (v.prev != null) 
            v.prev.next = v.next;

        if (v.next != null)
            v.next.prev = v.prev; 

        // fix the next and prev of element
        v.next = this.head;
        v.prev = null;

        // Set the head
        head.prev = v;
        setHead(v); 
        return v.v;
    }

    public synchronized void print() {
        Element <K, V> curElement = this.head;

        while (curElement != null) {
            System.out.println("Key: " + curElement.k + " Value: " + curElement.v);
            curElement = curElement.next; 
        }

        curElement = this.tail;

        while (curElement != null) {
            System.out.println("Key: " + curElement.k + " Value: " + curElement.v);
            curElement = curElement.prev; 
        }
    }

}
