Jump start WebSphere MQ development on Linux
http://www.ibm.com/developerworks/websphere/library/techarticles/0705_salkosuo/0705_salkosuo.html

Configuring WebSphere MQ
  Configuration consists of following steps:
    Create queue manager.
    Start queue manager.
    Create queues and channels.
    Start command server, listener, and channels.
    These steps are required on both WebSphere MQ machines. The sections below show configuration only on WMQ1, but the same steps are required on WMQ2 also.
    Create and start queue managers
    Log in to WMQ1 using root and then change to user mqm by typing su - mqm. 

Create queue manager for the WMQ1 machine using the following command:
mqm@wmq1:~/ # crtmqm WMQ1QM

Start the queue manager using following command.
mqm@wmq1:~/ # strmqm WMQ1QM

Create queue and channels
Configure WebSphere MQ using command-line tools. A configuration script for WMQ1 is below. The script defines local queue, remote queue, transmission queue, sender channel, and receiver channel.Save the script as WMQ1QM.conf

Configure WebSphere MQ using the command below to create queues and channels:
mqm@wmq1:~/ # runmqsc WMQ1QM < WMQ1QM.conf > qcreate.log

The command runmqsc is used to issue WebSphere MQ commands. In this case, commands are read from the WMQ1QM.conf file and output is directed to qcreate.log. Open qcreate.log and verify that there were no syntax errors and all valid commands were processed.

Start services
The command server, listener, and channels need to be started and then everything is ready for developing and testing the sample applications:
mqm@wmq1:~/ # strmqcsv WMQ1QM &
mqm@wmq1:~/ # runmqlsr -m WMQ1QM -t TCP &

Verify that the queue managers and listeners are running on both machines, then start the channels:
mqm@wmq1:~/ # runmqchl -m WMQ1QM -c WMQ1QM.WMQ2QM &
mqm@wmq1:~/ # runmqchl -m WMQ1QM -c WMQ2QM.WMQ1QM &

Build / Run test code
javac -cp "/g01/srv/mqm/java/lib/*:" mqconn/MQConnector.java
java  -cp "/g01/srv/mqm/java/lib/*:" mqconn/MQConnector

java -cp .:com.ibm.mq.jar mqconn.MQSend "$@"
java -cp .:com.ibm.mq.jar mqconn.MQGet "$@"

