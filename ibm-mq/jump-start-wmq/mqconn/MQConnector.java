package mqconn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class MQConnector {
  protected String qManager = ""; // define name of queue manager

  protected String qManagerHost = "";

  protected String queuName = ""; // define name of queue

  protected MQQueue mqQueue;

  protected MQQueueManager qMgr;

  public static boolean DEBUG = true;

  /**
   * Setting up the WebSphere MQ environment for WebSphere MQ classes for Java
   * https://www.ibm.com/support/knowledgecenter/en/SSFKSJ_7.5.0/com.ibm.mq.dev.doc/q030790_.htm
   */
  public MQConnector() {
  }

  public void initMq() {
    try  {
      FileInputStream fis = new FileInputStream(new File("mqconnect.properties"));
      Properties props = new Properties();
      props.load(fis);
      fis.close();
      qManager = props.getProperty("queue.manager");
      qManagerHost = props.getProperty("queue.manager.host");
      queuName = props.getProperty("queue.name");

      // Create a connection to the queue manager
      MQEnvironment.channel = "SYSTEM.DEF.SVRCONN";
      MQEnvironment.hostname = qManagerHost;
      debug("Connecting to QueueManager " + qManager + " on " + qManagerHost);
      qMgr = new MQQueueManager(qManager);

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void openQueue() throws MQException {

    // Set up the options on the queue we wish to open...
    // Note. All WebSphere MQ Options are prefixed with MQC in Java.
     int openOptions = MQC.MQOO_INPUT_AS_Q_DEF | MQC.MQOO_OUTPUT;
    // Now specify the queue that we wish to open,
    // and the open options...    
    debug("Opening queue: " + queuName);
    try {
      mqQueue = qMgr.accessQueue(queuName, openOptions);
    }
    catch(MQException mqe) {
      //check if MQ reason code 2045
      //means that opened queu is remote and it can not be opened as 
      //input queue
      //try to open as output only
      if(mqe.reasonCode==2045) {
        openOptions = MQC.MQOO_OUTPUT;
        mqQueue = qMgr.accessQueue(queuName, openOptions);
      }
    }
  }

  public void putMessageToQueue(String msg) throws MQException {
    try {
      debug("Sending message: " + msg);

      MQPutMessageOptions pmo = new MQPutMessageOptions(); 
      MQMessage mqMsg = new MQMessage();
      mqMsg.write(msg.getBytes());

      // put the message on the queue
      mqQueue.put(mqMsg, pmo);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getMessageFromQueue() throws MQException {
    try {
      MQMessage mqMsg = new MQMessage();

      MQGetMessageOptions gmo = new MQGetMessageOptions();

      // Get a message from the queue
      mqQueue.get(mqMsg,gmo);

      //Extract the message data
      int len=mqMsg.getDataLength();
      byte[] message = new byte[len];
      mqMsg.readFully(message,0,len);
      return new String(message);
    }
    catch(MQException mqe) {
      int reason=mqe.reasonCode;

      if(reason==2033){ //no messages
        return null;
      }
      else
      {
        throw mqe;
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void closeQueue() throws MQException
  {
    debug("Closing queue...");

    // Close the queue...
    mqQueue.close();

  }

  public void disconnectMq() throws MQException {
    debug("Disconnecting QueueManager...");

    // Disconnect from the queue manager
    qMgr.disconnect();
  }

  
  protected boolean hasArg(String arg, String[] args) {
    for(int i=0;i<args.length;i++) {
      if(args[i].equals(arg)) {
        return true;
      }
    }
    return false;
  }

  public void debug(Object msg) {
    if (DEBUG) {
      System.out.println(msg);
    }
  }
}
