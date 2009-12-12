cp *.hbm *.xml ../classes ; javac -g -d ../classes/ -cp ".:../lib`sh classpath.sh`" */*.java 
