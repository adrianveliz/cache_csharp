using System;
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
            Console.WriteLine("Hello World!");
        }
    }

    class MyCache
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
        
        public void addEntry(string key, string value)
        {
			try
			{
				_cache.Add(key, new WeakReference(value));
			}
			catch(ArgumentException)
			{
				_lru.Remove(value);//does nothing if does not exist, O(n)
			}
		}
	}

    class MyCacheableObject
    {
        MyCache cache = null;

        MyCacheableObject(MyCache cache)
        {
            this.cache = cache;
        }

    }
}
