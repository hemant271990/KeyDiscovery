
reset
set term png truecolor
set output 'out.png'
set yrange [0:600]
set title 'No. of questions analysis'
set ylabel 'No. of questions'
set xlabel 'No. of uniques per column'
set grid
set boxwidth 0.85 relative
set style fill transparent solid 0.5 noborder
plot 'ab.dat' title 'AB' with lines ls 1, 'abc.dat' title 'ABC' with lines ls 2 

