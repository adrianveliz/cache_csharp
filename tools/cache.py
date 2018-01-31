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

    def addEntry(key, value):
        if(len(lru) >= size):
            evict()

    def evict():
        print ("Called evict")



if __name__ == "__main__" : main()
