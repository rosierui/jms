. ./env.sh

WMQ_LIB=$WMQ_HOME/java/lib

$JAVA_HOME/java -classpath $WMQ_LIB/jms.jar:$WMQ_LIB/com.ibm.mq.jar:$WMQ_LIB/com.ibm.mqjms.jar:target net.kevinboone.apacheintegration.simplewmqclient.SimpleWMQClient 
