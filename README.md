# cache_csharp
Cache in c#

Linux:

Required: mono sdk installed

dmcs Program.cs

./Program.exe

Windows:

Required: Visual Studio Community Edition 2015

csc Program.cs

.\Program.exe


.NET Core

For some reason this will not compile with .NET Core even though it works fine with Mono and Visual Studio.
It complains 


..... Program.cs(37,16): error CS0246: The type or namespace name 'ArrayList' could not be found (are you missing a using directive or an assembly reference?)


Compilation failed.

