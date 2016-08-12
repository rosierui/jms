// SCCSID "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=MQJavaSamples/jms/interactive/SampleProducerJava.java"
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="1352245721" > 
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
package interactive;

import interactive.helper.JmsApp;
import interactive.helper.Literals;
import interactive.helper.MyContext;
import interactive.helper.Options;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;

/**
 * Send message(s) to a topic/queue.
 */
public class SampleProducerJava extends JmsApp {

  /**
   * The first piece of sample text to use as a message.
   */
  private final static String message1 = "But soft! What light through yonder window breaks? \n"
      + "Arise, fair sun, and kill the envious moon \n"
      + "That thou her maid art far more fair than she. \n"
      + "Her vestal livery is but sick and green,";

  /**
   * The second piece of sample text to use as a message.
   */
  private final static String message2 = "It is the East, and Juliet is the sun! \n"
      + "Who is already sick and pale with grief \n" + "Be not her maid, since she is envious. \n"
      + "And none but fools do wear it. Cast it off.";

  /**
   * The number of messages sent so far.
   */
  private int messagesSent;

  /**
   * The number of messages to send in total.
   */
  private String messagesInTotal;

  /**
   * Constructor.
   */
  protected SampleProducerJava(String[] args) {
    super(args, MyContext.Producer);
    messagesSent = 0;
    messagesInTotal = Options.NumberOfMessages.Value();
    return;
  }

  /**
   * The main entry point for the application.
   * 
   * @param args Cmd line arguments
   */
  public static void main(String[] args) {
    // Check if user needs help
    if ((args.length > 0) && (args[0].endsWith("?") || args[0].toLowerCase().endsWith("help"))) {
      DisplayHelp();
      return;
    }

    try {
      SampleProducerJava sampleProducer = new SampleProducerJava(args);
      sampleProducer.SendMessages();
    }
    catch (Exception ex) {
      System.out.println("Exception caught:\n" + ex);
      System.out.println("Sample execution FAILED!");
      System.exit(-1);
    }

    // Read an input before termination
    System.out.print("Press return key to continue...");
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    try {
      reader.read();
    }
    catch (IOException ioex) {
      ioex.printStackTrace();
    }
    System.exit(0);
    return;
  } // end Main

  /**
   * Send the message(s).
   */
  private void SendMessages() throws Exception {
    // Variables
    Connection connection = null;
    Session session = null;
    MessageProducer producer = null;

    try {
      ConnectionFactory connectionFactory = MyCreateConnectionFactory();

      System.out.println("Connection Factory created.");

      connection = MyCreateConnection(connectionFactory);

      System.out.println("Connection created.");

      session = MyCreateSession(connection);

      System.out.println("Session created.");

      Destination destination = MyCreateDestination(session);

      System.out.println("Destination created: " + destination.toString());

      producer = MyCreateProducer(session, destination);

      System.out.println("Producer created.");

      // Start the connection
      connection.start();

      // Send specified number of messages
      Message sendMsg;
      while (messagesInTotal.equals(Literals.Infinite)
          || (messagesSent < Integer.parseInt(messagesInTotal))) {
        // Create a message
        sendMsg = CreateNewMessage(session, Options.MessageType.Value());

        // Send the message
        producer.send(sendMsg);

        // Increment the counter
        messagesSent++;

        // Display sent message
        DisplayMessage(sendMsg);

        // Sleep for a while to allow the user to see the output on the screen
        Thread.sleep(Options.Interval.ValueAsNumber() * 1000);

      } // end of while
    }
    catch (JMSException jmsex) {
      processJMSException(jmsex);
    }
    finally {
      if (producer != null) {
        try {
          producer.close();
        }
        catch (JMSException jmsex) {
          System.out.println("Producer could not be closed.");
          processJMSException(jmsex);
        }
      }

      if (session != null) {
        try {
          session.close();
        }
        catch (JMSException jmsex) {
          System.out.println("Session could not be closed.");
          processJMSException(jmsex);
        }
      }

      if (connection != null) {
        try {
          connection.close();
        }
        catch (JMSException jmsex) {
          System.out.println("Connection could not be closed.");
          processJMSException(jmsex);
        }
      }
    }

    // We're finished.
    System.out.println("\nSample execution SUCCESSFUL.\n");

    return;
  } // end SendMessages

  /**
   * Create a new message of desired type for a given session.
   */
  private Message CreateNewMessage(Session session, String type) throws Exception {
    // The new message
    Message msg = null;

    try {

      // The message text
      StringBuffer messageText = new StringBuffer();

      // Label the message with a message number
      messageText.append("[This is message ");
      messageText.append(messagesSent + 1);
      messageText.append(" in this sample run] ");

      // Append the message text
      if (!Options.MessageText.IsNull()) {
        // User provided message text
        messageText.append(Options.MessageText.Value());
      }
      else {
        // Alternate between two message texts
        messageText.append((messagesSent % 2 == 0) ? message1 : message2);
      }

      if (type.equals(Literals.Base)) {

        msg = session.createMessage();

      }
      else if (type.equals(Literals.Bytes)) {

        msg = session.createBytesMessage();

        ((BytesMessage) msg).writeUTF(messageText.toString());

      }
      else if (type.equals(Literals.Map)) {

        msg = session.createMapMessage();

        ((MapMessage) msg).setString("aStringName", messageText.toString());

      }
      else if (type.equals(Literals.Object)) {

        msg = session.createObjectMessage();

        byte[] byteText = messageText.toString().getBytes("UTF-8");

        ((ObjectMessage) msg).setObject(byteText);

      }
      else if (type.equals(Literals.Stream)) {

        msg = session.createStreamMessage();

        ((StreamMessage) msg).writeString(messageText.toString());

      }
      else if (type.equals(Literals.Text)) {

        msg = session.createTextMessage(messageText.toString());

      }
      else {
        // Should not come here
        msg = null;
      }

      // Set custom properties
      msg.setStringProperty("MyStringProperty", "My Year Of Birth");
      msg.setIntProperty("MyIntProperty", 2007);

    }
    catch (JMSException jmsex) {
      System.out.println("Error: Unable to create a new message.");
      throw jmsex;
    }
    catch (Exception ex) {
      System.out.println("Error: Unable to create a new message.");
      throw ex;
    }

    return msg;
  } // end CreateNewMessage

  /**
   * Display message contents.
   */
  private void DisplayMessage(Message msg) {
    String marker = "---------------";

    if (messagesInTotal.equals(Literals.Infinite)) {
      System.out.println("\n" + marker + " Message " + messagesSent + " sent " + marker);
    }
    else {
      System.out.println("\n" + marker + " Message " + messagesSent + " of " + messagesInTotal
          + " sent " + marker);
    }

    System.out.println(msg);
    return;
  } // end DisplayMessage

  /**
   * Display help to the user.
   */
  private static void DisplayHelp() {
    System.out.println();
    System.out.println("Usage: Produces messages to a topic/queue");
    System.out.println();
    System.out.println("  SampleProducerJava [ < response_file ]");
    System.out.println();
    return;
  }
} // end SampleProducerJava

