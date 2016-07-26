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
 * @author saket
 */
public class SimplePTP {
  /**
   * https://hursleyonwmq.wordpress.com/2007/02/07/what-tcp-ports-are-you-using-for-channel-listeners/
   * 1414 - hanging
   * 1420 - com.ibm.msg.client.jms.DetailedJMSException: JMSWMQ0018: Failed to connect to queue manager 'QM1' with connection mode 'Client' and host name 'localhost(1420)'.
   *        Check the queue manager is started and if running in client mode, check there is a listener running. Please see the linked exception for more information.
   * Constants for WebSphere MQ JMS and WebSphere MQ Java Classes 
   *        http://www-01.ibm.com/support/docview.wss?uid=swg21423244
   *
   * 1) sudo adduser mq_user (one time on Linux)
   * 2) run moonwave/jms/wmq/scripts/create-qmgr.sh
   * 3) change QueueManager and port to match in create-qmgr.sh
   * 4) run this program
   */

  private static BufferedReader stdin = null;
  
  public static void main(String[] args) {
    try {
      stdin = new BufferedReader(new InputStreamReader(System.in));
      System.out.print("Username: ");
      String username = stdin.readLine(); // mq_user

      System.out.print("Password: ");
      String password = stdin.readLine();
      MQQueueConnectionFactory cf = new MQQueueConnectionFactory();

      cf.setHostName("192.168.0.13");
      cf.setQueueManager("QMA");
      cf.setPort(1414);

      cf.setChannel("JAVA.CHANNEL"); // JAVA.CHANNEL | SYSTEM.DEF.SVRCONN - Sets the name of the channel - applies to client transport mode only
      cf.setTransportType (WMQConstants.WMQ_CM_CLIENT); // WMQ_CM_DIRECT_TCPIP 

      MQQueueConnection connection = (MQQueueConnection) cf.createQueueConnection(username, password); // cf.createQueueConnection()
      MQQueueSession session = (MQQueueSession) connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
      MQQueue queue = (MQQueue) session.createQueue("queue:///REQUEST_Q"); // Note three forward slashes are required (not two) to account for a default queue manager name
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
