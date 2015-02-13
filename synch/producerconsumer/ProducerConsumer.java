package systems.synch.producerconsumer;

import java.util.Random;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import systems.synch.boundedbuffer.BoundedBuffer;

/*
** Driver Producer Consumer 
*/
public class ProducerConsumer {
     
    private BoundedBuffer<Item> buffer = null;

    private static final Options OPTIONS = new Options();
    private static final String OPT_NSLOTS = "n";
    private static final String OPT_LONG_NSLOTS = "num-slots";
    private static final String OPT_NUM_PRODUCERS = "p";
    private static final String OPT_LONG_NUM_PRODUCERS = "num-producers";
    private static final String OPT_NUM_CONSUMERS = "c";
    private static final String OPT_LONG_NUM_CONSUMERS = "num-consumers";

    private int numSlots = 50;
    private int numProducers = 10;
    private int numConsumers = 10;

    static {
        OPTIONS.addOption(
            OPT_NSLOTS,
            OPT_LONG_NSLOTS,
            true,
            "Number of slots in the Bounded Buffer");
        OPTIONS.addOption(
            OPT_NUM_PRODUCERS,
            OPT_LONG_NUM_PRODUCERS,
            true,
            "Number of producer threads");
        OPTIONS.addOption(
            OPT_NUM_CONSUMERS,
            OPT_LONG_NUM_CONSUMERS,
            true,
            "Number of consumer threads");
    }


    // A Simple Item Class
    class Item{
        private int item;

        public Item(int value) {
            item = value;
        }

        public int getValue() {
            return item;
        }
    }

    // Create a synchronized list and a bounded buffer
    public ProducerConsumer() {
        List<Item> itemList = Collections.synchronizedList(new LinkedList<Item>());    
        buffer = new BoundedBuffer(numSlots, itemList);
    }

    public void setSlots(int slots) {
        numSlots = slots;
    }

    public void setProducers(int producers) {
        numProducers = producers;
    }

    public void setConsumers(int consumers) {
        numConsumers = consumers;
    }

    // Producer Thread
    public class ProducerThread implements Runnable {

        BoundedBuffer boundedBuffer = null;
        Random random = null;
          
        public ProducerThread(BoundedBuffer bb) {
            boundedBuffer = bb;
            random = new Random();
        }
        
        // Producer thread creates an item and adds it to the bounded buffer
        public void run() {
            while (true) {
                try {
                    Item item = new Item(random.nextInt());
                    System.out.println("Thread: " + Thread.currentThread().getName() + " Queue Length: " + boundedBuffer.getQueueLength());
                    boundedBuffer.putItem(item);
                    System.out.println("Thread: " + Thread.currentThread().getName() + " Put Item " + item.getValue());
//                    Thread.sleep(100); // Sleeps so that we don't hog the CPU
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted " + Thread.currentThread().getName());
                    return;
                } 
    
            }
        }
    }

    // Consumer thread that consumes an item from the BB
    public class ConsumerThread implements Runnable {

        BoundedBuffer boundedBuffer = null;

        public ConsumerThread(BoundedBuffer bb) {
            boundedBuffer = bb;
        }

        public void run() {
            while (true) {
                try {
                    Item item = (Item)boundedBuffer.getItem();
                    System.out.println("Thread: " + Thread.currentThread().getName() + " Retrieved Item " + item.getValue());
//                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted " + Thread.currentThread().getName());
                    return;
                } 
            }
        }
        

    }

    // Operative method - create the threads and start
    public void run() {

        LinkedList<Thread> producerThreads = new LinkedList<Thread>();

        // Create producer threads
        for (int i = 0; i < numProducers; i ++) {
            producerThreads.add(new Thread(new ProducerThread(buffer)));
        }

        LinkedList<Thread> consumerThreads = new LinkedList<Thread>();

        // Create consumer threads
        for (int i = 0; i < numConsumers; i ++) {
            consumerThreads.add(new Thread(new ConsumerThread(buffer)));
        }
        
        for (Thread thread : producerThreads) 
            thread.start();

        for (Thread thread : consumerThreads) 
            thread.start();

    }

    // Driver 
    public static void main(String[] args) {

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(OPTIONS, args);
        } catch (ParseException e) {
            System.out.println("Parse Exception : " + e.getMessage());
            return;
        } 


        // Setup the producer consumer
        ProducerConsumer pc = new ProducerConsumer();

        if (cmd.hasOption(OPT_NSLOTS)) {
            int slots = Integer.valueOf(cmd.getOptionValue(OPT_NSLOTS)).intValue();
            pc.setSlots(slots);
        }
            

        if (cmd.hasOption(OPT_NUM_PRODUCERS)) {
            int producers = Integer.valueOf(cmd.getOptionValue(OPT_NUM_PRODUCERS)).intValue(); 
            pc.setProducers(producers);
        }

        if (cmd.hasOption(OPT_NUM_CONSUMERS)) {
            int consumers = Integer.valueOf(cmd.getOptionValue(OPT_NUM_CONSUMERS)).intValue(); 
            pc.setConsumers(consumers);
        }


        // Run
        pc.run();

    }

}
