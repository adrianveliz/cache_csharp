import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IntervalFinalCache implements Cache{
    private FinalCache cache;
    private Map<String, Cacheable> interval;
    private List<String> intervalLru;
    private int size, intervalSize;
    private static final int DEFAULT_SIZE = 100, DEFAULT_INTERVAL_SIZE = 10;

    public IntervalFinalCache(){
        this(DEFAULT_SIZE, DEFAULT_INTERVAL_SIZE);
    }

    public IntervalFinalCache(int size, int intervalSize){
        this.size = size;
        cache = new FinalCache(size);
        this.intervalSize = intervalSize;
        interval = new HashMap<>();
        intervalLru = new LinkedList<>();
    }

    @Override
    public void add(String key, Cacheable val) {
        cache.add(key, val);
    }

    @Override
    public Cacheable get(String key) {
        //null if nothing
        return cache.get(key);
    }

    //meant to be called with items that are doomed
    public void addToInterval(String key, Cacheable val){
        //time to manage interval
        if(interval.size() >= intervalSize){
            //remove least recently added item to interval
            interval.remove(intervalLru.get(intervalLru.size() - 1));
            intervalLru.remove(intervalLru.size() - 1);
        }
        intervalLru.add(0, key);//add this to top
        interval.put(key, val);
    }

    //meant to be called when interval has elapsed
    //dumps entire interval into finalcache
    public void dumpInterval(){
        for (String key : interval.keySet()) {
            cache.add(key, interval.get(key));
        }
    }
}
