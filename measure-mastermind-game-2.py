#!/bin/python

data = {
    (2, 4 ): 537,
    (2, 5 ): 464,
    (2, 6 ): 582,
    (2, 7 ): 481,
    (2, 8 ): 474,
    (2, 9 ): 612,
    (2, 10): 885,
    (2, 11): 522,
    (2, 12): 536,
    (3, 4 ): 808,
    (3, 5 ): 946,
    (3, 6 ): 1191,
    (3, 7 ): 976,
    (3, 8 ): 979,
    (3, 9 ): 786,
    (3, 10): 1025,
    (3, 11): 829,
    (3, 12): 802,
    (4, 5 ): 2182,
    (4, 6 ): 1824,
    (4, 7 ): 1795,
    (4, 8 ): 1641,
    (4, 9 ): 1442,
    (4, 10): 1698,
    (4, 11): 1813,
    (4, 12): 1644,
    (5, 6 ): 2590,
    (5, 7 ): 2551,
    (5, 8 ): 2681,
    (5, 9 ): 2554,
    (5, 10): 2509,
    (5, 11): 2513,
    (5, 12): 2516,
    (6, 7 ): 3147,
    (6, 8 ): 3893,
    (6, 9 ): 3082,
    (6, 10): 2977,
    (6, 11): 3264,
    (6, 12): 3075,
    (7, 8 ): 4730,
    (7, 9 ): 4610,
    (7, 10): 4858,
    (7, 11): 4714,
    (7, 12): 4690,
    (8, 9 ): 7171,
    (8, 10): 7846,
    (8, 11): 7762,
    (8, 12): 7447
}

import numpy
import matplotlib.pyplot as plt

# Extract input variables and measured variable from the data dictionary
input_vars = list(data.keys())
measured_variable = numpy.log10(list(data.values()))

# Separate the input variables into two lists
x = [x[0] for x in input_vars]
y = [x[1] for x in input_vars]

# Create a 2D scatter plot
plt.figure(figsize=(10, 6))  # Optional: Set the figure size
plt.scatter(x, y, c=measured_variable, cmap='viridis', marker='o')

# Set labels for the axes
plt.xlabel('Length of code')
plt.ylabel('Allowed colors')
plt.title('Measured Variable vs. Input Variables')

# Add a color bar
cbar = plt.colorbar()
cbar.set_label('Passed time')

# Show the plot
plt.grid(True)  # Optional: Add a grid to the plot
plt.show()
