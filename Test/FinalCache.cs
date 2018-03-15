using System;
using System.Threading;
using System.Collections;
using System.Collections.Generic;

namespace Test
{
    public class FinalCache : Cache
    {
        // Dictionary to contain the cache.
        static Dictionary<string, WeakReference> _cache;
        static ArrayList _lru;
        static int _size = 50;

        public FinalCache(int size)
        {
            _cache = new Dictionary<string, WeakReference>();
            _lru = new ArrayList();
            _size = size;
        }

        public void addEntry(string key, MyCacheableObject value)
        {
            try
            {
                value.setCache(this);
                // ressurection tracking enabled, allow calls to get during EWR
                //GC.SuppressFinalize(this);
                //GC.ReRegisterForFinalize(this);
                _cache.Add(key, new WeakReference(value, true));//ressurection tracking enabled 
            }
            catch (ArgumentException)
            {
                _lru.Remove(value);//does nothing if does not exist, O(n)
            }
        }

        public MyCacheableObject getEntry(string key)
        {
            try
            {
                if (_cache.ContainsKey(key))
                {
                    MyCacheableObject mco = _cache[key].Target as MyCacheableObject;
                    GC.SuppressFinalize(mco);
                    GC.ReRegisterForFinalize(mco);
                    _lru.Remove(mco);
                    mco.setGet(true);
                    return mco;
                }
            }
            catch (System.ArgumentNullException e)
            {
                //problem is GC.SuppressFinalize(mco);, not accepting null objects
                return null;
            }

            return null;
        }

        public void LRU(MyCacheableObject mco)
        {
            GC.SuppressFinalize(mco);
            _lru.Insert(0, mco);
            evict();
        }

        public void evict()
        {
            if (_lru.Count >= _size && _size > 0)
            {
                try
                {
                    _lru.RemoveAt(_size-1);
                }
#pragma warning disable 0168
                catch (System.ArgumentOutOfRangeException ex)
#pragma warning restore 0168
                {
                    //This should not happen but DMCS complains that this
                    // exception is not caught. The complier will then 
                    // complain that the variable is never used.
                    Console.WriteLine("Hmmm.... You should not be seeing this.");
                }
            }
        }

        public int LRUCount()
        {
            return _lru.Count;
        }
    }
}