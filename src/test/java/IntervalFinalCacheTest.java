import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class IntervalFinalCacheTest {
    public static void main(String[] args) throws FileNotFoundException {
        int intervalSize = 10;
        int hits = 0;
        int accesses = 0;

        IntervalFinalCache<String, Cacheable> cache = new IntervalFinalCache<>(intervalSize);
        //when an entry is removed remove it from cache

        File fire_logs = new File("resources/fire_logs");
        int interval = 100;//interval of accesses

        for(File logFile : fire_logs.listFiles()){//all files in this directory
            Scanner in = new Scanner(logFile);
            //iterate over each log in each file
            for(String log = null; in.hasNextLine(); log = in.nextLine()){
                if(log == null) continue;
                if(TestUtils.isNewEntry(log)){
                    interval--;

                    String id = TestUtils.newEntryKey(log);
                    cache.add(id, new Cacheable(log));
                }
                else if(TestUtils.isDoom(log) && TestUtils.hasKey(log)){
                    String id = FinalCacheTest.doomKey(log);
                    cache.addToInterval(id, new Cacheable(log));
                    if(interval < 0){
                        cache.dumpInterval();
                        interval = 100;
                    }
                }
                else if(TestUtils.isAccess(log) && TestUtils.hasKey(log)){
                    interval--;

                    accesses++;
                    String id = TestUtils.accessKey(log);
                    if(cache.get(id.trim()) != null){
                        hits++;
                    }
                }
                else if(TestUtils.isRemoval(log)){
                    interval--;
                    //remove from finalcache
//                    System.out.println("log = \n" + log);
//                    System.out.println("id = \n " + TestUtils.removalKey(log));
                    String id = TestUtils.removalKey(log);
//                    cache
                    cache.remove(id);
                }
            }
            in.close();
        }

        System.out.println("accesses = " + accesses);
        System.out.println("hits = " + hits);
    }
}
