using System;
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
		Dictionary  refs = null;
		
		MyCache()
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
