cp *.hbm *.xml *.properties ../classes ; javac -g -d ../classes/ -cp ".:../lib`sh classpath.sh`" */*.java 
