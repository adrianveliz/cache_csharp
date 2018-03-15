using System;
using System.Threading;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;

namespace Test
{
	public class MyCache : Cache
	{
		// Dictionary to contain the cache.
		Dictionary<String, MyCacheableObject> _cache;
		ArrayList _lru;
		static int _size = 50;

		public MyCache(int size)
		{
			_size = size;
			_lru = new ArrayList();
			_cache = new Dictionary<String, MyCacheableObject>();
		}
		
		public void printCache()
		{
			foreach(MyCacheableObject val in _lru){
				Console.WriteLine(val);
			}
		}
		
		public void addEntry(String key, MyCacheableObject value)
		{
			try
			{
				if(_lru.Count >= _size){
					evict();
				}
				value.setCache(this);
				_cache.Add(key, value);
				_lru.Insert(0, value);
			}
			catch (ArgumentException)
			{
				_lru.Remove(value);//does nothing if does not exist, O(n)
			//	Console.WriteLine("ArgumentException in addEntry");
			}
		}

		public MyCacheableObject getEntry(String key)
		{
			if (_cache.ContainsKey(key))
			{
				MyCacheableObject mco = _cache[key] as MyCacheableObject;
				_lru.Remove(_cache[key]);//remove by key given
				_lru.Insert(0, mco);//reorder, this is now the most recently used
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
			_lru.Insert(0, mco);
			evict();
		}

		//evict the least recently used, bottom of cache
		public void evict()
		{
			if (_lru.Count >= _size && _size > 0)
			{
				try
				{
					_lru.RemoveAt(_size - 1);
				}
				#pragma warning disable 0168
				catch (System.ArgumentOutOfRangeException ex)
				#pragma warning restore 0168
				{
					// This should not happen but DMCS complains that this
					// exception is not caught. The complier will then 
					// complain that the variable is never used.
					Console.WriteLine("Hmmm.... You should not be seeing this.");
				}
			}
		}

		//TODO make an evict with an object

		public int LRUCount()
		{
			return _lru.Count;
		}
	}
}	
