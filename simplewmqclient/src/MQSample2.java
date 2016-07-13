import javax.jms.Connection;

import com.ibm.mq.*;            // Include the MQSeries classes for Java package
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

/**
 * http://www.coderanch.com/t/77875/Websphere/Simple-Java-MQ-Connection
 * ==> error message
 *     com.ibm.mq.MQException: MQJE001: Completion Code '2', Reason '2538
 * 
 * http://stackoverflow.com/questions/21672833/mqje001-completion-code-2-reason-2538
 */
public class MQSample2 {

  private String hostname = "localhost";          // define the name of your
                                                  // host to connect to
  private String channel  = "SYSTEM.DEF.SVRCONN"; // define name of channel
                                                  // for client to use
                                                  // Note. assumes MQSeries Server
                                                  // is listening on the default
                                                  // TCP/IP port of 1414
  private String qManager = "QM2";                // define name of queue
                                                  // manager object to
                                                  // connect to.

  private MQQueueManager qMgr;                    // define a queue manager object
  private int port = 1414;

  // When the class is called, this initialization is done first.

  public void init() {
     // Set up MQSeries environment
     MQEnvironment.hostname = hostname;           // Could have put the
                                                  // hostname & channel
     MQEnvironment.channel  = channel;            // string directly here!

     MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY,//Set TCP/IP or server
                                  MQC.TRANSPORT_MQSERIES);//Connection
  } // end of init

  public void start() {

    try {
      // Create a connection to the queue manager
      qMgr = new MQQueueManager(qManager);

      // Set up the options on the queue we wish to open...
      // Note. All MQSeries Options are prefixed with MQC in Java.

      int openOptions = MQC.MQOO_INPUT_AS_Q_DEF |
                        MQC.MQOO_OUTPUT ;

      // Now specify the queue that we wish to open, and the open options...
  
      MQQueue system_default_local_queue =
              qMgr.accessQueue("SYSTEM.DEFAULT.LOCAL.QUEUE",
                               openOptions,
                               null,           // default q manager
                               null,           // no dynamic q name
                               null);          // no alternate user id

      // Define a simple MQSeries message, and write some text in UTF format..

      MQMessage hello_world = new MQMessage();
      hello_world.writeUTF("Hello World!");

      // specify the message options...

      MQPutMessageOptions pmo = new MQPutMessageOptions();  // accept the defaults,
                                                            // same as
                                                            // MQPMO_DEFAULT
                                            // constant
  
      // put the message on the queue
  
      system_default_local_queue.put(hello_world,pmo);
  
      // get the message back again...
      // First define a MQSeries message buffer to receive the message into..
  
      MQMessage retrievedMessage = new MQMessage();
      retrievedMessage.messageId = hello_world.messageId;
  
      // Set the get message options..
  
      MQGetMessageOptions gmo = new MQGetMessageOptions();  // accept the defaults
                                                            // same as
                                                            // MQGMO_DEFAULT
  
      // get the message off the queue..
  
      system_default_local_queue.get(retrievedMessage, gmo);
  
      // And prove we have the message by displaying the UTF message text
  
      String msgText = retrievedMessage.readUTF();
      System.out.println("The message is: " + msgText);
  
      // Close the queue
  
      system_default_local_queue.close();
  
      // Disconnect from the queue manager
  
      qMgr.disconnect();
  
    }
  
    // If an error has occurred in the above, try to identify what went wrong.
    // Was it an MQSeries error?
  
    catch (MQException ex)
    {
      System.out.println("An MQSeries error occurred : Completion code " +
                         ex.completionCode +
                         " Reason code " + ex.reasonCode);
    }
    // Was it a Java buffer space error?
    catch (java.io.IOException ex)
    {
      System.out.println("An error occurred whilst writing to the message buffer: " + ex);
    }
  
  } // end of start

  /**
   * com.ibm.msg.client.jms.DetailedJMSSecurityException: JMSWMQ2013: The security authentication was not valid that was supplied for QueueManager '' with connection mode 'Client' and host name 'localhost(1414)'.
   * Please check if the supplied username and password are correct on the QueueManager to which you are connecting.
   * ==> http://stackoverflow.com/questions/6012579/websphere-7-configuring-jms-q-connection-factory-without-user-id-mqrc-not-auth
   */
  public void connetion () {
      try {
          JmsFactoryFactory jmsFactory = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
          JmsConnectionFactory jcf = jmsFactory.createConnectionFactory();
    
          // Set the properties
          jcf.setStringProperty(WMQConstants.WMQ_HOST_NAME, hostname);
          jcf.setIntProperty(WMQConstants.WMQ_PORT, port);
          jcf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
          jcf.setStringProperty(WMQConstants.WMQ_CHANNEL, channel);
          jcf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, qManager);
    
          Connection connection = jcf.createConnection("moonwave", "");
      } catch (Exception e) {
          System.out.println("An error occurred: " + e);
          
      }
  }

  public static void main(String[] argvs) {
      MQSample2 mq = new MQSample2();
      mq.connetion();
      mq.init();
      mq.start();
  }
} // end of sample