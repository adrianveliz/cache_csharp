import java.io.FileNotFoundException;

public class FiguresTest extends TestUtils{
    int newEntries;
    int removals;
    int dooms;

    @Override
    void newEntryHandler(String entry) {
        newEntries++;
    }

    @Override
    void removalHandler(String entry) {
        removals++;
    }

    @Override
    void doomHandler(String entry) {
        dooms++;
    }

    public static void main(String[] args) {
        FiguresTest test = new FiguresTest();
        try {
            test.iterateLogs();
            System.out.println("test.newEntries = " + test.newEntries);
            System.out.println("test.dooms = " + test.dooms);
            System.out.println("test.removals = " + test.removals);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
