#!/bin/bash
i=0
while [ $i -lt 100 ] 
do
(( i++ ))
cd bin
java Client DD2380.csc.kth.se 5032 $i&
cmdpid=$!
sleep 55
if [ -d /proc/$cmdpid ]
then
  echo "no solution found. exiting board no "$i
  kill $cmdpid
fi
cd ..
done
