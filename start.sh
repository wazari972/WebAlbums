#!/bin/sh

# Change this to the actual location of WebAlbum s
WA_HOME=$(dirname $0)
cd $WA_HOME
java    -Dfile.encoding=UTF-8 \
	-Djava.library.path=./lib \
	-jar ./dist/WebAlbums3-Bootstrap.jar $*
