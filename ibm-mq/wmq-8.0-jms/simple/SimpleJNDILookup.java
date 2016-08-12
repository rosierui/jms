// SCCSID "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=MQJavaSamples/jms/simple/SimpleJNDILookup.java"
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26" 
 *   years="2008,2014" 
 *   crc="824784486" > 
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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsDestination;

/**
 * A minimal and simple application that demonstrates lookup of JMS objects using the initial
 * context. No connection to the queue manager is made and no messages are sent or received.
 * 
 * Application makes use of fixed literals, any customisations will require re-compilation of this
 * source file. Application assumes that the named queue is empty prior to a run.
 * 
 * It is assumed that the initial context contains the named definitions for connection factory and
 * destination objects. To create such an initial context (on the file system), launch the JMSAdmin
 * tool and issue the following commands:
 * 
 * InitCtx> DEF QCF(myQCF)
 * 
 * InitCtx> DEF Q(myQueue) QUEUE(Q1)
 * 
 * The first command defines a connection factory object with the name 'myQCF'. The second command
 * defines a destination object with the name 'myQueue' that maps to queue:///Q1.
 * 
 * Notes:
 * 
 * API type: IBM JMS API (v1.1, unified domain)
 * 
 * Messaging domain: Point-to-point or Publish-Subscribe
 * 
 * Provider type: WebSphere MQ
 * 
 * Connection mode: Client connection or bindings connection
 * 
 * JNDI in use: Yes
 * 
 */
public class SimpleJNDILookup {

  // System exit status value (assume unset value to be 1)
  private static int status = 1;

  /**
   * Main method
   * 
   * @param args
   */
  public static void main(String[] args) {

    try {
      // Instantiate the initial context
      String contextFactory = "com.sun.jndi.fscontext.RefFSContextFactory";
      Hashtable<String, String> environment = new Hashtable<String, String>();
      environment.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
      environment.put(Context.PROVIDER_URL, "file:/C:/JNDI-Directory");
      Context context = new InitialDirContext(environment);
      System.out.println("Initial context found!");

      // Lookup the connection factory
      JmsConnectionFactory cf = (JmsConnectionFactory) context.lookup("myQCF");
      System.out.println("Connection factory looked up: \n" + cf);

      // Lookup the destination
      JmsDestination destination = (JmsDestination) context.lookup("myQueue");
      System.out.println("Destination looked up: \n" + destination);

      recordSuccess();
    }
    catch (NamingException ne) {
      System.out.println("The initial context could not be instantiated, or the lookup failed.");
      recordFailure(ne);
    }
    System.exit(status);
    return;
  } // end main()

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
      System.out.println(ex);
    }
    System.out.println("FAILURE");
    status = -1;
    return;
  }
}
