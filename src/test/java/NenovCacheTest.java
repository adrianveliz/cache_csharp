import java.io.FileNotFoundException;

/**
 * Author: Aleksandr Diamond on 5/29/2018
 * Assignment: cache-java
 * Purpose:
 */
public class NenovCacheTest extends TestUtils{
    NenovCache cache;
    int hits;
    int maxNumOfEntries = Integer.MIN_VALUE;

    NenovCacheTest(int size){
        cache = new NenovCache(size);
    }

    @Override
    void newEntryHandler(String entry) {
        cache.add(entry);
        maxNumOfEntries = Math.max(cache.size(), maxNumOfEntries);
    }

    @Override
    void accessHandler(String entry) {
        cache.get(entry);
        if(cache.containsKey(entry)) hits++;
        maxNumOfEntries = Math.max(cache.size(), maxNumOfEntries);
    }

    @Override
    void doomHandler(String entry) {
        cache.update(entry);
        maxNumOfEntries = Math.max(cache.size(), maxNumOfEntries);
    }

    @Override
    void removalHandler(String entry) {
        cache.remove(entry);
        maxNumOfEntries = Math.max(cache.size(), maxNumOfEntries);
    }

    public static void main(String[] args) throws FileNotFoundException {
        NenovCacheTest n = null;
        int[] nums =
                {0, 50, 100, 250, 500, 1000, 1200,
                2000, 3000, 4000, 5000, 6000, 7000,
                8000, 9000, 10_000};

        //int [] nums = {0,1,2,5,10,50,60,70,80,90,100};
        for (int num : nums) {
            System.out.println("Starting test of size: " + (num));
            n = new NenovCacheTest(num);
            n.iterateLogs();
            System.out.println("lruhits: " + n.hits);
            System.out.println("maxNumOfEntries: " + n.maxNumOfEntries);
            System.out.println();
        }
    }
}
