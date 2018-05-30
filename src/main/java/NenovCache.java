/**
 * Author: Aleksandr Diamond on 5/29/2018
 * Assignment: cache-java
 * Purpose:
 */
public class NenovCache {
    //cache for strongly referenced entries
    LruCache<String, String> strongCache;

    //cache for entries that have been evicted into
    //a weak reference from the strongCache
    FinalCache<String, String> weakCache;

    //size of strongCache
    int strongCacheSize;
    NenovCache(int strongCacheSize){
        this.strongCacheSize = strongCacheSize;
        this.strongCache = new LruCache<>(strongCacheSize);
        this.strongCache.addOnEntryEvictedListener(NenovCache.this::onStrongEntryEvicted);
    }

    /**
     * Adds the specified key-val pair if there is room
     * @param key The key to be added to the cache
     * @param val Value to be added to the cache, mapped to key
     */
    public void add(String key, String val){
        strongCache.put(key, val);
    }

    /**
     *
     */
    public void removeFromFinalCache(String entry){

    }

    /**
     *
     * @param key The key to be removed from the cache
     */
    public void remove(String key){

    }

    public boolean containsKey(String key){
        return strongCache.containsKey(key) || weakCache.containsKey(key);
    }

    /**
     *
     * @return The combined size of the strong and weak caches
     */
    public int size(){
        return strongCache.size() + weakCache.size();
    }

    protected void onStrongEntryEvicted(String key, String val){

    }
}
