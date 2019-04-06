import java.io.FileNotFoundException;

/** TODO, Collect
 *  Regular additions,
 *  Adds because of misses,
 *  Num of Evictions (regular as well)
 */

public class AddOnMissLruTest extends TestUtils{
    LruCache<String, String> cache;
    LruCache<String, String> addOnMissCache;
    int lruHits, addOnMissHits;
    int adds, addsOnMiss;
    int evictions;//evictions from addOnMissCache

    AddOnMissLruTest(int size){
        cache = new LruCache<>(size);
        addOnMissCache = new LruCache<>(size);
        addOnMissCache.addOnEntryEvictedListener((key, val) -> evictions++);
    }

    @Override
    void newEntryHandler(String entry) {
        cache.put(entry, entry);
        addOnMissCache.put(entry, entry);
        adds++;
    }

    @Override
    void accessHandler(String entry) {
        //symbolic 'gets' to maintain lru
        if(cache.get(entry) != null) lruHits++;

        if(addOnMissCache.get(entry) != null){
            addOnMissHits++;
        }
        else {
            //add on miss
            addOnMissCache.put(entry, entry);
            addsOnMiss++;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        AddOnMissLruTest test;
        int[] sizes =
                {50, 100, 250, 500, 1000, 1200,
                        2000, 3000, 4000, 5000, 6000, 7000,
                        8000, 9000, 10_000};
        for (int size : sizes) {
            test = new AddOnMissLruTest(size);
            test.iterateLogs();
            System.out.println("test of size " + size);
            System.out.println("test.addOnMissHits = " + test.addOnMissHits);
            System.out.println("test.addsOnMiss = " + test.addsOnMiss);
            System.out.println("test.adds = " + test.adds);
            System.out.println("test.evictions = " + test.evictions);
            System.out.println();
        }


    }
}
