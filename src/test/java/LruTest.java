import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LruTest {
    //what we are considering an access
    //they are in format we want
    static String[] accesses = {
            "CacheFileMetadata::GetElement()",
            "CacheFileMetadata::SetElement()"
    };

    static boolean isAccess(String log){
        for(String val : accesses){
            if(log.contains(val)){
                return true;
            }
        }
        return false;
    }

    static boolean isNewEntry(String log){
        return log.contains("CacheStorageService::AddStorageEntry");
    }

    static boolean isRemoved(String log){
        return log.contains("RemoveExactEntry");
    }

    static boolean hasKey(String log){
        return log.contains("key=predictor::http");
    }

    static boolean isDoom(String log){
        return log.contains("dooming entry");
    }

    static String newEntryKey(String log){
        int beginIndex = log.indexOf("entryKey=:") + 10;
        int endIndex = log.indexOf(",");

        return log.substring(beginIndex, endIndex);
    }

    static String accessKey(String log){
        int beginIndex = log.indexOf("key=predictor::") + 15;
        int endIndex = log.length() - 1;//hopefully gets rid of '['
        return log.substring(beginIndex, endIndex);
    }

    public static void main(String[] args) throws FileNotFoundException {
        int size = 50;
        int hits = 0;
        int accesses = 0;

        FinalCache<String, Cacheable> cache = new FinalCache<>(size);

        File fire_logs = new File("resources/fire_logs");
        for(File logFile : fire_logs.listFiles()){//all files in this directory
            Scanner in = new Scanner(logFile);
            //iterate over each log in each file
            for(String log = null; in.hasNextLine(); log = in.nextLine()){
                if(log == null) continue;
                if(isNewEntry(log)){
                    String id = newEntryKey(log);
//                    cache.put(id, log);
                    cache.add(id, new Cacheable(log));
                }
                else if(isAccess(log) && hasKey(log)){
                    accesses++;
                    String id = accessKey(log);
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
