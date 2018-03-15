using System;
using System.Threading;
using System.Collections;
using System.Collections.Generic;
/**
* Set of regular
* When doomed, move to doomed set
* When removed, remove it
* If accessed something in doomed set move to regular
*/

namespace Test
{
    public class FinalCache : Cache
    {
        // Dictionary to contain the cache.
        static Dictionary<string, MyCacheableObject> _cache;
	static Dictionary<string, MyCacheableObject> _doomed;
        static int _size = 50;

        public FinalCache(int size)
        {
            _cache = new Dictionary<string, MyCacheableObject>();
            _doomed = new Dictionary<string, MyCacheableObject>();
            _size = size;
        }

        public void addEntry(string key, MyCacheableObject value)
        {
            value.setCache(this);
	    //wont complain about adding entry twice
	    _cache[key] = value;
        }

        public MyCacheableObject getEntry(string key)
        {
                if (_cache.ContainsKey(key))
                {
                    MyCacheableObject mco = _cache[key];
                    return mco;
                }
		else if(_doomed.ContainsKey(key))
		{
                    MyCacheableObject mco = _doomed[key];
	 	    //remove from doomed and add to regular set
		    _doomed.Remove(key);
		    _cache.Add(key, mco);
                    return mco;
		}
            return null;
        }

	//to be called when an entry is doomed
	public void update(string key)
	{
	//if an entry is doomed remove it from cache and
	//add it to doomed set
		if(_cache.ContainsKey(key))
		{
			MyCacheableObject mco = _cache[key];
			_cache.Remove(key);
			_doomed[key] = mco;
		}
	}
    }
}
