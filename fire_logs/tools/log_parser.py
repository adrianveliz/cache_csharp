#track creation of entries "new entry"
#xxxtrack amount of time its accessedxxx
#track destruction of entry "entry="" removed"
#track time from cache entry created to destroyed

import sys
from datetime import datetime
from collections import defaultdict

newEntryList = []
removedEntriesList = []
dateTimes = []
entryDict = defaultdict(list)

for line in sys.stdin:
	#print line
	if "new entry" in line:
		newEntryList.append(line)

	try:
		if "removed" in line[line.index("entry=") + 13:]:
			removedEntriesList.append(line)
	#from entry= not being in the string.
	except ValueError:
		pass
	try:
		#convert this spliced string into a datetime
		#object and use it to identify entries that 
		#have been created and deleted
		#print line[0:line.index(" UTC"):]
		dateTimes.append(datetime.strptime(line[0:line.index(" UTC"):], '%Y-%m-%d %H:%M:%S.%f'))
	except ValueError:
		pass

for x in newEntryList:
	print x
#for x in removedEntriesList:
#	print x
#for x in dateTimes:
#	print x
#print dateTimes

