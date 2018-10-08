import matplotlib.pyplot as plt
import numpy as np
import sys


data = []
for line in sys.stdin:
	data.append(int(line.split(" ")[1]))

plt.hist(data)

plt.title("Entry Lifetime")
plt.xlabel("Lifetime in Accesses")
plt.ylabel("Frequency")

plt.show()
