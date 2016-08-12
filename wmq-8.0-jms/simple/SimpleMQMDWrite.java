// SCCSID "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=MQJavaSamples/jms/simple/SimpleMQMDWrite.java"
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="862997680" > 
 *   Licensed Materials - Property of IBM  
 *    
 *   5724-H72,5655-R36,5655-L82,5724-L26, 
 *    
 *   (C) Copyright IBM Corp. 2008, 2012 All Rights Reserved.  
 *    
 *   US Government Users Restricted Rights - Use, duplication or  
 *   disclosure restricted by GSA ADP Schedule Contract with  
 *   IBM Corp.  
 *   </copyright> 
 */

package simple;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsDestination;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

/**
 * A simple application that demonstrates how a JMS application may write MQ Message Descriptor
 * (MQMD) fields. No messages are received.
 * 
 * Application makes use of fixed literals, any customisations will require re-compilation of this
 * source file.
 * 
 * Notes:
 * 
 * API type: IBM JMS API (v1.1, unified domain)
 * 
 * Messaging domain: Point-to-point
 * 
 * Provider type: WebSphere MQ
 * 
 * Connection mode: Client connection
 * 
 * JNDI in use: No
 * 
 * Queue manager level assumed: v7 or above
 * 
 */
public class SimpleMQMDWrite {

  /*
   * The following note is based on MQMD_VERSION_2. For information about newer fields introduced by
   * a newer version of the MQMD structure, refer to the WebSphere MQ documentation.
   * 
   * Message context: Certain fields in MQMD contain the message context. There are two types of
   * message context -- identity context and origin context. Setting a MQMD field that contains the
   * message context requires the current user to have appropriate WebSphere MQ authority. For the
   * information about Message context, refer to the WebSphere MQ documentation.
   * 
   * Following fields can be set without setting the message context:
   * 
   * CodedCharSetId, CorrelId, Encoding, Expiry, Feedback, Format, GroupId, MsgFlags, MsgId,
   * MsgSeqNumber, MsgType, Offset, OriginalLength, Persistence, Priority, ReplyToQ, ReplyToQMgr,
   * Report.
   * 
   * Following fields require the identity context authority or, the superset, origin context
   * authority: AccountingToken, ApplIdentityData, UserIdentifier.
   * 
   * Following fields require the origin context authority: ApplOriginData, PutApplName,
   * PutApplType, PutDate, PutTime.
   * 
   * The identity context can be set in JMS by the following:
   * 
   * ((JmsDestination) destination).setIntProperty(WMQConstants.WMQ_MQMD_MESSAGE_CONTEXT,
   * WMQConstants.WMQ_MDCTX_SET_ALL_CONTEXT);
   * 
   * The origin context can be set in JMS by the following:
   * 
   * ((JmsDestination) destination).setIntProperty(WMQConstants.WMQ_MQMD_MESSAGE_CONTEXT,
   * WMQConstants.WMQ_MDCTX_SET_ALL_CONTEXT);
   * 
   * The identity context must be set in the above manner by setting a property on the destination
   * before the corresponding (identified) Producer object is instantiated.
   */

  // System exit status value (assume unset value to be 1)
  private static int status = 1;

  /**
   * Main method
   * 
   * @param args
   */
  public static void main(String[] args) {

    // Variables
    Connection connection = null;
    Session session = null;
    Destination destination = null;
    MessageProducer producer = null;

    try {
      // Create a connection factory
      JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
      JmsConnectionFactory cf = ff.createConnectionFactory();

      // Set the properties
      cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, "localhost");
      cf.setIntProperty(WMQConstants.WMQ_PORT, 1414);
      cf.setStringProperty(WMQConstants.WMQ_CHANNEL, "SYSTEM.DEF.SVRCONN");
      cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
      cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, "QM1");

      // Insist on queue manager level to be v7 or above
      cf.setStringProperty(WMQConstants.WMQ_PROVIDER_VERSION, "7.0.0.0");

      // Create JMS objects
      connection = cf.createConnection();
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      destination = session.createQueue("queue:///Q1");

      // Enable write of MQMD fields. See documentation for further details.
      ((JmsDestination) destination).setBooleanProperty(WMQConstants.WMQ_MQMD_WRITE_ENABLED, true);

      // Set message context, if needed. See comment at the top.

      // Create a producer
      producer = session.createProducer(destination);

      // Create a message
      long uniqueNumber = System.currentTimeMillis() % 1000;
      TextMessage message = session
          .createTextMessage("SimpleMQMDWrite: Your lucky number today is " + uniqueNumber);

      // Generate a custom message id
      byte[] customMessageId = new byte[24];
      for (int i = 0; i < 24; i++) {
        // Hex-string 010203040506070801020304050607080102030405060708
        customMessageId[i] = (byte) ((i % 8) + 1);
      }

      // Write to MQMD.MsgId via JMS_IBM_MQMD_MSGID message property
      message.setObjectProperty(WMQConstants.JMS_IBM_MQMD_MSGID, customMessageId);

      // Start the connection
      connection.start();

      // And, send the message
      producer.send(message);
      System.out.println("Sent message:\n" + message);

      recordSuccess();
    }
    catch (JMSException jmsex) {
      recordFailure(jmsex);
    }
    finally {
      if (producer != null) {
        try {
          producer.close();
        }
        catch (JMSException jmsex) {
          System.out.println("Producer could not be closed.");
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
      }
      else {
        System.out.println(ex);
      }
    }
    System.out.println("FAILURE");
    status = -1;
    return;
  }

}
