using System;
using System.Threading;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;

// @author Aleksandr S Diamond
// program that tracks simple lru 
// from stdin, which is supposed 
// to be firefox logs


namespace Test
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
		//May only work in certain 'gets'
		//Example, in the form:
		//2018-03-12 20:13:34.674091 UTC - [Main Thread]: D/cache2 CacheFileMetadata::GetElement() - Key not found [this=7f7148731120, key=predictor::http://www.pages02.net/]
		//After the key=predictor:: looks to be a candidate for the key
		//Not very many logs are in this form.
		public static bool isAccess(string log){
			foreach(string tmp in accesses){
				if(log.Contains(tmp)){
					return true;
				}
			}
			return false;
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

		public static bool hasKey(string log){
			return log.Contains("key=predictor::http"); 
		}

		/*
		* For FinalCache, returns true for logs in the following format:
		2018-03-12 20:13:40.467981 UTC - [Main Thread]: D/cache2   dooming entry 7f714d24af00 for :https://stags.bluekai.com/site/13583?id=d9d70302-2631-11e8-a359-0242ac110005 because of OPEN_TRUNCATE
		*/
		public static bool isDoom(string log){
			return log.Contains("dooming entry");
		}

		public static void lruTest()
		{
			int size = 50;
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
				} else if(isAccess(log) && hasKey(log)){
					//Console.WriteLine("LOG:");
					//Console.WriteLine(log);
					accesses++;
					int start = log.IndexOf("key=predictor::") + 15;
					int length = log.Length - start - 1;//no need for the ']'
					try{
						string id = log.Substring(start, length);
						//Console.WriteLine("ID:");
						//Console.WriteLine(id);
						//Console.WriteLine("\n");
						if(cache.getEntry(id.Trim()) != null){
							hits++;
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

		public static void Main(string[] args)
		{
			int size = 50;
			int hits = 0;
			int accesses = 0;
			FinalCache cache = new FinalCache(size);
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
					cache.addEntry(id, mco);//id is key, log in mco is val
				} else if(isAccess(log) && hasKey(log)){
					accesses++;
					int start = log.IndexOf("key=predictor::") + 15;
					int length = log.Length - start - 1;//no need for the ']'
					try{
						string id = log.Substring(start, length);
						//Console.WriteLine("ID:");
						//Console.WriteLine(id);
						//Console.WriteLine("\n");
						if(cache.getEntry(id.Trim()) != null){
							hits++;
						}
					} catch(ArgumentOutOfRangeException){
						//Console.WriteLine("probably didnt have this= in it...");
					}
				} else if(isDoom(log)){
		/*2018-03-12 20:13:40.467981 UTC - [Main Thread]: D/cache2   dooming entry 7f714d24af00 for :https://stags.bluekai.com/site/13583?id=d9d70302-2631-11e8-a359-0242ac110005 because of OPEN_TRUNCATE
*/
					try{
						int start = log.IndexOf("for") + 5;//chop of ':'
						int length = log.IndexOf("because") - start; 
						string id = log.Substring(start, length).Trim();
						cache.update(id);
					}catch(ArgumentOutOfRangeException){}
				}
			}

			double hitrate = (double)hits / (double)accesses;
			Console.WriteLine("Accesses " + accesses);
			Console.WriteLine("Hits " + hits);
		}
	}


}
