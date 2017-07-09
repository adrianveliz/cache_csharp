# FinalCache
FinalCache uses LRU to evict entries. However it does not use access-history to compute LRU. Rather when objects become unused they become managed by LRU. Usage is determined by the garbage collector.

## .NET Core (Cross-Platform)


> dotnet build

> dotnet run


## Linux:

Required: Mono SDK 

> dmcs Program.cs

> ./Program.exe


## Windows:

Required: Visual Studio or Microsoft Build Tools/SDK

> csc Program.cs

> .\Program.exe
