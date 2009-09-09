cp *.hbm *.xml ../classes ; javac -d ../classes/ -cp ".:../lib`sh classpath.sh`" */*.java && sudo /etc/init.d/tomcat5.5 restart
