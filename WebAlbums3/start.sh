#!/bin/sh

# Change this to the actual location of WebAlbum s
WA_HOME="."
java    -Dfile.encoding=UTF-8 \
	-Djava.library.path=$WA_HOME/lib:/home/kevin/WebAlbums/apache-tomee-webprofile/lib \
	-jar $WA_HOME/dist/WebAlbums3-Bootstrap.jar $*
