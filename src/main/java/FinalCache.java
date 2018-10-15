import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class FinalCache<K, V> {
    private Map<K, V> cache;
    private LruCache<K, V> doomed;
    private int doomedSize;//of lru

    /**
     * for the Histogram
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
        return cache.size()+doomed.size();
    }

    public void remove(K key){
        if(key == null) return;
        cache.remove(key);
        doomed.remove(key);

        //TODO removed at...
        if(trackingMap.containsKey(key)){
            trackingMap.get(key).removed = true;
        }
    }

    public void add(K key, V val) {
        cache.remove(key);
        cache.putIfAbsent(key,val);
        doomed.remove(key);
        //TODO added at...
        if(!trackingMap.containsKey(key)){
            trackingMap.putIfAbsent(key, new Tracker());
        }
    }

    public void add(K key, V val, int addedAt){
        cache.remove(key);
        cache.putIfAbsent(key, val);
        doomed.remove(key);
        if(!trackingMap.containsKey(key)){
            trackingMap.putIfAbsent(key, new Tracker(addedAt));
        }
    }

    public V get(K key) {
        if(key == null) return null;
        if(doomed.containsKey(key)){//doomed contains key
            V c = doomed.get(key);
            trackingMap.get(key).gets++;
            return c;
        }

        else if(cache.containsKey(key)){
            trackingMap.get(key).gets++;
            doomed.remove(key);
            return cache.get(key);
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
            doomed.remove(key);
            doomed.putIfAbsent(key, c);
        }
        else if(doomed.containsKey(key)) {
            System.out.println("That's odd: "+key.toString());
        }
        else{
            //System.out.println("Doom without an add:"+key.toString());
        }
    }

    public int getDoomedSetAmountDoomed(){
        return doomed.getAmountDoomed();
    }

    public void resetDoomedSetAmountDoomed(){
        doomed.resetAmountDoomed();
    }

    public K getRecentEvection(){
        return doomed.getRecentEviction();
    }
}
