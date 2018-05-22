import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class SizeTest extends TestUtils {
    FinalCache<String, String> cache = new FinalCache<>(100);

    /**
     * Interval of access
     */
    static final int intervalSize = 100;


    int intervalMax, intervalMin;

    /**
     * Holds current position of interval
     */
    int counter;

    @Override
    void newEntryHandler(String entry) {
        intervalMax = Math.max(intervalMax, cache.size());
        intervalMin = Math.min(intervalMin, cache.size());
        onCacheAccess();

        //doesnt need to have a value, just tracking entryset
        cache.add(entry, null);
    }

    @Override
    void doomHandler(String entry) {
        cache.update(entry);
    }

    @Override
    void accessHandler(String entry) {
        intervalMax = Math.max(intervalMax, cache.size());
        intervalMin = Math.min(intervalMin, cache.size());
        onCacheAccess();

        //to update caches sets, could change configuration
        cache.get(entry);
    }

    private void onCacheAccess() {
        if(counter == intervalSize){
            System.out.println("intervalMax: " + intervalMax);
            System.out.println("intervalMin: " + intervalMin);
            //reset interval variables
            counter = 0;
            intervalMax = Integer.MIN_VALUE;
            intervalMin = Integer.MAX_VALUE;
        }
        else {
            counter++;
        }
    }

    @Override
    void removalHandler(String entry) {
        cache.remove(entry);
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.setOut(new PrintStream("cacheSizeRaw.txt"));
        SizeTest sizeTest = new SizeTest();
        try {
            sizeTest.iterateLogs();
        } catch (FileNotFoundException e) {
            System.out.println("Problem with files in " + sizeTest.logDir);
            e.printStackTrace();
        }
    }
}
