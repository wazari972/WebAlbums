#!/bin/sh

cd ../WebAlbums3-Bootstrap
ant clean
ant jar 

cd ../WebAlbums3-ea
ant clean 
ant dist 

