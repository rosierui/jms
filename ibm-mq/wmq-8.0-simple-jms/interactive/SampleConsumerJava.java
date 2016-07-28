// SCCSID "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=MQJavaSamples/jms/interactive/SampleConsumerJava.java"
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26," 
 *   years="2008,2012" 
 *   crc="1109759252" > 
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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

/**
 * Receive message(s) from a topic/queue.
 */
public class SampleConsumerJava extends JmsApp {

  /**
   * The number of messages received so far.
   */
  private int messagesReceived;

  /**
   * The number of messages to receive in total.
   */
  private String messagesInTotal;

  /**
   * A lock that is used as the basis for a wait in the main thread, and as the basis of
   * notification in the listener thread.
   */
  private static final Object threadWaitLock = new Object();

  /**
   * The wait time for the main thread when waiting for the listener thread's notification.
   */
  private static final int waitTime = 5000; // 5 seconds

  /**
   * Flag used for break condition during asynchronous messaging.
   */
  private static final int MAX_NO_PROGRESS = 3;

  /**
   * Constructor.
   */
  protected SampleConsumerJava(String[] args) {
    super(args, MyContext.Consumer);
    messagesReceived = 0;
    messagesInTotal = Options.NumberOfMessages.Value();
    return;
  } // end constructor

  /**
   * The main entry point for the application.
   * 
   * @param args cmd line arguments
   */
  public static void main(String[] args) {
    // Check if user needs help
    if ((args.length > 0) && (args[0].endsWith("?") || args[0].toLowerCase().endsWith("help"))) {
      DisplayHelp();
      return;
    }

    try {
      SampleConsumerJava sampleConsumer = new SampleConsumerJava(args);
      sampleConsumer.ReceiveMessages();
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
   * Receive the message(s).
   */
  private void ReceiveMessages() throws Exception {
    // Variables
    Connection connection = null;
    Session session = null;
    MessageConsumer consumer = null;

    try {
      ConnectionFactory connectionFactory = MyCreateConnectionFactory();

      System.out.println("Connection Factory created.");

      connection = MyCreateConnection(connectionFactory);

      System.out.println("Connection created.");

      session = MyCreateSession(connection);

      System.out.println("Session created.");

      Destination destination = MyCreateDestination(session);

      System.out.println("Destination created: " + destination.toString());

      consumer = MyCreateConsumer(session, destination);

      System.out.println("Consumer created.");

      System.out.println("Waiting for messages...");

      // Now receive messages synchronously or asynchronously

      // Synchronously
      if (Options.ReceiveMode.Value().equals(Literals.Sync)) {
        // Start the connection
        connection.start();

        // Receive specified number of messages
        Message recvMsg = null;
        while (messagesInTotal.equals(Literals.Infinite)
            || (messagesReceived < Integer.parseInt(messagesInTotal))) {
          // If we want to wait until timeout, use the following call instead
          // recvMsg = consumer.receive(TIMEOUTTIME);

          // Receive the message
          recvMsg = consumer.receive();

          // Increment the counter
          messagesReceived++;

          if (recvMsg != null) {
            // Display received message
            DisplayMessage(recvMsg);
          }
          else {
            throw new Exception("Message received was null.");
          }

          // Sleep for a while to allow the user to see the output on the screen
          Thread.sleep(Options.Interval.ValueAsNumber() * 1000);

        } // end of while

      } // end synchronously

      // Asynchronously
      else if (Options.ReceiveMode.Value().equals(Literals.Async)) {
        // Create and register a new MessageListener for this consumer
        consumer.setMessageListener(new MessageListener() {

          public void onMessage(Message msg) {
            // The try block below is unlikely to throw a runtime exception, but as a general good
            // practice an asynchronous consumer's message listener or callback should catch a
            // potential runtime exception, and optionally divert the message in question to an
            // application-specific destination.
            try {
              synchronized (threadWaitLock) {
                // Increment the counter
                ++messagesReceived;

                // Display the message that just arrived
                DisplayMessage(msg);

                // Notify main thread we've received a message
                threadWaitLock.notify();

                // Sleep for a while to allow the user to see the output on the screen
                Thread.sleep(Options.Interval.ValueAsNumber() * 1000);
              }
            } // end try
            catch (Exception e) {
              System.out.println("Exception caught in onMessage():\n" + e);
              // We have atleast two choices now - (1) rethrow the exception. In this case the
              // control passes back to JMS client and which may attempt to redeliver the message,
              // depending on session's acknowledge mode, or (2) terminate the process.
              // Orthogonally, we may divert the message to an application-specific destination.

              // Terminate the current process
              System.exit(-1);
            }
            return;
          } // end onMessage()
        }); // end setMessageListener

        // Start the connection
        connection.start();

        // Finite number of messages to receive
        if (messagesInTotal != Literals.Infinite) {
          synchronized (threadWaitLock) {
            try {
              // Temporary variables
              int noProgressCount = 0;
              int countLastSeen = -1;

              int expectedNumMessages = Integer.parseInt(messagesInTotal);
              while (messagesReceived != expectedNumMessages) {
                // Wait for few seconds (or be notified by the listener thread) before checking the
                // number of messages received
                threadWaitLock.wait(waitTime);

                if (countLastSeen != messagesReceived) {
                  countLastSeen = messagesReceived;
                  noProgressCount = 0;
                }
                else {
                  ++noProgressCount;
                }

                if (++noProgressCount >= MAX_NO_PROGRESS) {
                  // No progress has been made by the listener in 5 seconds * 3 = 15 seconds.
                  // Let's quit.
                  break;
                }

              } // end while
            } // end try
            catch (InterruptedException e) {
              System.err
                  .println("Main thread waiting for MessageListener thread to receive message was interupted!"
                      + e);
              throw e;
            }
          } // end synchronized block
        } // end if
        // Infinite number of messages to receive
        else {
          // Block this thread and let listener thread do all the work (if any).
          synchronized (threadWaitLock) {
            try {
              while (true) {
                // Wait to be notified by the listener thread and then wait again!
                threadWaitLock.wait();
              }
            }
            catch (InterruptedException e) {
              System.err
                  .println("Main thread waiting for MessageListener thread to receive message was interupted!"
                      + e);
              throw e;
            }

          } // end synchronized block
        } // end else
      } // end asynchronously
    }
    catch (JMSException jmsex) {
      processJMSException(jmsex);
    }
    finally {
      if (consumer != null) {
        try {
          consumer.close();
        }
        catch (JMSException jmsex) {
          System.out.println("Consumer could not be closed.");
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
  }// end ReceiveMessages

  /**
   * Display message contents.
   */
  private void DisplayMessage(Message msg) {
    String marker = "---------------";

    // Synchronous
    if (Options.ReceiveMode.Value().equals(Literals.Sync)) {
      if (messagesInTotal.equals(Literals.Infinite)) {
        System.out.println("\n" + marker + " Message " + messagesReceived
            + " received synchronously " + marker);
      }
      else {
        System.out.println("\n" + marker + " Message " + messagesReceived + " of "
            + messagesInTotal + " received synchronously " + marker);
      }
    }

    // Asynchronous
    else if (Options.ReceiveMode.Value().equals(Literals.Async)) {
      if (messagesInTotal.equals(Literals.Infinite)) {
        System.out.println("\n" + marker + " Message " + messagesReceived
            + " received asynchronously " + marker);
      }
      else {
        System.out.println("\n" + marker + " Message " + messagesReceived + " of "
            + messagesInTotal + " received asynchronously " + marker);
      }
    }

    System.out.println(msg.toString());

    try {
      // Get values for custom properties, if available
      String property1 = msg.getStringProperty("MyStringProperty");

      // Get value for an int property, store the result in long to validate
      // the get operation.
      long property2 = ((long) Integer.MAX_VALUE) + 1;
      property2 = msg.getIntProperty("MyIntProperty");

      if ((property1 != null) && (property2 < Integer.MAX_VALUE)) {
        System.out.println("[Message has my custom properties]");
      }
      else {
        System.out.println("[Hmm... my custom properties aren't there!]");
      }
    }
    catch (Exception e) {
      // It appears that the received message was not created by the SampleProducerJava application,
      // as the expected properties are not available. This is a valid scenario, so suppress this
      // exception.
    }
    return;
  } // end DisplayMessage

  /**
   * Display help to the user.
   */
  private static void DisplayHelp() {
    System.out.println();
    System.out.println("Usage: Consumes messages from a topic/queue");
    System.out.println();
    System.out.println("  SampleConsumerJava [ < response_file ]");
    System.out.println();
    return;
  }

} // end SampleConsumerJava

