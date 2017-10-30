using System;
using System.Threading;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;
//using System.Collections.NonGeneric;

// Simple LRU cache
// @author Aleksandr S Diamond
// 


namespace ConsoleApplication
{
	public class Program
	{
		public static MyCacheableObject mco = null;
		public static void Main(string[] args)
		{

			//#pragma warning disable 219
			int size = 20;
			MyCache cache = new MyCache(size);
			mco = new MyCacheableObject("0");
			cache.addEntry("0", mco);

			for (int i = 1; i < 5; i++)
			{
				MyCacheableObject temp = new MyCacheableObject(i.ToString());
				cache.addEntry(i.ToString(), temp);
				temp = null;//csc and dotnet require this in order to work correctly, dmcs does not
			}
			//printCache(cache);
			MyCacheableObject mco2 = cache.getEntry("3");//Example of getting object before finalize
			Console.WriteLine("after a get");
			//printCache(cache);
		}

		/*
		public static void sleepAndPrintCount(MyCache cache)
		{
			System.Threading.Thread.Sleep(1000);//Make time for finalize to run
			Console.WriteLine("Count of elements: " + cache.LRUCount());
		}
		*/ 

		/* To display dictionary contents */
		static void DisplayContents(
        		ICollection keyCollection, ICollection valueCollection, int dictionarySize)
    		{
        		String[] myKeys = new String[dictionarySize];
        		String[] myValues = new String[dictionarySize];
        		keyCollection.CopyTo(myKeys, 0);
        		valueCollection.CopyTo(myValues, 0);

        		// Displays the contents of the OrderedDictionary
        		Console.WriteLine("   INDEX KEY                       VALUE");
        		for (int i = 0; i < dictionarySize; i++)
        		{
            			Console.WriteLine("   {0,-5} {1,-25} {2}",
                			i, myKeys[i], myValues[i]);
        		}
        		Console.WriteLine();
    		}
		
		public static void printCache(MyCache cache)
		{
			DisplayContents(cache.getLruCache().Keys, cache.getLruCache().Values, cache.getLruCache().Count);
		}

	}

	public class MyCache
	{
		// Dictionary to contain the cache.
		//static Dictionary<string, MyCacheableObject> _cache;
		//static ArrayList _lru;

		//Ordered Dictionary for the cache
		OrderedDictionary _lrucache;
		static int _size = 50;

		public MyCache(int size)
		{
			_lrucache = new OrderedDictionary();
			_size = size;
		}

		public OrderedDictionary getLruCache() 
		{
			return _lrucache;
		}
		
		public void addEntry(string key, MyCacheableObject value)
		{
			try
			{
				value.setCache(this);
				_lrucache.Add(key, value);
			}
			catch (ArgumentException)
			{
				//lru.Remove(value);//does nothing if does not exist, O(n)
				Console.WriteLine("ArgumentException in addEntry");
			}
		}

		public MyCacheableObject getEntry(string key)
		{
			if (_lrucache.Contains(key))
			{
				//MyCacheableObject mco = _cache[key].Target as MyCacheableObject;
				MyCacheableObject mco = _lrucache[key] as MyCacheableObject;
				_lrucache.Remove(key);//remove by key given
				_lrucache.Add(0, mco);//reorder, this is now the most recently used
				return mco;
			}
			return null;
		}

		/*TODO
		* updates lru
		* update on adds, insert at top, make
		* just mainline in add method
		*/

		public void LRU(MyCacheableObject mco)
		{
			//GC.SuppressFinalize(mco);
			_lrucache.Add(0, mco);
			evict();
		}

		//evict the least recently used, bottom of cache
		public void evict()
		{
			if (_lrucache.Count >= _size && _size > 0)
			{
				try
				{
					_lrucache.RemoveAt(_size - 1);
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

		//TODO make an evict with an object

		public int LRUCount()
		{
			return _lrucache.Count;
		}
	}

	public class MyCacheableObject
	{
		//WeakReference _cache = null;
		MyCache _cache;
		string _value;
		bool _get = false;

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

		public bool gotten()
		{
			return this._get;
		}

		public void setGet(bool gotten)
		{
			this._get = gotten;
		}

		~MyCacheableObject()
		{
			//MyCache cache = _cache.Target as MyCache;
			if (_cache != null)
			{
				Console.WriteLine("Finalized: " + this._value);
				if(this.gotten())//this object has been gotten, second chance before adding to LRU
				{
					this.setGet(false);
				}
				else
				{
					_cache.LRU(this);
				}
				//GC.SuppressFinalize(this);//I know this looks weird, trust me. Don't delete
				GC.ReRegisterForFinalize(this);
			}
			else
			{
				//This call to suppress is not necessary since it was not reregistered
				GC.SuppressFinalize(this);
				//Console.WriteLine("Cache does not exist. Do not revive.");
			}
		}
	}
}
