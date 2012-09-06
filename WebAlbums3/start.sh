ROOT_PATH=/other/Web
WA_HOME="."
java -Droot.path=$ROOT_PATH \
     -Dfile.encoding=UTF-8 \
	 -Djava.library.path=$WA_HOME/lib \
	 -classpath \
$WA_HOME/lib/WebAlbums-libs/RT-DB-mysql-connector-java-5.1.12-bin.jar:\
$WA_HOME/lib/WebAlbums-libs/RT-Log-logback-classic-0.9.24.jar:\
$WA_HOME/lib/WebAlbums-libs/RT-Log-logback-core-0.9.24.jar:\
$WA_HOME/lib/WebAlbums-libs/CP-Log-slf4j-api-1.6.0.jar:\
$WA_HOME/bin/WebAlbums3-Bootstrap.jar net.wazari.bootstrap.GF
