using System;
using System.Threading;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;

// @author Aleksandr S Diamond
// program that tracks simple lru 
// from stdin, which is supposed 
// to be firefox logs


namespace ConsoleApplication
{
	public class Program
	{
		//what is considered an acccess 
		static string[] accesses = {
			//"CacheFileOutputStream::Write", 
			//"CacheFileChunk::Write", 
			//"CacheFileIOManager::Write()",
			//"CacheFile::SetElement()",
			//added these after we started switch to URL based keys
			"CacheFileMetadata::GetElement()",
			"CacheFileMetadata::SetElement()"
			};
		//FIXME
		public static bool isAccess(string log){
			foreach(string tmp in accesses){
				if(log.Contains(tmp)){
					return true;
				}
			}
			return false;
		}

		public static bool isDoom(string log){
			return log.Contains("dooming entry");
		}

		/*
		* works pretty well except for some exceptional circumstances.
		* Example:
		* ID = 2 20:13:40.479597 UTC - [Main Thread]: D/cache2 CacheStorageService::AddStorageEntry [entryKey=~predictor-origin
		* LOG = 2018-03-12 20:13:40.479597 UTC - [Main Thread]: D/cache2 CacheStorageService::AddStorageEntry [entryKey=~predictor-origin,:http://tags.bluekai.com/, contextKey=]
		*/
		public static bool isNewEntry(string log){
			return log.Contains("CacheStorageService::AddStorageEntry");	
		}

		public static bool isRemoved(string log){
			return log.Contains("RemoveExactEntry");
		}


		public static MyCacheableObject mco = null;
		public static void Main(string[] args)
		{
			int size = Int32.MaxValue;
			int hits = 0;
			int accesses = 0;
			MyCache cache = new MyCache(size);
			string log = null;
			while((log = Console.ReadLine()) != null){
				//need to get id of entries when they are created
				//so i can 'get' them when accessed
				if(isNewEntry(log)){
					//testing getting the id of this new entry log
					int start = log.IndexOf("entryKey=:") + 10;
					int length = log.IndexOf(",") - start;
					string id = log.Substring(start, length);//grabs the url id of the entry.
					//add to cache
					MyCacheableObject mco = new MyCacheableObject(log);
					cache.addEntry(id, mco);//hexid is key, log in mco is val
				} else if(isAccess(log)){
					accesses++;
					int start = log.IndexOf("this=")+5;
					int length = 13;
					try{
						string hexid = log.Substring(start, length);

						if(cache.getEntry(hexid.Trim()) != null){
							hits++;//not in cache
						}
					} catch(ArgumentOutOfRangeException){
						//Console.WriteLine("probably didnt have this= in it...");
					}
				}
			}
			double hitrate = (double)hits / (double)accesses;
			Console.WriteLine("Accesses " + accesses);
			Console.WriteLine("Hits " + hits);
		}
	}

	public class MyCache
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
				//Console.WriteLine("Finalized: " + this._value);
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
