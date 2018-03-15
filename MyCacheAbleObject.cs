using System;
using System.Threading;
using System.Collections;
using System.Collections.Generic;
using System.Collections.Specialized;

namespace Test
{
	public class MyCacheableObject
	{
		//WeakReference _cache = null;
		Cache _cache;
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

		public void setCache(Cache cache)
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