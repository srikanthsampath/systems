package systems.LRU;



public class LruTest {


    public static void main(String[] args) {
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
    }


}
