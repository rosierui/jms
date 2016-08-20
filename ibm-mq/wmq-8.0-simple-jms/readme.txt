Copied from wmq v8.0 installation
/j01/srv/mqm/samp/jms/samples/simple

Using the IBM MQ JMS extensions (version 8.0.0)
https://www.ibm.com/support/knowledgecenter/SSFKSJ_8.0.0/com.ibm.mq.dev.doc/q032190_.htm

1) build from wmq v8.0 releae jars
javac -cp "/j01/srv/mqm/java/lib/*:" simple/SimplePTP.java
java  -cp "/j01/srv/mqm/java/lib/*:" simple/SimplePTP

2) build from copy of wmq v8.0 release jars
cp /j01/srv/mqm/java/lib/*.jar lib

javac -cp "lib/*:" simple/SimplePTP.java
java  -cp "lib/*:" simple/SimplePTP
