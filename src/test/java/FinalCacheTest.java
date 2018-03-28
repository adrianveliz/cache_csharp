import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FinalCacheTest {

    static String doomKey(String log){
        int start = log.indexOf("for") + 5;
        int end = log.indexOf("because");
        return log.substring(start, end).trim();
    }

    public static void main(String[] args) throws FileNotFoundException {
        int size = 9423;//final size of finalcache on current logs
        int hits = 0;
        int removals = 0;
        int accesses = 0;
        int potential = 0;
        int fcacheRemovals = 0;
        int fcacheDoomed = 0;

        FinalCache<String, Cacheable> cache = new FinalCache<>(size);
        LruCache<String, String> lcache = new LruCache<>(size);
        //when an entry is removed remove it from cache

        File fire_logs = new File("resources/fire_logs");
        for(File logFile : fire_logs.listFiles()){//all files in this directory
            Scanner in = new Scanner(logFile);
            //iterate over each log in each file
            for(String log = null; in.hasNextLine(); log = in.nextLine()){
                if(log == null) continue;

                if(TestUtils.isNewEntry(log)){
                    String id = TestUtils.newEntryKey(log);
                    cache.add(id, new Cacheable(log));

                    lcache.put(id, log);

                    //get key of most recent eviction
                    String recentLruEviction = lcache.getRecentEviction();
                    if(cache.get(recentLruEviction) != null){
                        potential++;
                    }
                }
                else if(TestUtils.isDoom(log) && TestUtils.hasKey(log)){
                    String id = doomKey(log);
                    if(cache.get(id) != null){
                        fcacheDoomed++;
                    }
                    cache.update(id);
                }
                else if(TestUtils.isAccess(log) && TestUtils.hasKey(log)){
                    accesses++;
                    String id = TestUtils.accessKey(log);
                    if(cache.get(id.trim()) != null){
                        hits++;
                    }
                }
                else if(TestUtils.isRemoval(log)){
                    //remove from finalcache
//                    System.out.println("log = \n" + log);
//                    System.out.println("id = \n " + TestUtils.removalKey(log));
                    String id = TestUtils.removalKey(log);
//                    cache
                    if(cache.get(id) != null){
                        fcacheRemovals++;
                    }
                    cache.remove(id);
                    removals++;
                }

            }
            in.close();
        }

//        System.out.println("size = " + cache.size());
        System.out.println("hits = " + hits);
//        System.out.println("removals = " + removals);
        System.out.println("potential = " + potential);
        System.out.println("fcacheRemovals = " + fcacheRemovals);
        System.out.println("fcacheDoomed = " + fcacheDoomed);
    }
}
