Copied from /g01/srv/mqm/samp/wmqjava/samples

ws-mq-usingJava-2002.pdf
    Verifying with the sample application p.32 (14)

1) build from wmq v8.0 releae jars
javac -cp "/g01/srv/mqm/java/lib/*:" SimplePTP.java
java  -cp "/g01/srv/mqm/java/lib/*:" SimplePTP

2) build from copy of wmq v8.0 release jars
cp /g01/srv/mqm/java/lib/*.jar lib

javac -cp "lib/*:" SimplePTP.java
java  -cp "lib/*:" SimplePTP
