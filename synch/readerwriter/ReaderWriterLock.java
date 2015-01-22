package systems.synch.readerwriter;

import java.util.Map;
import java.util.HashMap;

/*
* Reentrant Read/write lock
* Handles the following:
*    * Multiple Readers or Single Writer allowed
*    * Readers are reentrant
*    * A writer thread can also read
*    * Writers are not starved
* Code is not super optimal
*/
public class ReaderWriterLock {
    private int nWriters;
    private int nWriteIntents;
    private Thread writingThread = null;
    private Map <Thread, Integer> activeReaderCount = new HashMap<>();
    
    

    public synchronized void lockReader() throws InterruptedException{
        while (!grantRead()) {
            System.out.println("Waiting to read...");
            wait();
        }

    }

    private boolean grantRead() {
        Thread currentThread = Thread.currentThread();
 
        // Check if one of the current threads reading is again requesting it
        if (activeReaderCount.containsKey(currentThread)) {
            System.out.println("Reentrant Read...");
            int count = activeReaderCount.get(currentThread).intValue() + 1;
            activeReaderCount.put(currentThread, new Integer(count));
            return true;
        }

        // If the writer is also a reader...
        if (currentThread == writingThread) {
            System.out.println("Writing Thread  Read...");
            if (activeReaderCount.containsKey(currentThread)) {
                int count = activeReaderCount.get(currentThread).intValue() + 1;
                activeReaderCount.put(currentThread, new Integer(count));
                return true;
            } else {
               activeReaderCount.put(currentThread, 1); 
               return true;
            }
        }

        // No one writing or no one intending to write
        if (nWriters == 0 && nWriteIntents == 0) {
            System.out.println("New Read");
            activeReaderCount.put(currentThread, 1); 
            return true;
        }

        return false;
    }


    public synchronized void unlockReader() {
        Thread currentThread = Thread.currentThread();
        int readerCount = activeReaderCount.get(currentThread).intValue();
        
        if (readerCount == 1)
            activeReaderCount.remove(currentThread);
        else
            activeReaderCount.put(currentThread, (readerCount - 1));
        
        notifyAll();
    }


    public synchronized void lockWriter() throws InterruptedException{
        nWriteIntents ++;
        while (!grantWrite()) {
            System.out.println("Waiting to write...");
            wait();
        }

        nWriteIntents --;
        nWriters ++;
        writingThread = Thread.currentThread();
    }
     
    private boolean grantWrite() {
        if (activeReaderCount.size() == 0 && nWriters == 0)
            return true; 

        return false;
    }

    public synchronized void unlockWriter() {
        nWriters --;
        writingThread = null;
        notifyAll();
    }



}
