package systems.synch.blockingqueue;



import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.Collection;
import java.util.LinkedList;

public  class LinkedBlockingQueue<E> {

    private int maxCapacity;

    private LinkedList<E> queue = new LinkedList<E>();

    public LinkedBlockingQueue(int capacity) {
        maxCapacity = capacity;
    }

    public synchronized void put(E e) throws InterruptedException {

        // Wait till there is space
        while (queue.size() ==  maxCapacity) {
            wait();
        }
 

        // If we are empty - notify all those that are waiting
        if (queue.size() == 0)
            notifyAll();

        // Add the item 
        queue.add(e);      

        return;
        
    }


    public synchronized E take() throws InterruptedException {

        // Wait if empty
        while (queue.size() == 0) {
            wait();
        }
        
        // If we are full notify since we are going to remove one
        if (queue.size() == maxCapacity)
            notifyAll();

        E item = queue.remove(0); 

        return item;
    }

    public synchronized int size() {
        return (queue.size());

    }



}
