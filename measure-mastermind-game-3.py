#!/bin/python

data = {
    (2, 3, 5): 702,
    (2, 3, 6): 1128,
    (2, 3, 7): 1818,
    (2, 3, 8): 3508,
    (2, 3, 9): 7575,
    (2, 4, 5): 950,
    (2, 4, 6): 1586,
    (2, 4, 7): 2533,
    (2, 4, 8): 4186,
    (2, 4, 9): 7936,
    (2, 5, 5): 1098,
    (2, 5, 6): 1379,
    (2, 5, 7): 2413,
    (2, 5, 8): 3884,
    (2, 5, 9): 6055,
    (2, 6, 5): 734,
    (2, 6, 6): 1162,
    (2, 6, 7): 1868,
    (2, 6, 8): 3364,
    (2, 6, 9): 6751,
    (2, 7, 5): 943,
    (2, 7, 6): 1329,
    (2, 7, 7): 2297,
    (2, 7, 8): 4319,
    (2, 7, 9): 7961,
    (3, 4, 5): 1270,
    (3, 4, 6): 2039,
    (3, 4, 7): 3282,
    (3, 4, 8): 6269,
    (3, 4, 9): 11744,
    (3, 5, 5): 1566,
    (3, 5, 6): 2569,
    (3, 5, 7): 4005,
    (3, 5, 8): 6971,
    (3, 5, 9): 12107,
    (3, 6, 5): 1816,
    (3, 6, 6): 2752,
    (3, 6, 7): 4455,
    (3, 6, 8): 7052,
    (3, 6, 9): 12877,
    (3, 7, 5): 1275,
    (3, 7, 6): 1994,
    (3, 7, 7): 3093,
    (3, 7, 8): 5804,
    (3, 7, 9): 12081,
    (4, 5, 5): 3453,
    (4, 5, 6): 4910,
    (4, 5, 7): 7590,
    (4, 5, 8): 11627,
    (4, 5, 9): 19734,
    (4, 6, 5): 3358,
    (4, 6, 6): 4868,
    (4, 6, 7): 7422,
    (4, 6, 8): 11370,
    (4, 6, 9): 19336,
    (4, 7, 5): 3274,
    (4, 7, 6): 5033,
    (4, 7, 7): 7372,
    (4, 7, 8): 11498,
    (4, 7, 9): 19281,
    (5, 6, 5): 4729,
    (5, 6, 6): 7209,
    (5, 6, 7): 10574,
    (5, 6, 8): 16779,
    (5, 6, 9): 24674,
    (5, 7, 5): 3404,
    (5, 7, 6): 6250,
    (5, 7, 7): 8557,
    (5, 7, 8): 13348,
    (5, 7, 9): 23033,
    (6, 7, 5): 4562,
    (6, 7, 6): 6833,
    (6, 7, 7): 11496,
    (6, 7, 8): 17842,
    (6, 7, 9): 29034
}

import numpy
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

# Extract input variables and measured variable from the data dictionary
input_vars = list(data.keys())
passed_time = numpy.log10(list(data.values()))

# Separate the input variables into three lists
x = [x[0] for x in input_vars]
y = [x[1] for x in input_vars]
z = [x[2] for x in input_vars]

# Create a 3D scatter plot
fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')
ax.scatter(x, y, z, c=passed_time, cmap='viridis', marker='o')

# Set labels for the axes
ax.set_xlabel('Length of code')
ax.set_ylabel('Allowed colors')
ax.set_zlabel('Guesses')
ax.set_title('Measured Variable vs. Input Variables')

# Add a color bar
cbar = plt.colorbar(ax.scatter(x, y, z, c=passed_time, cmap='viridis'))
cbar.set_label('Passed time')

# Show the plot
plt.show()

