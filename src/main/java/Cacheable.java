public class Cacheable {
    private Cache cache;
    public Object target;

    public Cacheable(){

    }

    public Cacheable(Object o){
        target = o;
    }

    @Override
    public String toString() {
        return target.toString();
    }

    public void setCache(Cache cache){
        this.cache = cache;
    }
}
