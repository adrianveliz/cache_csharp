import java.util.*;

public class LruCache<K, V> extends LinkedHashMap<K, V> implements Cache{
    private int size;

    public LruCache(int size) {
        //size
        //load factor
        //set access order to be true
        super(size, .75f, true);
        this.size = size;
    }

    //called for maintenance
    //specifies to remove the least recently used when
    //the size has grown to be equal to the size
    //the client has stipulated
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > size;
    }
}


