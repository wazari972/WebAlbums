#!/bin/sh

cd ../WebAlbums-Bootstrap
ant clean
ant jar 

cd ../WebAlbums-ea
ant clean 
ant dist 

