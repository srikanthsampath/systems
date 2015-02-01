package systems.synch.boundedbuffer;

import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.Random;


/*
* A bounded buffer implementation using Semaphores provided by Java :)
* Assumes that the underlying buffer is synchronized (a different problem)
*/
public class BoundedBuffer <T> {

    private int nSlots;		// Size of the BB
    private List<T> synchList = null; 		// Synchronized
    private Semaphore nAvailableSlots = null; // Semaphore that tacks available slots
    private Semaphore nAvailableItems = null; // Semaphore to track items

    public BoundedBuffer(int slots, List<T> list) {
        nSlots = slots;
        nAvailableSlots = new Semaphore(nSlots);
        nAvailableItems = new Semaphore(0);
        synchList = list;
    }

    public void putItem(T item) throws InterruptedException {
        nAvailableSlots.acquire();
        synchList.add(item);
        nAvailableItems.release();
    }

    public T getItem() throws InterruptedException {
        nAvailableItems.acquire();
        T item = synchList.remove(0);// remove the first time
        nAvailableSlots.release();
        return item;
    }

    // Just a debug routine
    public int getQueueLength() {
        return nAvailableSlots.getQueueLength();
    }
}
