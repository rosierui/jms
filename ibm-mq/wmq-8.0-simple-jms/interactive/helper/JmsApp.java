// SCCSID "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=MQJavaSamples/jms/interactive/helper/JmsApp.java"
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26" 
 *   years="2008,2014" 
 *   crc="329500830" > 
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
package interactive.helper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

/**
 * An abstract class that can be extended to provide consumer/producer functionality.
 */
public abstract class JmsApp {

  /**
   * Constructor.
   * 
   * @param args User specified list of arguments
   * @param ctx Specifies the context for options
   */
  protected JmsApp(String[] args, MyContext ctx) {

    // Request options from the user
    new OptionsPresenter(ctx);

    // Write out user responses to a file in current directory
    Writer writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(Options.ConnectionType.Value().toLowerCase() + "-"
          + ctx.toString().toLowerCase() + ".rsp"));

      writer.write(BaseOptions.UserResponses());
    }
    catch (IOException ioex) {
      System.out.println("Error: Unable to write a response file.");
      ioex.printStackTrace();
    }
    finally {
      try {
        if (writer != null) {
          writer.close();
        }
      }
      catch (IOException ioex) {
        System.out.println("Error: Unable to close the response file.");
        ioex.printStackTrace();
      }
    }
    return;
  }

  /**
   * Create a connection factory.
   */
  protected static ConnectionFactory MyCreateConnectionFactory() throws JMSException {
    // TODO: Initial context not implemented
    /*
     * // If an initial context was provided, returned connection factory already looked up if
     * (InitContext.context != null) { return InitContext.connectionFactory; }
     */
    if (Options.ConnectionType.Value().equals(Literals.WMQ)) {
      // WMQ connection factory
      return MyCreateConnectionFactoryWMQ();
    }
    else {
      // Should never come here
      return null;
    }
  } // end MyCreateConnectionFactory

  /**
   * Create a WMQ connection factory and set relevant properties.
   */
  private static ConnectionFactory MyCreateConnectionFactoryWMQ() throws JMSException {
    JmsConnectionFactory cf = null;

    try {
      // Create a connection factory
      JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);

      cf = ff.createConnectionFactory();

      // Set the properties
      cf.setStringProperty(WMQConstants.WMQ_PROVIDER_VERSION, Options.ProviderVersion.Value());

      cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, Options.HostnameWMQ.Value());

      cf.setIntProperty(WMQConstants.WMQ_PORT, Options.PortWMQ.ValueAsNumber());

      cf.setStringProperty(WMQConstants.WMQ_CHANNEL, Options.Channel.Value());

      cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, Options.ConnectionMode.ValueAsNumber());

      if (!Options.QueueManager.IsNull()) {
        cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, Options.QueueManager.Value());
      }

      // Broker version is only applicable for ProviderVersion unspecified or v6
      if (Options.ProviderVersion.Value().equals(Literals.ProviderVersionUnspecified)
          || Options.ProviderVersion.Value().equals(Literals.ProviderVersion6)) {
        cf.setIntProperty(WMQConstants.WMQ_BROKER_VERSION, Options.BrokerVersion.ValueAsNumber());

        if (Options.BrokerVersion.Value().equals(Literals.Integrator)) {
          cf.setStringProperty(WMQConstants.WMQ_BROKER_PUBQ, Options.BrokerPublishQueue.Value());
        }
      }

    }
    catch (JMSException jmsex) {
      System.out.println("Error: Unable to create WMQ connection factory.");
      throw jmsex;
    }

    return cf;
  } // end MyCreateConnectionFactoryWMQ

  /**
   * Create a connection with relevant values.
   * 
   * @param cf The connection factory to use
   */
  protected static Connection MyCreateConnection(ConnectionFactory cf) throws JMSException {
    Connection connection = null;
    try {
      // Create a connection
      if (!Options.UserID.IsNull() && !Options.Password.IsNull()) {
        connection = cf.createConnection(Options.UserID.Value(), Options.Password.Value());
      }
      else {
        connection = cf.createConnection();
      }
    }
    catch (JMSException jmsex) {
      System.out.println("Error: Unable to create connection using the connection factory.");
      throw jmsex;
    }
    return connection;
  } // end MyCreateConnection

  /**
   * Create a session with relevant values.
   * 
   * @param connection The connection to use
   */
  protected static Session MyCreateSession(Connection connection) throws JMSException {
    Session session = null;

    try {
      // Create a session
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }
    catch (JMSException jmsex) {
      System.out.println("Error: Unable to create a session using the connection.");
      throw jmsex;
    }

    return session;
  } // end MyCreateSession

  /**
   * Create a destination with relevant values.
   * 
   * @param session The session to use
   */
  protected static Destination MyCreateDestination(Session session) throws JMSException {
    /*
     * // If an initial context was provided, returned destination already looked up if
     * (InitContext.context != null) { return InitContext.destination; }
     */
    Destination destination = null;

    try {
      if (Options.ConnectionType.Value().equals(Literals.WMQ)) {
        // WMQ destination
        // Create a topic if the destination name starts with topic://. Otherwise, default to
        // creating a queue.
        destination = (Options.DestinationWMQ.Value().startsWith("topic://"))
            ? (Destination) session.createTopic(Options.DestinationWMQ.Value())
            : (Destination) session.createQueue(Options.DestinationWMQ.Value());
      }
      else {
        // Should never come here
        return null;
      }
    }
    catch (JMSException jmsex) {
      System.out.println("Error: Unable to create the destination using the session.");
      throw jmsex;
    }

    return destination;
  } // end MyCreateDestination

  /**
   * Create a producer and set relevant properties.
   * 
   * @param session The session to use
   * @param destination The destination to use
   */
  protected static MessageProducer MyCreateProducer(Session session, Destination destination)
      throws JMSException {
    MessageProducer producer = null;

    try {
      producer = session.createProducer(destination);
      producer.setDeliveryMode(Options.DeliveryModeWMQ.ValueAsNumber());
    }
    catch (JMSException jmsex) {
      System.out.println("Error: Unable to create a producer using the session and destination.");
      throw jmsex;
    }

    return producer;
  } // end MyCreateProducer

  /**
   * Create a consumer and set relevant properties.
   * 
   * @param session The session to use
   * @param destination The destination to use
   */
  protected static MessageConsumer MyCreateConsumer(Session session, Destination destination)
      throws JMSException {
    MessageConsumer consumer = null;

    try {
      consumer = (Options.Selector.IsNull()) ? session.createConsumer(destination) : session
          .createConsumer(destination, Options.Selector.Value());
    }
    catch (JMSException jmsex) {
      System.out.println("Error: Unable to create a consumer using the session and destination.");
      throw jmsex;
    }

    return consumer;
  } // end MyCreateConsumer

  /**
   * Process a JMSException and any associated inner exceptions.
   * 
   * @param jmsex
   */
  protected static void processJMSException(JMSException jmsex) {
    System.out.println(jmsex);
    Throwable innerException = jmsex.getLinkedException();
    if (innerException != null) {
      System.out.println("Inner exception(s):");
    }
    while (innerException != null) {
      System.out.println(innerException);
      innerException = innerException.getCause();
    }
    System.out.println("Sample execution FAILED!");
    System.exit(-1);
    return;
  }

} // end JMSApp
