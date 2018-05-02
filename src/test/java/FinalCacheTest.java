import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class FinalCacheTest {

    static String doomKey(String log){
        int start = log.indexOf("for") + 5;
        int end = log.indexOf("because");
        try {
            return log.substring(start, end).trim();
        }catch(StringIndexOutOfBoundsException e){
            return null;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        int size = 50;
        int hits = 0;
        int removals = 0;
        int accesses = 0;
        int potential = 0;
        int fcacheRemovals = 0;
        int fcacheDoomed = 0;
        int dooms = 0;
        int fcacheHits = 0;

        int maxSize = 0;

        class Tracker{
            int gets;
            int addedAt;
            int removedAt;
            boolean removed;
            Tracker(int addedAt){this.addedAt = addedAt;}

            @Override
            public String toString() {
                String val = "gets: " + gets + " addedAt: " + addedAt + " removed: " + removed + " removedAt: " + removedAt;
                return val;
            }
        }

        Map<String, Tracker> trackingMap = new HashMap<>();

        FinalCache<String, String> cache = new FinalCache<>(100);
        LruCache<String, String> lcache = new LruCache<>(10_000);
        //when an entry is removed remove it from cache

        File fire_logs = new File("resources/fire_logs");
        for(File logFile : fire_logs.listFiles()){//all files in this directory
            Scanner in = new Scanner(logFile);
            //iterate over each log in each file
            for(String log = null; in.hasNextLine(); log = in.nextLine()){
                if(log == null) continue;
                if(TestUtils.isNewEntry(log)){
                    accesses++;
                    String id = TestUtils.newEntryKey(log);

//                    Tracker tracker = new Tracker();
//                    tracker.addedAt = accesses;
                    trackingMap.put(id.trim(), new Tracker(accesses));

                    cache.add(id.trim(), log);

                    maxSize = cache.size() > maxSize ? cache.size() : maxSize;

                    lcache.put(id, log);

                    //get key of most recent eviction
                    String recentLruEviction = lcache.getRecentEviction();
                    if(cache.get(recentLruEviction) != null){
                        potential++;
                    }
                }
                else if(TestUtils.isDoom(log)){
                    dooms++;
                    String id = doomKey(log);
                    if(cache.get(id) != null){
                        fcacheDoomed++;
                    }
                    cache.update(id);
                }
                else if(TestUtils.isAccess(log) && TestUtils.hasKey(log)){
                    accesses++;
                    String id = TestUtils.accessKey(log);
                    if(lcache.get(id.trim()) != null){
                        hits++;
                    }
                    String entry = cache.get(id.trim());
                    if(entry != null){
                        fcacheHits++;

                        if(trackingMap.get(entry) != null){
                            trackingMap.get(entry).gets++;
                        }
                    }
                }
                else if(TestUtils.isRemoval(log)){
                    //remove from finalcache
                    String id = TestUtils.removalKey(log);
                    if(cache.get(id) != null){
                        fcacheRemovals++;
                        trackingMap.get(id).removed = true;
                        trackingMap.get(id).removedAt = accesses;
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

//        for(String key : trackingMap.keySet()){
//            System.out.println(trackingMap.get(key));
//        }

//        System.out.println("size = " + cache.size());
//        System.out.println("hits = " + hits);
//        System.out.println("potential = " + potential);
//        System.out.println("maxSize = " + maxSize);
        System.out.println("removals = " + removals);
        System.out.println("fcacheRemovals = " + fcacheRemovals);
        System.out.println("fcacheDoomed = " + fcacheDoomed);
//        System.out.println("dooms = " + dooms);
    }
}
