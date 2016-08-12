//SCCSID "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=MQJavaSamples/jms/simple/SimpleWMQJMSPubSub.java"
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26" 
 *   years="2008,2014" 
 *   crc="199216406" > 
 *   Licensed Materials - Property of IBM  
 *    
 *   5724-H72,5655-R36,5655-L82,5724-L26 
 *    
 *   (C) Copyright IBM Corp. 2008, 2014 All Rights Reserved.  
 *    
 *   US Government Users Restricted Rights - Use, duplication or  
 *   disclosure restricted by GSA ADP Schedule Contract with  
 *   IBM Corp.  
 *   </copyright> 
 */

package simple;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQTopic;
import com.ibm.mq.jms.MQTopicConnection;
import com.ibm.mq.jms.MQTopicConnectionFactory;
import com.ibm.mq.jms.MQTopicPublisher;
import com.ibm.mq.jms.MQTopicSession;
import com.ibm.mq.jms.MQTopicSubscriber;

/**
 * A minimal and simple application for Publish-Subscribe messaging.
 * 
 * Application makes use of fixed literals, any customisations will require re-compilation of this
 * source file.
 * 
 * Notes:
 * 
 * API type: IBM WebSphere MQ JMS API (v1.02, domain specific)
 * 
 * Messaging domain: Publish-Subscribe
 * 
 * Provider type: WebSphere MQ
 * 
 * Connection mode: Client connection
 * 
 * JNDI in use: No
 * 
 */
@SuppressWarnings("deprecation")
public class SimpleWMQJMSPubSub {

  // System exit status value (assume unset value to be 1)
  private static int status = 1;

  /**
   * Main method
   * 
   * @param args
   */
  public static void main(String[] args) {

    // Variables
    MQTopicConnection connection = null;
    MQTopicSession session = null;
    MQTopic topic = null;
    MQTopicPublisher publisher = null;
    MQTopicSubscriber subscriber = null;

    try {
      // Create a connection factory
      MQTopicConnectionFactory cf = new MQTopicConnectionFactory();

      // Set the properties
      cf.setHostName("localhost");
      cf.setPort(1414);
      cf.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
      cf.setQueueManager("QM1");
      cf.setChannel("SYSTEM.DEF.SVRCONN");

      // Create JMS objects
      connection = (MQTopicConnection) cf.createTopicConnection();
      session = (MQTopicSession) connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
      topic = (MQTopic) session.createTopic("topic://foo");
      publisher = (MQTopicPublisher) session.createPublisher(topic);
      subscriber = (MQTopicSubscriber) session.createSubscriber(topic);

      long uniqueNumber = System.currentTimeMillis() % 1000;
      TextMessage message = session
          .createTextMessage("SimpleWMQJMSPubSub: Your lucky number today is " + uniqueNumber);

      // Start the connection
      connection.start();

      // And, send the message
      publisher.send(message);
      System.out.println("Sent message:\n" + message);

      // Now, receive the message
      Message receivedMessage = subscriber.receive(15000); // in ms or 15 seconds
      System.out.println("\nReceived message:\n" + receivedMessage);

      recordSuccess();
    }
    catch (JMSException jmsex) {
      recordFailure(jmsex);
    }
    finally {
      if (publisher != null) {
        try {
          publisher.close();
        }
        catch (JMSException jmsex) {
          System.out.println("Sender could not be closed.");
          recordFailure(jmsex);
        }
      }
      if (subscriber != null) {
        try {
          subscriber.close();
        }
        catch (JMSException jmsex) {
          System.out.println("Receiver could not be closed.");
          recordFailure(jmsex);
        }
      }

      if (session != null) {
        try {
          session.close();
        }
        catch (JMSException jmsex) {
          System.out.println("Session could not be closed.");
          recordFailure(jmsex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        }
        catch (JMSException jmsex) {
          System.out.println("Connection could not be closed.");
          recordFailure(jmsex);
        }
      }
    }
    System.exit(status);
    return;
  } // end main()

  /**
   * Process a JMSException and any associated inner exceptions.
   * 
   * @param jmsex
   */
  private static void processJMSException(JMSException jmsex) {
    System.out.println(jmsex);
    Throwable innerException = jmsex.getLinkedException();
    if (innerException != null) {
      System.out.println("Inner exception(s):");
    }
    while (innerException != null) {
      System.out.println(innerException);
      innerException = innerException.getCause();
    }
    return;
  }

  /**
   * Record this run as successful.
   */
  private static void recordSuccess() {
    System.out.println("SUCCESS");
    status = 0;
    return;
  }

  /**
   * Record this run as failure.
   * 
   * @param ex
   */
  private static void recordFailure(Exception ex) {
    if (ex != null) {
      if (ex instanceof JMSException) {
        processJMSException((JMSException) ex);
      } else {
        System.out.println(ex);
      }
    }
    System.out.println("FAILURE");
    status = -1;
    return;
  }

}
