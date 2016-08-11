package my.samples;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//https://hursleyonwmq.wordpress.com/2007/05/29/simplest-sample-applications-using-websphere-mq-jms/

//Simple Point-to-point application using WebSphere MQ JMS

import javax.jms.JMSException;
import javax.jms.Session;

import com.ibm.jms.JMSMessage;
import com.ibm.jms.JMSTextMessage;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueReceiver;
import com.ibm.mq.jms.MQQueueSender;
import com.ibm.mq.jms.MQQueueSession;
import com.ibm.msg.client.wmq.WMQConstants;

/**
 * SimplePTP: A minimal and simple testcase for Point-to-point messaging (1.02 style).
 *
 * Assumes that the queue is empty before being run.
 *
 * Does not make use of JNDI for ConnectionFactory and/or Destination definitions.
 *
 * Modeled from SimpleWMQJMSPTP.java
 */


public class SimplePTP {
  /**
   * 1) sudo adduser mq_user (one time on Linux)
   * 2) run moonwave/jms/wmq/scripts/create-qmgr.sh
   * 3) run this program
   */

  private static BufferedReader stdin = null;
  static String defaultUser     = "mq_user";
  static String defaultHost     = "192.168.0.13"; // 10.0.2.15
  static int    defaultPort     = 1415; // 30002
  static String defaultQmgr     = "QM1";
  static String defaultChannnel = "JAVA.CHANNEL"; //"SYSTEM.DEF.SVRCONN";
  static String defaultQueue    = "REQUEST_Q";

  public static void main(String[] args) {
    try {
      stdin = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("Username [" + defaultUser + "]: ");
      String username = stdin.readLine();
      if (username.trim().length() == 0) {
          username = defaultUser;
      }

      System.out.print("Password: ");
      String password = stdin.readLine();

      System.out.print("Host [" + defaultHost + "]: ");
      String host = stdin.readLine();
      if (host.trim().length() == 0) {
          host = defaultHost;
      }

      System.out.print("Port [" + defaultPort + "]: ");
      String portIn = stdin.readLine();
      int port  = 0;
      if (portIn.trim().length() == 0) {
          port = defaultPort;
      } else {
          port = Integer.valueOf(portIn);
      }

      System.out.print("QueueManager [" + defaultQmgr + "]: ");
      String qmgr = stdin.readLine();
      if (qmgr.trim().length() == 0) {
          qmgr = defaultQmgr;
      }

      System.out.print("Queue [" + defaultQueue + "]: ");
      String queueName = stdin.readLine();
      if (queueName.trim().length() == 0) {
    	  queueName = defaultQueue;
      }

      MQQueueConnectionFactory cf = new MQQueueConnectionFactory();

      cf.setHostName(host);
      cf.setPort(port);
      cf.setQueueManager(qmgr);
      cf.setChannel(defaultChannnel);
      cf.setTransportType (WMQConstants.WMQ_CM_CLIENT); // WMQ_CM_DIRECT_TCPIP 

      MQQueueConnection connection = (MQQueueConnection) cf.createQueueConnection(username, password); // cf.createQueueConnection()
      MQQueueSession session = (MQQueueSession) connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
      //MQQueue queue = (MQQueue) session.createQueue("queue:///" + queueDef); // Note three forward slashes are required (not two) to account for a default queue manager name
      MQQueue queue = (MQQueue) session.createQueue(queueName); // this works
      MQQueueSender sender =  (MQQueueSender) session.createSender(queue);
      MQQueueReceiver receiver = (MQQueueReceiver) session.createReceiver(queue);

      long uniqueNumber = System.currentTimeMillis() % 1000;
      JMSTextMessage message = (JMSTextMessage) session.createTextMessage("SimplePTP "+ uniqueNumber);

      // Start the connection
      connection.start();

      sender.send(message);
      System.out.println("Sent message:\\n" + message);

      JMSMessage receivedMessage = (JMSMessage) receiver.receive(10000);
      System.out.println("\\nReceived message:\\n" + receivedMessage);

      sender.close();
      receiver.close();
      session.close();
      connection.close();

      System.out.println("\\nSUCCESS\\n");
    }
    catch (JMSException jmsex) {
      System.out.println(jmsex);
      System.out.println("\\nFAILURE\\n");
    }
    catch (Exception ex) {
      System.out.println(ex);
      System.out.println("\\nFAILURE\\n");
    }
  }
}
