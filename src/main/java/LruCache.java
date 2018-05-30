import java.util.*;

public class LruCache<K, V> extends LinkedHashMap<K, V> {
    private int size;
    private K recentEviction;
    private int amountDoomed;

    /**
     * An interface for being alerted when an entry is evicted from this cache
     * passes in the entry evicted
     */
    interface OnEntryEvictedListener{
        void onEntryEvicted(String key, String val);
    }

    //a list of listeners to be notified when an entry is evicted
    private List<OnEntryEvictedListener> listeners;

    public LruCache(int size) {
        //size
        //load factor
        //set access order to be true
        super(size, .75f, true);
        this.size = size;
        recentEviction = null;

        //size 10 why not?
        listeners = new ArrayList<>(10);
    }

    public void addOnEntryEvictedListener(OnEntryEvictedListener listener){
        listeners.add(listener);
    }

    //called for maintenance
    //specifies to remove the least recently used when
    //the size has grown to be equal to the size
    //the client has stipulated
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (size() > size) {
            this.amountDoomed++;
            recentEviction = eldest.getKey();
            //notify listeners
            listeners.forEach(listener -> {
                listener.onEntryEvicted(eldest.getKey().toString(), eldest.getValue().toString());
            });
            return true;
        }
        return false;
    }

    public K getRecentEviction() {
        return recentEviction;
    }

    public void resetAmountDoomed(){
        amountDoomed = 0;
    }

    public int getAmountDoomed(){
        return amountDoomed;
    }
}


