import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LruTest {

    public static void main(String[] args) throws FileNotFoundException {
        int size = 7_000;
        int hits = 0;
        int accesses = 0;

        LruCache<String, String> cache = new LruCache<>(size);
        FinalCache<String, String> fcache = new FinalCache<>(size);

        File fire_logs = new File("resources/fire_logs");
        for(File logFile : fire_logs.listFiles()){//all files in this directory
            Scanner in = new Scanner(logFile);
            //iterate over each log in each file
            for(String log = null; in.hasNextLine(); log = in.nextLine()){
                if(log == null) continue;
                if(TestUtils.isNewEntry(log)){
                    String id = TestUtils.newEntryKey(log);
                    cache.put(id, log);
                    fcache.add(id, log);
                }
                else if(TestUtils.isAccess(log) && TestUtils.hasKey(log)){
                    accesses++;
                    String id = TestUtils.accessKey(log);
                    if(cache.get(id.trim()) != null){
                        hits++;
                    }
                }
            }
            in.close();
        }

        System.out.println("accesses = " + accesses);
        System.out.println("hits = " + hits);
    }


}
