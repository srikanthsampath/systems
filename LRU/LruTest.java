package systems.LRU;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.HashMap;



public class LruTest {

    private static HashMap<Integer, String> testMap = new HashMap<>();    

    public LruTest() {
       testMap.put(0, "APPLE"); 
       testMap.put(1, "ORANGE"); 
       testMap.put(2, "BANANA"); 
       testMap.put(3, "PEAR"); 
       testMap.put(4, "GUAVA"); 
       testMap.put(5, "POMO"); 
       testMap.put(6, "GRAPES"); 
       testMap.put(7, "PEACH"); 
       testMap.put(8, "PLUM"); 
       testMap.put(9, "NECTARINE"); 
    }
    
    public HashMap<Integer, String> getMap() {
        return testMap;
    }

    public static class TestRunnable implements Runnable {
        LRU<Integer, String> lru = null;
        HashMap<Integer, String> inputMap = null;
        Random random;

        public TestRunnable(LRU lru, HashMap<Integer, String> input) {
            this.lru = lru;
            inputMap = input; 
            random = new Random();
        }


        public void run() {
            try {
                for (int i = 0; i < 10; i ++) {
                    int number = random.nextInt(10);
                    String value = inputMap.get(number);
                    lru.put(number, value);
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                System.out.println("Thread Interrupted");
            }
        }
    }


    public static class TestCallable implements Callable<String> {
        LRU<Integer, String> lru = null;
        Random random;

        public TestCallable(LRU inputLru) {
            lru = inputLru;
            random = new Random();
        } 
     
        public String call() {
            int number = random.nextInt(10);
            return lru.get(number);
        } 
    }


    public static void main(String[] args) {


        LruTest test = new LruTest();
        HashMap<Integer, String>  inputMap = test.getMap();
        LRU lru = new LRU(5);


        ExecutorService inserters = Executors.newFixedThreadPool(10);
        ExecutorService selectors = Executors.newFixedThreadPool(20);

        for (int i = 0; i< 10; i ++)
           inserters.execute(new TestRunnable(lru, inputMap));

        while (true) {
           Future<String> future = selectors.submit(new TestCallable(lru));
           try {
               System.out.println("Retrieved: " + future.get());
           } catch (Exception e) {
               System.out.println("Exception");
           }
        }

      


/*
        LRU<Integer, String> sampleLRU = new LRU(3);
        sampleLRU.put(1, "Apple");
        sampleLRU.put(2, "Orange");
        sampleLRU.put(3, "Banana");
        sampleLRU.print();
        String item = sampleLRU.get(1);
        sampleLRU.put(4, "Pear");
        sampleLRU.print();
        item = sampleLRU.get(3);
        sampleLRU.print();
*/
    }


}
