import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IntervalFinalCache<K, V extends Cacheable> implements Cache{
    private FinalCache<K, V> cache;
    private LruCache<K, V> interval;
    private int size, intervalSize;

    public IntervalFinalCache(int size, int intervalSize){
        this.size = size;
        cache = new FinalCache<>(size);

        this.intervalSize = intervalSize;
        interval = new LruCache<>(intervalSize);
    }

    public void add(K key, V val) {
        val.setCache(this);
        cache.add(key, val);
    }

    public Cacheable get(K key) {
        //null if nothing
        return cache.get(key);
    }

    //meant to be called with items that are doomed
    //tracking LRU on them
    public void addToInterval(K key, V val){
        interval.put(key, val);
    }

    //meant to be called when interval has elapsed
    //dumps entire interval into finalcache
    //the interval, whatever it is (time, accesses etc) is
    //meant to be handled outside of this class.
    public void dumpInterval(){
        for (K key : interval.keySet()) {
            cache.add(key, interval.get(key));
        }
        interval.clear();
    }
}
