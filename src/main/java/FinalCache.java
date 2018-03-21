import java.util.HashMap;
import java.util.Map;

public class FinalCache implements Cache{
    private Map<String, Cacheable> cache;
    private LruCache doomed;
    private int size;//of lru

    public FinalCache(int size){
        this.size = size;
        cache = new HashMap<>();
        doomed = new LruCache(size);
    }

    @Override
    public void add(String key, Cacheable val) {
        val.setCache(this);
        cache.put(key, val);//adding duplicates doesnt affect maps
    }

    @Override
    public Cacheable get(String key) {
        if(cache.containsKey(key)){
            return cache.get(key);
        }
        if(doomed.get(key) != null){//doomed contains key
            Cacheable c = doomed.get(key);
            //move from doomed to regular set
            doomed.remove(key);
            cache.put(key, c);
            return c;
        }
        return null;
    }

    //to be called when entry is doomed
    public void update(String key){
        if(cache.containsKey(key)){
            //move from cache to doomed set
            Cacheable c = cache.get(key);
            cache.remove(key);
            doomed.add(key, c);
        }
    }
}
