import sys
import pprint
from collections import defaultdict
from datetime import datetime

#pretty prints dictionary
def prettyPrinter(dictionary):
	pprint.isrecursive(dictionary)
	pp = pprint.PrettyPrinter(indent=3, width=1)
	for x in dictionary:
		pp.pprint(x)
		for y in x:
			pp.pprint(dictionary[x])

def includeLine(log, notAccessLogs):
	for x in notAccessLogs:
		if x in log:
			return True
	return False

def excludeLine(log, notAccessLogs):
	for x in notAccessLogs:
		if x in log:
			return False 
	return True

accesses = ['CacheFileOutputStream::Write', 'CacheFileChunk::Write', 'CacheFileIOManager::Write()']

removalDict = defaultdict(str)
newEntryDict = defaultdict(str)
accessDict = defaultdict(list) 
#only need times
timesDict = defaultdict(str)
doomDict = defaultdict(str)

for line in sys.stdin:
	#assuming some lines dont 
	#contain this string
	if "dooming entry" in line:
		doomDict[line[line.index("dooming entry") + 14 : line.index("dooming entry") + 27 :]] = line
		timesDict[datetime.strptime(line[0 : line.index(" UTC") :], '%Y-%m-%d %H:%M:%S.%f')] = line
	#no method is called.. this is how its logged
	if "new entry" in line:
	#	newEntryDict[line[line.index("new entry") + 9 : line.index("new entry") + 22:]] = line
		timesDict[datetime.strptime(line[0 : line.index(" UTC") :], '%Y-%m-%d %H:%M:%S.%f')] = line
	if "this=" in line:
		hexaddress = line[line.index("this=") + 5 : line.index("this=") + 18:]
		if(includeLine(line, accesses)):
		#	accessDict[hexaddress].append(line)
			timesDict[datetime.strptime(line[0 : line.index(" UTC") :], '%Y-%m-%d %H:%M:%S.%f')] = line
		#if(excludeLine(line, accesses)):
			#dictionary[hexaddress].append(line)
	if "RemoveExactEntry" in line:
		removalDict[hexaddress] = line
		timesDict[datetime.strptime(line[0 : line.index(" UTC") :], '%Y-%m-%d %H:%M:%S.%f')] = line
	
#prettyPrinter(dictionary)
#for x,y in accessDict.iteritems():
	#print x, y
for x,y in timesDict.iteritems():
	print x, y
#for x,y in doomDict.iteritems():
#	print x,y
