//interface for making caches
//makes it easier to test multiple caches.
using System;

namespace Test
{
    public interface Cache
    {
        void addEntry(string key, MyCacheableObject val);
        MyCacheableObject getEntry(string key);
        //this dependency may need to be generalized or otherwise fixed.
    }
}
