import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public class TestUtils {

    //what we are considering an access
    //they are in format we want
    String[] accesses = {
            "CacheFileMetadata::GetElement()",
            "CacheFileMetadata::SetElement()"
    };

    File logDir = new File("resources/fire_logs");

    boolean isAccess(String log){
        for(String val : accesses){
            if(log.contains(val)){
                return true;
            }
        }
        return false;
    }

    boolean isNewEntry(String log){
        if(log.contains("entryKey=~")) return false;
        return log.contains("CacheStorageService::AddStorageEntry");
    }

    boolean hasKey(String log){
        return log.contains("key=predictor::http");
    }

    boolean isDoom(String log){
        return log.contains("dooming entry");
    }

    String newEntryKey(String log){
        int beginIndex = log.indexOf("entryKey=:") + 10;
        int endIndex = log.indexOf(",");

        return log.substring(beginIndex, endIndex);
    }

    String accessKey(String log){
        int beginIndex = log.indexOf("key=predictor::") + 15;
        int endIndex = log.length() - 1;//hopefully gets rid of '['
        return log.substring(beginIndex, endIndex);
    }

    boolean isRemoval(String log){
        return log.contains("CacheStorageService::RemoveEntryForceValid");
    }

    String removalKey(String log){
        //some entries are not prefixed with 'http'
        try{
            int start = log.indexOf("entryKey=:");
            return log.substring(start);
        } catch(Exception e){
            return null;
        }
    }

    String doomKey(String log){
        int start = log.indexOf("for") + 5;
        int end = log.indexOf("because");
        try {
            return log.substring(start, end).trim();
        }catch(StringIndexOutOfBoundsException e){
            return null;
        }
    }

    void iterateLogs() throws FileNotFoundException {
        if(logDir == null || logDir.listFiles() == null){
            throw new FileNotFoundException("Problem with files in " + logDir);
        }
        for(File logFile : logDir.listFiles()){
            Scanner in = new Scanner(logFile);
            for(String log = null; in.hasNextLine(); log = in.nextLine()){
                if(log == null) continue;
                if(isNewEntry(log)){
                    newEntryHandler(newEntryKey(log).trim());
                }
                else if(isDoom(log)){
                    String doomKey = doomKey(log);
                    if (doomKey != null) {
                        doomHandler(doomKey.trim());
                    }
                }
                else if(isAccess(log) && hasKey(log)){
                    accessHandler(accessKey(log).trim());
                }
                else if(isRemoval(log)){
                    String removalKey = removalKey(log);
                    if(removalKey != null){
                        removalHandler(removalKey.trim());
                    }
                }
            }
            in.close();
        }
    }

    void newEntryHandler(String entry){}

    void doomHandler(String entry){}

    void accessHandler(String entry){}

    void removalHandler(String entry){}
}
