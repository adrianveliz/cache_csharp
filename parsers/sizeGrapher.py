import matplotlib.pyplot as plt
import numpy as np
import sys

maxs = []
mins = []

for line in sys.stdin:
	if "Max" in line:
		maxs.append(int(line.split(": ")[1]))
	if "Min" in line:
		mins.append(int(line.split(": ")[1]))

x = np.linspace(0, 20, len(maxs))

plt.plot(x, maxs, '-b', label='Max')
plt.plot(x, mins, '-r', label='Min')
plt.show()
