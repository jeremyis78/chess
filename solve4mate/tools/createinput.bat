@echo off
::
:: Creates a perft (performance test) file for an engine given an input file and a depth.
::
:: Loops over each line (a fen string) in the test file for each depth up
:: to the given argument depth.  
::
:: For example with 2 fens in the test file
:: calling this with arguments "fens.txt 3" would print the following:
::   position fen <fen1>
::   perft 1
::   perft 2
::   perft 3
::   position fen <fen2>
::   perft 1
::   perft 2
::   perft 3
::
:: Useful for creating a base script for the engine to run perft tests a set of positions.
::
set "file=%1"
set "depth=%2"
for /F "usebackq delims=" %%f in ("%file%") do (
  echo position fen %%f
  for /L %%d in (1,1,%depth%) do (
    echo perft %%d
  )
)
echo quit