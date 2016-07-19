package my.samples;

//https://hursleyonwmq.wordpress.com/2007/05/29/simplest-sample-applications-using-websphere-mq-jms/

//Simple Point-to-point application using WebSphere MQ JMS

import javax.jms.JMSException;
import javax.jms.Session;

import com.ibm.jms.JMSMessage;
import com.ibm.jms.JMSTextMessage;
import com.ibm.mq.jms.JMSC;
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
   * 
   */
  public static void main(String[] args) {
    try {
      MQQueueConnectionFactory cf = new MQQueueConnectionFactory();

      // Config
      cf.setHostName("10.0.2.15");//
      cf.setPort(1414); // 1414, 1420

      // Constants for WebSphere MQ JMS and WebSphere MQ Java Classes
      //     http://www-01.ibm.com/support/docview.wss?uid=swg21423244
      //cf.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP); // JMSC.MQJMS_TP_CLIENT_MQ_TCPIP | MQCNO_STANDARD_BINDING | MQJMS_TP_BINDINGS_MQ | JMSC.MQCNO_SHARED_BINDING
      cf.setTransportType (WMQConstants.WMQ_CM_CLIENT); // WMQ_CM_CLIENT | WMQ_CM_DIRECT_TCPIP 
      cf.setQueueManager("QMA");
      cf.setChannel("CHAN1"); // ClientConn1 | SYSTEM.DEF.SVRCONN - Sets the name of the channel - applies to client transport mode only

//      MQQueueConnection connection = (MQQueueConnection) cf.createQueueConnection();
      MQQueueConnection connection = (MQQueueConnection) cf.createQueueConnection("oracle", "welcome1");
      MQQueueSession session = (MQQueueSession) connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
      MQQueue queue = (MQQueue) session.createQueue("queue:///Q1"); // Note three forward slashes are required (not two) to account for a default queue manager name
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


