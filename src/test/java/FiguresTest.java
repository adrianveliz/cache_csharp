import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashSet;

public class FiguresTest extends TestUtils{
    int additions;
    int removals;
    int dooms;

    /**
     * Holds the unique keys associated with each addition
     */
    HashSet<String> keys = new HashSet<>();

    @Override
    void newEntryHandler(String entry) {
        additions++;
        keys.add(entry);
//        System.out.println(entry);
    }

    @Override
    void removalHandler(String entry) {
        removals++;
    }

    @Override
    void doomHandler(String entry) {
        dooms++;
    }

    public static void main(String[] args) throws FileNotFoundException {
        FiguresTest test = new FiguresTest();
//        System.setOut(new PrintStream(new File("addKeys.txt")));
        try {
            test.iterateLogs();
            System.out.println("test.additions = " + test.additions);
            System.out.println("unique additions = " + test.keys.size());
            System.out.println("test.dooms = " + test.dooms);
            System.out.println("test.removals = " + test.removals);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
