build : 

server : build
	java -cp "JnetFS_Java/dist/JnetFS_Java.jar" com.jnetfs.core.Server

mount : build
	java  -Djava.library.path=JnetFS_C/lib/ -cp "JnetFS_Java/dist/JnetFS_Java.jar" com.jnetfs.core.JnetFS test
