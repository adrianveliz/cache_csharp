import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class FinalCache<K, V> {
    private Map<K, V> cache;
    private LruCache<K, V> doomed;
    private int doomedSize;//of lru


    int i = 0;

    /**
     * for the | Histogram |
     */
    static class Tracker{
        int gets;
        int addedAt;
        int removedAt;
        boolean removed;

        Tracker(){}

        Tracker(int addedAt){this.addedAt = addedAt;}

        @Override
        public String toString() {
            return String.format("gets: %d addedAt: %d removed: %s removedAt: %d", gets, addedAt, removed, removedAt);
        }
    }
    Map<K, Tracker> trackingMap;

    public FinalCache(int doomedSize){
        this.doomedSize = doomedSize;
        cache = new HashMap<>(doomedSize);
        doomed = new LruCache<>(doomedSize);

        trackingMap = new HashMap<>();
    }

    public int size(){
        return cache.size();
    }

    public void remove(K key){
        if(key == null) return;
        cache.remove(key);
        doomed.remove(key);

        //TODO added at...
        if(trackingMap.containsKey(key)){
            trackingMap.get(key).removed = true;
        }
    }

    public void add(K key, V val) {
        cache.put(key, val);//adding duplicates doesnt affect maps

        //TODO added at...
        trackingMap.put(key, new Tracker());
    }

    public V get(K key) {
        if(key == null) return null;
        if(cache.containsKey(key)){
            trackingMap.get(key).gets++;

            i++;
            return cache.get(key);
        }
        if(doomed.containsKey(key)){//doomed contains key
            V c = doomed.get(key);
            //move from doomed to regular set
            doomed.remove(key);
            cache.put(key, c);

            i++;
            trackingMap.get(key).gets++;
            return c;
        }
        return null;
    }

    public boolean containsKey(K key){
        return cache.containsKey(key) || doomed.containsKey(key);
    }

    //to be called when entry is doomed
    public void update(K key){
        if(cache.containsKey(key)){
            //move from cache to doomed set
            V c = cache.get(key);
            cache.remove(key);
            doomed.put(key, c);
        }
    }

    public int getDoomedSetAmountDoomed(){
        return doomed.getAmountDoomed();
    }

    public void resetDoomedSetAmountDoomed(){
        doomed.resetAmountDoomed();
    }
}
