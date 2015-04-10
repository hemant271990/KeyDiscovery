
reset
set term png truecolor
set output 'out.png'
set title 'No. of questions analysis'
#set zlabel 'Questions'
set ylabel 'No. of uniques per column'
set xlabel 'key size'
set dgrid3d 60,30
set view 70,80
set hidden3d
splot 'result.dat' u 1:2:3 with lines title 'No. of questions'
