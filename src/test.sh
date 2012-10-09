#!/bin/bash
echo "AI Sokoban"
echo "Compiling"
javac *.java
echo "Running testcases" 

for i in {1..10}
do
  echo "Testing board $i"
  java Client dd2380.csc.kth.se 5032 $i | grep -i 'CORRECT SOLUTION' $1
done

