@echo off
:: Provides a wrapper around the engine to call a perft operation with the 
:: given depth and fen arguments.
::
:: usage: perft <type> <depth> "<fen>"
::
:: Examples:
::   perft.bat perft 2 "some fen here"
::   perft.bat divide 6 "some fen here"
::   perft.bat perft2 3 "some fen here"
echo position fen %~3 > input.txt
echo %1 %2 >> input.txt
type input.txt
java -jar C:\Users\jeremy\git\chess\solve4mate\build\engine.jar<input.txt 2> NUL
del input.txt