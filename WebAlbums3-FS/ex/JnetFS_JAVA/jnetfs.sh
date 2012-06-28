#!/bin/sh
if [ "$1" = "" ]; then
  java -Djava.library.path=. -jar jnetfsvr.jar -h
else
  java -Djava.library.path=. -Djnet.ip="$1" -jar jnetfsvr.jar "$2"
fi

