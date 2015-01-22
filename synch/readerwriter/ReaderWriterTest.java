package systems.synch.readerwriter;

import java.util.List;
import java.util.LinkedList;
import java.util.Random;


/*
*  A test driver program to test the read-write lock
*  Have a data item and a lock protecting its access
*/

public class ReaderWriterTest {

    private static volatile int testValue = 0;    
    private static int NUM_THREADS = 50;
    private ReaderWriterLock rwLock = null;
    private static ReaderWriterTest instance = null;


   /*
   * Singleton
   */
    public static synchronized ReaderWriterTest getInstance() {
        if (instance == null) {
            ReaderWriterLock lock = new ReaderWriterLock();
            instance = new ReaderWriterTest(lock);
        }
        return instance;
    }

    private ReaderWriterTest(ReaderWriterLock lock) {
        rwLock = lock;
    }

    /* 
    * Reader
    */
    public int getValue() {
        int value = 0;
        try {
            rwLock.lockReader();
            value =  testValue;
            rwLock.unlockReader();
        } catch (InterruptedException e) {
        }
        return (value);

    }

    /*
    * Writer/updater
    *  Also gets a read lock 
    */
    public int incrValue() {
        int newValue = 0;
        try {
            rwLock.lockWriter();
            testValue += 10; 
            newValue = getValue();
            rwLock.unlockWriter();
        } catch (InterruptedException e) {
 
        }
        return newValue;
    }


    /*
    * Thread that reads the data value and 1 in 3 times updates it
    */
    public static class TestThread implements Runnable {
        private ReaderWriterTest rwTest = null;
        Random random = null;
        public TestThread(ReaderWriterTest test) {
            rwTest = test;
            random = new Random();
        } 

        public void run() {
            int i = 0;
            while (true) {
                System.out.println("Thread: " + Thread.currentThread().getName() + " Value: " + rwTest.getValue());
                if (random.nextInt(10) < 3) {
                   System.out.println("Thread: " + Thread.currentThread().getName() + " New Value: " + rwTest.incrValue());
                }
            }
            
        }

    }


    public static void main(String[] args) {

        List<Thread> testThreads = new LinkedList<Thread>();

        // Get an instance
        ReaderWriterTest rwInstance = ReaderWriterTest.getInstance();

        // Just keep running the threads
        for (int i = 0; i < NUM_THREADS; i ++) {
            Runnable runner = new TestThread(rwInstance);
            testThreads.add(new Thread(runner));
        }

        for (Thread thread: testThreads)
            thread.start();

    }











}
