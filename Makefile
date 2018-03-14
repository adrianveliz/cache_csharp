all: Program

Program:
	dotnet build 

test:
	cat fire_logs/cache-log.txt.0 | dotnet run Program.cs

run:
	cat fire_logs/* | dotnet run Program.cs
