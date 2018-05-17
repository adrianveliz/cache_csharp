import java.io.File;
import java.io.FileNotFoundException;
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

    static void iterateLogs(File logDir) throws FileNotFoundException {
        for(File logFile : Objects.requireNonNull(logDir.listFiles())){
            Scanner in = new Scanner(logFile);
            for(String log = null; in.hasNextLine(); log = in.nextLine()){
                if(log == null) continue;
                if(TestUtils.isNewEntry(log)){
                    newEntryHandler();
                }
                else if(TestUtils.isDoom(log)){
                    doomHandler();
                }
                else if(TestUtils.isAccess(log) && TestUtils.hasKey(log)){
                    accessHandler();
                }
                else if(TestUtils.isRemoval(log)){
                    removalHandler();
                }
                in.close();
            }
        }
    }

    static void newEntryHandler(){

    }

    static void doomHandler(){}

    static void accessHandler(){}

    static void removalHandler(){}

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

        FinalCache<String, String> cache = new FinalCache<>(100);
        LruCache<String, String> lcache = new LruCache<>(10_000);
        //when an entry is removed remove it from cache
//        System.setOut(new PrintStream(new File("trackerstatus.txt")));

        File fire_logs = new File("resources/fire_logs");
        for(File logFile : fire_logs.listFiles()){//all files in this directory
            Scanner in = new Scanner(logFile);
            //iterate over each log in each file
            for(String log = null; in.hasNextLine(); log = in.nextLine()){
                if(log == null) continue;
                if(TestUtils.isNewEntry(log)){
                    accesses++;
                    String id = TestUtils.newEntryKey(log);

                    cache.add(id.trim(), log);

                    maxSize = Math.max(maxSize, cache.size());

                    lcache.put(id, log);

                    //get key of most recent eviction
                    String recentLruEviction = lcache.getRecentEviction();
                    if(cache.get(recentLruEviction) != null){
                        potential++;
                    }
                }
                else if(TestUtils.isDoom(log)){
                    dooms++;
                    String id = TestUtils.doomKey(log);
                    if(cache.get(id) != null){
                        fcacheDoomed++;
                    }
                    cache.update(id);
                }
                else if(TestUtils.isAccess(log) && TestUtils.hasKey(log)){
                    String id = TestUtils.accessKey(log);
                    accesses++;
                    if(lcache.get(id.trim()) != null){
                        hits++;
                    }
                    if(cache.get(id.trim()) != null){
                        fcacheHits++;
                    }
                }
                else if(TestUtils.isRemoval(log)){
                    //remove from finalcache
                    String id = TestUtils.removalKey(log);
                    if(cache.get(id) != null){
                        id = id.trim();

                        fcacheRemovals++;
                    }
                    cache.remove(id);
                    removals++;
                }

            }
            in.close();
        }

//        System.out.println("actual duplication: " + (fcacheHits - hits));
//        System.out.println("hits = " + hits);
        System.out.println("fcacheHits = " + fcacheHits);
//        System.setOut(new PrintStream(new File("histogramRaw.txt")));
//
//        int totalGets = 0;
//        for(String key : cache.trackingMap.keySet()){
//            System.out.println(trackingMap.get(key));
//            totalGets += cache.trackingMap.get(key).gets;
//        }
//        System.out.println("Tracker gets " + totalGets);
        System.out.println(cache.i);

//        System.out.println("size = " + cache.size());
//        System.out.println("hits = " + hits);
//        System.out.println("potential = " + potential);
//        System.out.println("maxSize = " + maxSize);
//        System.out.println("removals = " + removals);
        System.out.println("fcacheRemovals = " + fcacheRemovals);
//        System.out.println("fcacheDoomed = " + fcacheDoomed);
//        System.out.println("dooms = " + dooms);
    }
}
