import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Aleksandr Diamond on 5/24/2018
 * Assignment: cache-java
 * Purpose:
 */
public class HistogramTest extends TestUtils{
    FinalCache<String, String> cache = new FinalCache<>(100);
    Map<String, Tracker> trackerMap = new HashMap<>();

    //intervals of accesses
    int accesses = 0;

    static class Tracker{
        int gets;
        int addedAt;
        int removedAt;
        boolean removed;
        Tracker(int addedAt){this.addedAt = addedAt;}

        @Override
        public String toString() {
            return String.format("gets: %d addedAt: %d removed: %s removedAt: %d", gets, addedAt, removed, removedAt);
        }
    }

    @Override
    void newEntryHandler(String entry) {
        accesses++;
        cache.add(entry, null);
        //to avoid duplicates messing up tracking data
        if(!trackerMap.containsKey(entry)){
            trackerMap.put(entry, new Tracker(accesses));
        }
    }

    @Override
    void doomHandler(String entry) {
        cache.update(entry);
        String recentEviction = cache.getRecentEvection();
        if(recentEviction != null){//at beginning when doomed set isnt full
            trackerMap.get(recentEviction).removed = true;
            trackerMap.get(recentEviction).removedAt = accesses;
        }
    }

    @Override
    void accessHandler(String entry) {
        accesses++;
        if(cache.containsKey(entry)){
            cache.get(entry);//so cache can update itself
            trackerMap.get(entry).gets += 1;
        }
    }

    @Override
    void removalHandler(String entry) {
        if(cache.containsKey(entry)){
            trackerMap.get(entry).removed = true;
            trackerMap.get(entry).removedAt = accesses;
        }
        cache.remove(entry);
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.setOut(new PrintStream(new File("histogramRaw.txt")));
        HistogramTest test = new HistogramTest();
        test.iterateLogs();

        for(String key : test.trackerMap.keySet()){
            Tracker val = test.trackerMap.get(key);
            if(val.removed){
                System.out.println("Lifetime: " + (val.removedAt - val.addedAt) + " Entry: " + key);
            }
            else {
                //this case seems to comprise the vast majority of entries. almost by a factor of 10
                //compared to the other case
                System.out.println("Lifetime: " + (test.accesses - val.addedAt) + " Entry: " + key);
            }
        }
    }
}
