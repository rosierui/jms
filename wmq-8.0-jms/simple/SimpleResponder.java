// SCCSID "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=MQJavaSamples/jms/simple/SimpleResponder.java"
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2014" 
 *   crc="351956101" > 
 *   Licensed Materials - Property of IBM  
 *    
 *   5724-H72,5655-R36,5655-L82,5724-L26, 
 *    
 *   (C) Copyright IBM Corp. 2014 All Rights Reserved.  
 *    
 *   US Government Users Restricted Rights - Use, duplication or  
 *   disclosure restricted by GSA ADP Schedule Contract with  
 *   IBM Corp.  
 *   </copyright> 
 */

package simple;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;


/**
 * A simple application that listens on a destination for a message and then sends a reply to the
 * message's replyTo destination. The application is written to operate in conjunction with the
 * SimpleRequestor sample.
 * 
 * Application makes use of fixed literals, any customisations will require re-compilation of this
 * source file. Application assumes that the named queue is empty prior to a run.
 * 
 * Notes:
 * 
 * API type: JMS API (v1.1, unified domain)
 * 
 * Messaging domain: Point-to-point
 * 
 * Provider type: WebSphere MQ
 * 
 * Connection mode: Client connection
 * 
 * JNDI in use: No
 * 
 */
public class SimpleResponder {

  // System exit status value (assume unset value to be 1)
  private static int status = 1;

  public static void main(String[] args) {

    SimpleResponder theResponder = new SimpleResponder();
    theResponder.runResponder(args);

    return;
  }



  private void runResponder(String[] args) {

    // Variables
    Connection connection = null;
    Session session = null;
    Destination destination = null;
    MessageConsumer consumer = null;

    try {

      // Create a connection factory
      JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
      JmsConnectionFactory cf = ff.createConnectionFactory();

      // Set the properties
      cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, "localhost");
      cf.setIntProperty(WMQConstants.WMQ_PORT, 1416);
      cf.setStringProperty(WMQConstants.WMQ_CHANNEL, "SYSTEM.DEF.SVRCONN");
      cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
      cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, "mqfvjms1");

      // Create JMS objects
      connection = cf.createConnection();
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      destination = session.createQueue("queue:///JMS.FV.LOCAL.Q");
      consumer = session.createConsumer(destination);

      // Set up the consumer to listen for messages and respond.
      MessageListener listener = new SimpleMessageListener(session);
      consumer.setMessageListener(listener);

      // Start the connection
      connection.start();

      // The connection is started, so the MessageConsumer is listening for and processing messages
      // Wait while messages are processed


      pause("Processing messages");
      recordSuccess();

    }
    catch (JMSException jmsex) {
      recordFailure(jmsex);
    }
    finally {
      if (consumer != null) {
        try {
          consumer.close();
        }
        catch (JMSException jmsex) {
          System.out.println("Consumer could not be closed.");
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

  }





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
      ex.printStackTrace();

      if (ex instanceof JMSException) {
        processJMSException((JMSException) ex);
      }
      else {
        System.out.println(ex);
      }
    }
    System.out.println("FAILURE");
    status = -1;
    return;
  }



  public class SimpleMessageListener implements MessageListener {

    private Session session;
    private MessageProducer producer;

    public SimpleMessageListener(Session session) throws JMSException {
      this.session = session;
      producer = session.createProducer(null);
    }

    @Override
    public void onMessage(Message message) {


      try {
        System.out.println("\nReceived message:\n" + message);

        Destination replyTo = message.getJMSReplyTo();

        if (replyTo == null) {
          // Can't reply
          System.out.println("No ReplyTo destination defined");
        }
        else {
          long uniqueNumber = System.currentTimeMillis() % 1000;
          Message replyMessage = session.createTextMessage("SimpleResponder: another lucky number " + uniqueNumber);

          producer.send(replyTo, replyMessage);

        }
      }
      catch (JMSException e) {
        recordFailure(e);
      }
      return;
    }
  }



  public static void pause(String comment) {

    try {

      Reader inReader = new InputStreamReader(System.in);
      BufferedReader reader = new BufferedReader(inReader);
      if (comment != null) {
        System.out.println("Pausing: " + comment);
      }
      else {
        System.out.println("Pausing");
      }
      System.out.println("Press Return to continue");
      reader.readLine();
    }
    catch (Throwable t) {
      System.err.println(t);
    }
    return;
  }

}
