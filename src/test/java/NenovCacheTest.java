import java.io.FileNotFoundException;

/**
 * Author: Aleksandr Diamond on 5/29/2018
 * Assignment: cache-java
 * Purpose:
 */
public class NenovCacheTest extends TestUtils{
    NenovCache cache;
    int hits;

    NenovCacheTest(int size){
        cache = new NenovCache(size);
    }

    @Override
    void newEntryHandler(String entry) {
        cache.add(entry);
    }

    @Override
    void accessHandler(String entry) {
        cache.get(entry);
        if(cache.containsKey(entry)) hits++;
    }

    @Override
    void doomHandler(String entry) {
        cache.update(entry);
    }

    @Override
    void removalHandler(String entry) {
        cache.remove(entry);
    }

    public static void main(String[] args) throws FileNotFoundException {
        NenovCacheTest n = null;
        for (int i = 0; i < 10; i++) {
            System.out.println("Starting test of size: " + (i * 1000));
            n = new NenovCacheTest(i * 1000);
            n.iterateLogs();
            System.out.println("hits: " + n.hits);
            System.out.println("size: " + n.cache.size());
            System.out.println();
        }
    }
}
