import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

public class IntervalFinalCacheTest extends TestUtils{
    public static void main(String[] args) throws FileNotFoundException {
        int intervalSize = 0;
        int hits = 0;
        int accesses = 0;
        //record hits and dooms over intervals
        //evictions from doomed set over intervals
        int intervalDooms = 0;
        int intervalHit = 0;
        int doomEvictions = 0;

        int dooms = 0;

        IntervalFinalCache<String, String> cache = new IntervalFinalCache<>(50, intervalSize);
        //when an entry is removed remove it from cache
        int intervalNum = 1;


        /*
        File fire_logs = new File("resources/fire_logs");
        int interval = 500;//interval of accesses
        System.setOut(new PrintStream(new File("results" + interval + ".txt")));

        for(File logFile : fire_logs.listFiles()){//all files in this directory
            Scanner in = new Scanner(logFile);
            //iterate over each log in each file
            for(String log = null; in.hasNextLine(); log = in.nextLine()){
                if(log == null) continue;
                if(isNewEntry(log)){
                    interval--;

                    String id = newEntryKey(log);
                    cache.add(id, log);
                }
                else if(TestUtils.isDoom(log)){
                    dooms++;
                    String id = TestUtils.doomKey(log);
                    if(id != null)
                        cache.addToInterval(id, log);
                    intervalDooms++;

                    //reset interval variables
                    if(interval < 0){
                        System.out.println("intervalNum = " + intervalNum);
                        intervalNum++;

                        cache.dumpInterval();
                        System.out.println("intervalHit = " + intervalHit);
                        System.out.println("intervalDooms = " + intervalDooms);
                        intervalHit = 0;
                        interval = 500;
                        intervalDooms = 0;
                        cache.resetDoomedSetAmountDoomed();
                    }
                }
                else if(TestUtils.isAccess(log) && TestUtils.hasKey(log)){
                    interval--;
                    accesses++;
                    String id = TestUtils.accessKey(log);
                    if(cache.get(id.trim()) != null){
                        intervalHit++;
                        hits++;
                    }
                }
                else if(TestUtils.isRemoval(log)){
                    //remove from finalcache
                    String id = TestUtils.removalKey(log);
                    cache.remove(id);
                }
            }
            in.close();
        }

//        System.out.println("accesses = " + accesses);
        System.out.println("hits = " + hits);
        System.out.println("dooms = " + dooms);
        */
    }
}
