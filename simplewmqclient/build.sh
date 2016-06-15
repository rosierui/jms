. ./env.sh

WMQ_LIB=$WMQ_HOME/java/lib


mkdir -p target

$JAVA_HOME/javac -classpath $WMQ_LIB/jms.jar:$WMQ_LIB/com.ibm.mq.jar:$WMQ_LIB/com.ibm.mqjms.jar -d target src/net/kevinboone/apacheintegration/simplewmgclient/SimpleWMQClient.java 
