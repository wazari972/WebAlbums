#!/bin/sh

cd ../WebAlbums3-Bootstrap
ant clean
ant jar --verbose

ANT_OPT="-Dj2ee.server.home=../WebAlbums3/glassfish -Dlibs.CopyLibs.classpath=../WebAlbums-libs/CopyLibs/org-netbeans-modules-java-j2seproject-copylibstask.jar"
cd ../WebAlbums3-ea
ant clean $ANT_OPT
ant dist $ANT_OPT #fails in JnetFS_Java

