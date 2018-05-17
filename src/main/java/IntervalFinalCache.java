public class IntervalFinalCache<K, V> {
    private FinalCache<K, V> cache;
    private LruCache<K, V> interval;
    private int initialFinalCacheSize, intervalSize;

    public IntervalFinalCache(int intervalSize){
        this(10, intervalSize);
    }

    public IntervalFinalCache(int initialFinalCacheSize, int intervalSize){
        this.initialFinalCacheSize = initialFinalCacheSize;
        cache = new FinalCache<>(initialFinalCacheSize);

        this.intervalSize = intervalSize;
        interval = new LruCache<>(intervalSize);
    }

    public void add(K key, V val) {
        cache.add(key, val);
    }

    public V get(K key) {
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
        interval.forEach((K, V) -> cache.add(K, V));
        interval.clear();
    }

    public void remove(K key){
        cache.remove(key);
    }

    public int getDoomedSetAmountDoomed(){
        return cache.getDoomedSetAmountDoomed();
    }

    public void resetDoomedSetAmountDoomed(){
        cache.resetDoomedSetAmountDoomed();
    }
}
