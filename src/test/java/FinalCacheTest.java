import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class FinalCacheTest {

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


    public static void main(String[] args) throws FileNotFoundException {
        int size = 50;
        int hits = 0;
        int removals = 0;
        int accesses = 0, intervalSize = 100;
        int potential = 0;
        int fcacheRemovals = 0;
        int fcacheDoomed = 0;
        int dooms = 0;
        int fcacheHits = 0;

        int maxSize = 0;

        FinalCache<String, String> cache = new FinalCache<>(100);
        LruCache<String, String> lcache = new LruCache<>(10_000);

        Map<String, Tracker> trackerMap = new HashMap<>();

        System.setOut(new PrintStream(new File("sizeRaw.txt")));

        File fire_logs = new File("resources/fire_logs");
        for(File logFile : fire_logs.listFiles()){//all files in this directory
            Scanner in = new Scanner(logFile);
            //iterate over each log in each file
            for(String log = null; in.hasNextLine(); log = in.nextLine()){
                if(log == null) continue;
                if(TestUtils.isNewEntry(log)){
                    accesses++;
                    if (accesses == 100) {
                        accesses = 0;
                        System.out.println("Cache Size: " + cache.size());
                    }

                    String id = TestUtils.newEntryKey(log);

                    cache.add(id.trim(), log);

                    maxSize = Math.max(maxSize, cache.size());

                    lcache.put(id, log);

                    //only add if its not there
                    if(!trackerMap.containsKey(id.trim())){
                        trackerMap.put(id.trim(), new Tracker(accesses));
                    }
                    //get key of most recent eviction
//                    String recentLruEviction = lcache.getRecentEviction();
//                    if(cache.get(recentLruEviction) != null){
//                        potential++;
//                    }
                }
                else if(TestUtils.isDoom(log)){
                    dooms++;
                    String id = TestUtils.doomKey(log);
//                    if(cache.get(id) != null){
//                        fcacheDoomed++;
//                    }
                    cache.update(id);

                    String recentEvection = cache.getRecentEvection();
                    if(recentEvection != null){//at beginning when doomed set isnt full
                        trackerMap.get(recentEvection).removed = true;
                        trackerMap.get(recentEvection).removedAt = accesses;
                    }

                }
                else if(TestUtils.isAccess(log) && TestUtils.hasKey(log)){
                    accesses++;
                    if (accesses == 100) {
                        accesses = 0;
                        System.out.println("Cache Size: " + cache.size());
                    }

                    String id = TestUtils.accessKey(log);
                    if(lcache.get(id.trim()) != null){
                        hits++;
                    }
                    if(cache.get(id.trim()) != null){
                        fcacheHits++;
                        trackerMap.get(id.trim()).gets++;
                    }
                }
                else if(TestUtils.isRemoval(log)){
                    //remove from finalcache
                    String id = TestUtils.removalKey(log);
                    if(cache.get(id) != null){
                        id = id.trim();
                        fcacheRemovals++;

                        trackerMap.get(id.trim()).removed = true;
                        trackerMap.get(id.trim()).removedAt = accesses;
                    }
                    cache.remove(id);
                    removals++;
                }

            }
            in.close();
        }

//        System.out.println("actual duplication: " + (fcacheHits - hits));
//        System.out.println("hits = " + hits);
//        System.out.println("fcacheHits = " + fcacheHits);
//        System.setOut(new PrintStream(new File("histogramRaw.txt")));


//        for(String key : trackerMap.keySet()){
//            Tracker  t = trackerMap.get(key);
//            if(t.removed){//if entry was removed
//                System.out.println("Lifetime: " + (t.removedAt - t.addedAt) + " Entry: " + key);
//            }
//        }


//        System.out.println("Tracker gets " + totalGets);
//        System.out.println(cache.i);

//        System.out.println("size = " + cache.size());
//        System.out.println("hits = " + hits);
//        System.out.println("potential = " + potential);
//        System.out.println("maxSize = " + maxSize);
//        System.out.println("removals = " + removals);
//        System.out.println("fcacheRemovals = " + fcacheRemovals);
//        System.out.println("fcacheDoomed = " + fcacheDoomed);
//        System.out.println("dooms = " + dooms);
    }
}
