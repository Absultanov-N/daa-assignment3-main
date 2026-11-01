#!/usr/bin/env sh
set -eu
mkdir -p out
javac -Xlint:all -d out src/*.java tests/Assignment3Test.java
java -cp out Assignment3Test
