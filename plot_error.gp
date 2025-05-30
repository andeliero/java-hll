# Generate with the following command
# gnuplot plot_error.gp
# Set the data file separator to comma
set datafile separator comma

# Set the output terminal to SVG and specify the output filename
set terminal svg enhanced font "Arial,12"
set output "error_plot.svg"

# Set the title of the plot
# set title "Relative Percentage Error"
set xlabel "Cardinality"
set ylabel "Relative Percentage Error"

# set yrange [-5:100]

# Use the column headers for the legend entries
set key autotitle columnheader

# Add a grid for better readability
set grid

# Set the global point size (e.g., 0.8 for smaller points)
set pointsize 0.1

# Plot the data using 'with points'
# 'pt 7' specifies point type 7 (a solid circle by default in many terminals)
plot "with_linear_counting.csv" using 1:3 with points pt 1

set output