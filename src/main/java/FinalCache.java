import java.util.HashMap;
import java.util.Map;

public class FinalCache implements Cache{
    private Map<String, Cacheable> cache;
    private Map<String, Cacheable> doomed;
    private int size;

    public FinalCache(int size){
        this.size = size;
        cache = new HashMap<>();
        doomed = new HashMap<>();
    }

    @Override
    public void add(String key, Cacheable val) {
        val.setCache(this);
        cache.put(key, val);//adding twice shouldnt affect hashmap
    }

    @Override
    public Cacheable get(String key) {
        if(cache.containsKey(key)){
            Cacheable c = cache.get(key);
            return c;
        }
        else if(doomed.containsKey(key)){
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
            doomed.put(key, c);
        }
    }
}
