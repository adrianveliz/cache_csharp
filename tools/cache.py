import sys

def main():
    print("Hello")
    x = MyCache(2)
    print (x.size)
    
class MyCacheableObject:
    cache = MyCache()
    


class MyCache:
    """A simple lru class for tracking"""
    size = 0
    lru  = []
    cache = {}

    def __init__(self, size):
        self.size = size

    def printcache(self):
        """
        should print each element of the cache and its val
        """
        for x,y in self.cache:
            print(x, y)
        print("Called printCache()")

    def addentry(self, key, value):
        """
        :param key: a string
        :param value:should be a MyCacheableObject
        """
        try:
            if len(self.cache) > self.size:
                self.evict()
            value.setcache(self)
            self.cache[key] = value
            self.lru.insert(0, value)
        except OSError: #will this type of exception be thrown?
            self.lru.remove(value) #hopefully does nothing if doesnt exist
            print("Error")

    def getentry(self, key):
        if self.cache.__contains__(key) :
            mco = self.cache[key]
            self.lru.remove(self.cache[key]) #remove by key given

    def evict(self):
        pass




class MyCacheableObject:

    def setCache(self):
        print ("called setcache()")

if __name__ == "__main__" : main()
