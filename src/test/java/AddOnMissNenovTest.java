import java.io.FileNotFoundException;

public class AddOnMissNenovTest extends TestUtils{
    NenovCache cache;
    int hits;

    AddOnMissNenovTest(int size){
        cache = new NenovCache(size);
    }

    @Override
    void newEntryHandler(String entry) {
        cache.add(entry);
    }

    @Override
    void accessHandler(String entry) {
        if(!cache.containsKey(entry)) {
            cache.add(entry);
        } else {
            cache.get(entry);
            hits++;
        }
    }

    @Override
    void doomHandler(String entry) {
        cache.update(entry);
    }

    @Override
    void removalHandler(String entry) {
        cache.remove(entry);
    }

    public static void main(String[] args) throws FileNotFoundException {
        AddOnMissNenovTest test;

        int[] sizes =
                {50, 100, 250, 500, 1000, 1200,
                2000, 3000, 4000, 5000, 6000, 7000,
                8000, 9000, 10_000};
        for (int size : sizes) {
            System.out.println("Starting test of size: " + size);
            test = new AddOnMissNenovTest(size);
            test.iterateLogs();
            System.out.println("test.hits = " + test.hits);
        }
    }
}
