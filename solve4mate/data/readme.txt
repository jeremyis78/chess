So far this directory contains the correct output for various complicated tasks.


a8 already occupied bug:
reproduce it with:
position fen r3k2r/Pppp1ppp/1b3nbN/nP6/BBPPP3/q4N2/Pp4PP/R2Q1RK1 b kq d3 0 1 moves a8a7
do b4a3
#now a phantom rook appears on a8



Ra7-a8 1   : 4k2r/rppp1Npp/1b3nb1/nP6/BBPPP3/q4N2/Pp4PP/R2Q1RK1 b k - 0 2
Ra7-a8after: r3k2r/1ppp1Npp/1b3nb1/nP6/BBPPP3/q4N2/Pp4PP/R2Q1RK1 w k - 1 3
Ra7-a8 undo: r3k2r/1ppp1Npp/1b3nb1/nP6/BBPPP3/q4N2/Pp4PP/R2Q1RK1 w k - 1 3
Ra7-a8 2   : 4k2r/rppp1ppp/1b3nbN/nP6/B1PPP3/B4N2/Pp4PP/R2Q1RK1 b k - 0 2
Ra7-a8after: r3k2r/1ppp1ppp/1b3nbN/nP6/B1PPP3/B4N2/Pp4PP/R2Q1RK1 w k - 1 3
Ra7-a8 undo: r3k2r/1ppp1ppp/1b3nbN/nP6/B1PPP3/B4N2/Pp4PP/R2Q1RK1 w k - 1 3


before Bb4xa3: 4k2r/rppp1ppp/1b3nbN/nP6/BBPPP3/q4N2/Pp4PP/R2Q1RK1 w k - 0 2
after  Bb4xa3: r3k2r/rppp1ppp/1b3nbN/nP6/BBPPP3/q4N2/Pp4PP/R2Q1RK1 w k - 0 2

before Bb4xa3: 4k2r /rppp1ppp/1b3nbN/nP6/BBPPP3/q4N2/Pp4PP/R2Q1RK1 w k - 0 2
after  Bb4xa3: r3k2r/rppp1ppp/1b3nbN/nP6/BBPPP3/q4N2/Pp4PP/R2Q1RK1 w k - 0 2

