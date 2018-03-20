public interface Cache {
    void add(String key, Cacheable val);
    Cacheable get(String key);
}
