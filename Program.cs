using System;
using System.Threading;
using System.Collections;
using System.Collections.Generic;
//using System.Collections.NonGeneric;

// Cache that uses finalize to revive objects.
// @author Adrian Veliz
// 


namespace ConsoleApplication
{
	public class Program
	{
		public static MyCacheableObject mco = null;
		public static void Main(string[] args)
		{

			#pragma warning disable 219
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

			sleepAndPrintCount(cache);


			GC.Collect();//All but one entry will be finalized
			MyCacheableObject mco2 = cache.getEntry("3");//Example of getting object before finalize
			sleepAndPrintCount(cache);
			Console.WriteLine("One entry reachable from root set, another entry gotten after gc before finalize so it is not tracked.");

			mco2 = null;
			GC.Collect();
			sleepAndPrintCount(cache);
			Console.WriteLine("One entry reachable from root set, second chance added to lru tracking");

			
			mco = null;
			GC.Collect();
			sleepAndPrintCount(cache);
			Console.WriteLine("All entries should now be lru tracked");
			#pragma warning restore 219
		}

		public static void sleepAndPrintCount(MyCache cache)
		{
			System.Threading.Thread.Sleep(1000);//Make time for finalize to run
			Console.WriteLine("Count of elements: " + cache.LRUCount());
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
			if (_cache.ContainsKey(key))
			{
				MyCacheableObject mco = _cache[key].Target as MyCacheableObject;
				GC.SuppressFinalize(mco);
				GC.ReRegisterForFinalize(mco);
				_lru.Remove(mco);
				mco.setGet(true);
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
