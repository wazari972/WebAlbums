build-c : 
	make -C JnetFS_C all
clean-c :
	make -C JnetFS_C clean

JnetFS_C/lib/libJnetFS.so : build-c

clean-java :
	ant -buildfile ./JnetFS_Java/build.xml clean

build-java : 
	ant -buildfile ./JnetFS_Java/build.xml jar

JnetFS_Java/dist/JnetFS_Java.jar : build-java

clean : clean-java clean-c
    

mount : JnetFS_C/lib/libJnetFS.so JnetFS_Java/dist/JnetFS_Java.jar 
	/usr/lib/jvm/jre-1.7.0-openjdk.x86_64/bin/java  -Djava.library.path=JnetFS_C/lib/ -cp "JnetFS_Java/dist/JnetFS_Java.jar" com.jnetfs.core.JnetFS test
