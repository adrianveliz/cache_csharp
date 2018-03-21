public class LruTest {
    //what we are considering an access
    //they are in format we want
    static String[] accesses = {
            "CacheFileMetadata::GetElement()",
            "CacheFileMetadata::SetElement()"
    };

    boolean isAccess(String log){
        for(String val : accesses){
            if(log.contains(val)){
                return true;
            }
        }
        return false;
    }

    boolean isNewEntry(String log){
        return log.contains("CacheStorageService::AddStorageEntry");
    }

    boolean isRemoved(String log){
        return log.contains("RemoveExactEntry");
    }
}
