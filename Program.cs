using System;
using System.Threading;
using System.Collections;
using System.Collections.Generic;

// Cache that uses finalize to revive objects.
// @author Adrian Veliz
// 


namespace ConsoleApplication
{
    public class Program
    {
        public static void Main(string[] args)
        {
            int size = 20;
            MyCache cache = new MyCache(size);
            MyCacheableObject mco = null;
            for (int i = 0; i < size + size / 2; i++)
            {
                mco = new MyCacheableObject(i + "");
                cache.addEntry(i.ToString(), mco);
            }

            Console.WriteLine("Count of elements in LRU before GC: " + cache.LRUCount());
            GC.Collect();//All but one entry will be finalized
            System.Threading.Thread.Sleep(2000);//Make time for finalize to run
            Console.WriteLine("Count of elements in LRU after GC: " + cache.LRUCount());
        }
    }

    public class MyCache
    {
        // Dictionary to contain the cache.
        static Dictionary<string, WeakReference> _cache;
        static ArrayList _lru;
        static int _size = 50;

        public MyCache(int size)
        {
            _cache = new Dictionary<string, WeakReference>();
            _lru = new ArrayList();
        }

        public void addEntry(string key, MyCacheableObject value)
        {
            try
            {
                value.setCache(this);
                _cache.Add(key, new WeakReference(value));
            }
            catch (ArgumentException)
            {
                _lru.Remove(value);//does nothing if does not exist, O(n)
            }
        }

        public MyCacheableObject getEntry(string key)
        {
            if (_cache.ContainsKey(key))
            {
                MyCacheableObject mco = _cache[key].Target as MyCacheableObject;
                GC.ReRegisterForFinalize(mco);
                _lru.Remove(mco);
                return mco;
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
            if (_lru.Count >= _size)
            {
                _lru.RemoveAt(_size);
            }
        }

        public int LRUCount()
        {
            return _lru.Count;
        }
    }

    public class MyCacheableObject
    {
        //WeakReference _cache = null;
        MyCache _cache;
        string _value;

        public MyCacheableObject(string value)
        {
            _value = value;
        }

        public override string ToString()
        {
            return _value;
        }

        public override bool Equals(Object obj)
        {
            MyCacheableObject mco = obj as MyCacheableObject;

            if (mco == null)
                return false;

            return _value.Equals(mco.ToString());
        }

        public override int GetHashCode()
        {
            return this.ToString().GetHashCode();
        }

        public void setCache(MyCache cache)
        {
            //_cache = new WeakReference(cache);
            _cache = cache;
        }

        ~MyCacheableObject()
        {
            //MyCache cache = _cache.Target as MyCache;
            if (_cache != null)
            {

                _cache.LRU(this);
                GC.ReRegisterForFinalize(this);
            }
            else
            {
                //This call to suppress is not necessary since it was not reregistered
                GC.SuppressFinalize(this);
                Console.WriteLine("Cache does not exist. Do not revive.");
            }
        }
    }
}
