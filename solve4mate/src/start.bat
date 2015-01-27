@echo off
:: Provides a wrapper around the engine
::
::Print the java version we're using and will call out if 64 bit is not being used
set "JAVA=%JAVA_HOME%\jre\bin\java.exe"
%JAVA% -d64 -version
%JAVA% -jar engine.jar
