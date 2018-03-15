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

	}
}
