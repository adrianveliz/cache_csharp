import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

public class IntervalFinalCacheTest extends TestUtils{
    int interval;
    int intervalSize = 0;
    int hits = 0;
    int accesses = 0;
    //record lruhits and dooms over intervals
    //evictions from doomed set over intervals
    int intervalDooms = 0;
    int intervalHit = 0;
    int doomEvictions = 0;

    int dooms = 0;

    IntervalFinalCache<String, String> cache;

    IntervalFinalCacheTest(int initialFinalCacheSize, int intervalSize){
        cache = new IntervalFinalCache<>(initialFinalCacheSize, intervalSize);
        this.intervalSize = intervalSize;
    }

    int intervalNum = 1;

    @Override
    void newEntryHandler(String entry) {
        cache.add(entry, entry);
    }

    @Override
    void doomHandler(String entry) {
        cache.addToInterval(entry, entry);
        intervalDooms++;

        //reset interval variables
        if(interval < 0){
            System.out.println("intervalNum = " + intervalNum);
            intervalNum++;

            cache.dumpInterval();
            System.out.println("intervalHit = " + intervalHit);
            System.out.println("intervalDooms = " + intervalDooms);
            intervalHit = 0;
            interval = intervalSize;
            intervalDooms = 0;
            cache.resetDoomedSetAmountDoomed();
        }
    }

    @Override
    void accessHandler(String entry) {
        interval--;
        accesses++;
        if(cache.get(entry) != null){
            intervalHit++;
        }
    }

    @Override
    void removalHandler(String entry) {
        cache.remove(entry);
    }

    public static void main(String[] args) throws FileNotFoundException {
        int[] sizes = {50, 250, 500};
        for(int size : sizes){
            IntervalFinalCacheTest test = new IntervalFinalCacheTest(0, size);
            System.setOut(new PrintStream(new File("results" + size + ".txt")));
            test.iterateLogs();
        }
    }
}
