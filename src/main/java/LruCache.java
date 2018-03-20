import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LruCache implements Cache {
    private Map<String, Cacheable> cache;
    private List<Cacheable> lru;
    private int size;

    public LruCache(int size) {
        this.size = size;
        lru = new ArrayList<Cacheable>();
        cache = new HashMap<String, Cacheable>();
    }

    public void print() {
        for (Cacheable val : cache.values()) {
            System.out.println(val);
        }
    }

    @Override
    public void add(String key, Cacheable val) {
        //there may need to be catch block here. original has it
        if (lru.size() >= size) {
            this.evict();
        }
        val.setCache(this);
        cache.put(key, val);
        lru.add(0, val);//most recent
    }

    @Override
    public Cacheable get(String key){
        if(cache.containsKey(key)){
            Cacheable c = cache.get(key);
            lru.remove(cache.get(key));
            lru.add(0, c);//reorder to most recent
            return c;
        }
        return null;
    }

    public int lruCount(){
        return lru.size();
    }

    //evict least recently used, bottom of cache
    private void evict(){
        if(lru.size() >= size && (size >0)){
            lru.remove(lru.size() - 1);
        }
    }
}
