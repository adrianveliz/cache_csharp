public class TestUtils {

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

    static boolean isRemoval(String log){
        return log.contains("CacheStorageService::RemoveEntryForceValid");
    }

    static String removalKey(String log){
        //some entries are not prefixed with 'http'
        try{
            int start = log.indexOf("entryKey=:");
            return log.substring(start);
        } catch(Exception e){
            return null;
        }
    }

    static String doomKey(String log){
        int start = log.indexOf("for") + 5;
        int end = log.indexOf("because");
        try {
            return log.substring(start, end).trim();
        }catch(StringIndexOutOfBoundsException e){
            return null;
        }
    }
}
