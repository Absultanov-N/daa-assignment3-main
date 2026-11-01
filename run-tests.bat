@echo off
setlocal
if not exist out mkdir out
javac -Xlint:all -d out src\*.java tests\Assignment3Test.java
if errorlevel 1 exit /b 1
java -cp out Assignment3Test
