import java.io.FileNotFoundException;

public class LruTest extends TestUtils{
    //this is to get the potential and
    //actual duplicates
    FinalCache<String, String> fcache = new FinalCache<>(0);

    //the main testing candidate
    LruCache<String, String> cache;
    int lruhits, fcacheHits, potential;
    LruTest(int size){
        cache = new LruCache<>(size);
        cache.addOnEntryEvictedListener(this::onEntryEvicted);
    }

    @Override
    void newEntryHandler(String entry) {
        //no need to have value, just
        //keeping track of keys
        //adding this as val to not
        //break check for lruhits
        cache.put(entry, entry);
        fcache.add(entry, entry);
    }

    @Override
    void accessHandler(String entry) {
        if(cache.get(entry) != null) lruhits++;
        if(fcache.get(entry) != null) fcacheHits++;
    }

    @Override
    void doomHandler(String entry) {
        fcache.update(entry);
    }

    @Override
    void removalHandler(String entry) {
        fcache.remove(entry);
    }

    public void onEntryEvicted(String key, String val){
        if(fcache.containsKey(key)){
            potential++;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        int[] sizes =
                {50, 100, 250, 500, 1000, 1200,
                        2000, 3000, 4000, 5000, 6000, 7000,
                        8000, 9000, 10_000};

        for(int size : sizes){
            LruTest test = new LruTest(size);
            System.out.println("Test of size: " + size);
            test.iterateLogs();
            System.out.println("lru.lruhits: " + test.lruhits);
            System.out.println("test.fcacheHits: " + test.fcacheHits);
            System.out.println("test.potential: " + test.potential);
            System.out.println("actual duplication: " + (test.fcacheHits - test.lruhits));
            System.out.println();
        }
    }
}
