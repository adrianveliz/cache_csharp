from datetime import datetime as dt

invaliddates = 0
dates = []
with open('../resources/fire_logs/16klog.txt') as fp:
    for line in fp:
        # no need to include the UTC thing
        try:
            date = dt.strptime(line[:26], '%Y-%m-%d %H:%M:%S.%f')
            dates.append(date)
        except:
            # some lines only have a single element?
            # they just report offsets and some other
            # stuff, no idea why they don't have a date
            # print(line)
            invaliddates += 1

print(invaliddates)
date_ordinals = [d.timestamp() for d in dates]
print(type(date_ordinals))
print(type(date_ordinals[0]))

if sorted(date_ordinals) == date_ordinals:
    print("In sorted order...")
else:
    print("The events here did not occur the way you think they did")

# if len(date_ordinals) == 1:
#     print("uniq")
# elif (max(date_ordinals) - min(date_ordinals)) == len(date_ordinals) - 1:
#     print("consecutive")
# else:
#     print("not consecutive")

