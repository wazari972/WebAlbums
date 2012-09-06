ROOT_PATH=/other/Web
WA_HOME="."
java -Droot.path=$ROOT_PATH \
     -Dfile.encoding=UTF-8 \
	 -Djava.library.path=$WA_HOME/lib \
	 -jar $WA_HOME/dist/WebAlbums3-Bootstrap.jar
