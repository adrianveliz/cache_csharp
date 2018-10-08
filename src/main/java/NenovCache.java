/**
 * Author: Aleksandr Diamond on 5/29/2018
 * Assignment: cache-java
 * Purpose:
 */
public class NenovCache {
    //cache for strongly referenced entries
    //it is string to boolean so we can
    //know if it has been doomed recently
    LruCache<String, Boolean> strongCache;

    //cache for entries that have been evicted into
    //a weak reference from the strongCache
    FinalCache<String, String> weakCache;

    //size of strongCache
    int strongCacheSize;
    NenovCache(int strongCacheSize){
        this.strongCacheSize = strongCacheSize;
        this.strongCache = new LruCache<>(strongCacheSize);
        this.weakCache = new FinalCache<>(0);
        this.strongCache.addOnEntryEvictedListener(NenovCache.this::onStrongEntryEvicted);
    }

    /**
     * Adds the specified key-val pair if there is room.
     * Assumes that entry has not been doomed as of yet
     * @param key The key to be added to the cache
     */
    public void add(String key){
        if(!strongCache.containsKey(key)||!weakCache.containsKey(key))
        {
            strongCache.putIfAbsent(key,false);
            weakCache.remove(key);
        }
    }

    /**
     * To be called when a doom even happens
     * @param entry The entry that has been doomed
     */
    public void update(String entry){
        //if it is in strongCache and if it has not been doomed
        if(strongCache.containsKey(entry) ){//&& !strongCache.get(entry)){
            //set the flag to true because its been doomed
            //it will be set back if the entry is accessed
            strongCache.replace(entry, true);
        }
        else if(weakCache.containsKey(entry)){
            //if its in the weakCache just remove it
            weakCache.remove(entry);
        }
    }

    /**
     *
     * @param key The key to be removed from the cache
     */
    public void remove(String key){
        strongCache.remove(key);
        weakCache.remove(key);
    }

    /**
     *
     * @param key The key to be tested
     * @return True if in this cache, false otherwise
     */
    public boolean containsKey(String key){
        return strongCache.containsKey(key) || weakCache.containsKey(key);
    }

    /**
     * A simulated get function. Used to update the caches
     * order and eviction policies.
     * @param key Entry to retrieve
     * @return If key is in strongcache return its flag value. Otherwise return whether key is in weakcache.
     */
    public boolean get(String key){
        if(strongCache.containsKey(key)){
            boolean flag = strongCache.get(key);//its been accessed so set flag to false
            strongCache.replace(key,flag);
            return true ;//strongCache.get(key);
        }
        else if(weakCache.containsKey(key)) {
            weakCache.remove(key);
            strongCache.putIfAbsent(key, false);
            strongCache.get(key);
            return true;
        }
        else{
            return false;
//            return weakCache.containsKey(key);
        }
    }

    /**
     *
     * @return The combined size of the strong and weak caches
     */
    public int size(){
        return strongCache.size() + weakCache.size();
    }

    /**
     * Notified when LRU strongCache evicts an entry
     * @param key The key of the evicted entry
     * @param val The val of the evicted entry
     */
    protected void onStrongEntryEvicted(String key, String val){
        //has to do with how java treats nested interfaces
        //and generics...
        //val will always be boolean because of the way this model works
        if(!Boolean.parseBoolean(val)){
            //value can be null
            weakCache.add(key, null);
        }
        //otherwise the entry is removed
    }
}
