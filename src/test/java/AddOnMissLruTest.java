import java.io.FileNotFoundException;

public class AddOnMissLruTest extends TestUtils{
    LruCache<String, String> cache;
    LruCache<String, String> addOnMissCache;
    int lruHits, addOnMissHits;

    AddOnMissLruTest(int size){
        cache = new LruCache<>(size);
        addOnMissCache = new LruCache<>(size);
    }

    @Override
    void newEntryHandler(String entry) {
        cache.put(entry, entry);
        addOnMissCache.put(entry, entry);
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
            System.out.println("test.lruHits = " + test.lruHits);
            System.out.println("test.addOnMissHits = " + test.addOnMissHits);
            System.out.println();
        }


    }
}
