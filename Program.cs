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
        static ArrayList _lru = new ArrayList();

		
		public MyCache(int size)
        {

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
