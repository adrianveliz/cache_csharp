import java.util.HashMap;
import java.util.Map;


public class FinalCache<K, V> implements Cache{
    private Map<K, V> cache;
    private LruCache<K, V> doomed;
    private int doomedSize;//of lru

    public FinalCache(int doomedSize){
        this.doomedSize = doomedSize;
        cache = new HashMap<>(doomedSize);
        doomed = new LruCache<>(doomedSize);
    }

    public int size(){
        return cache.size();
    }

    public void remove(K key){
        if(key == null) return;
        cache.remove(key);
        doomed.remove(key);
    }

    public void add(K key, V val) {
//        val.setCache(this);
        cache.put(key, val);//adding duplicates doesnt affect maps
    }

    public V get(K key) {
        if(cache.containsKey(key)){
            return cache.get(key);
        }
        if(doomed.get(key) != null){//doomed contains key
            V c = doomed.get(key);
            //move from doomed to regular set
            doomed.remove(key);
            cache.put(key, c);
            return c;
        }
        return null;
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
