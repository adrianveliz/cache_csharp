import matplotlib.pyplot as plt
import numpy as np
import sys
import os

resultsFiles = os.listdir("results")
alldata = []
for resultFile in resultsFiles:
    with open("results/" + resultFile) as rawFile:
        data = []
        for line in rawFile:
            if "intervalDooms" in line:
                data.append(int(line.split("= ")[1]))

    alldata.insert(0, data)  # os.listdir() lists files in opposite order than you'd thing

plt.boxplot(alldata)

plt.xlabel('Interval Size')
plt.ylabel('Dooms per interval')

axes = plt.axes()
axes.set_xticklabels(['50', '250', '500'])

plt.show()
