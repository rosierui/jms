// SCCSID "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=MQJavaSamples/jms/interactive/helper/Literals.java"
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26" 
 *   years="2008,2014" 
 *   crc="3937041694" > 
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

/**
 * A set of constant literals.
 */
public final class Literals {

  /**
   * Private constructor.
   */
  private Literals() {
    // Empty
    return;
  }

  /**
   * Simple mode for samples
   */
  public static final String Simple = "simple";

  /**
   * Advanced mode for samples
   */
  public static final String Advanced = "advanced";

  /**
   * Connect to WMQ
   */
  public static final String WMQ = "wmq";

  /**
   * Localhost
   */
  public static final String LocalHost = "localhost";

  /**
   * Server-connection Channel
   */
  public static final String ServerConnectionChannel = "SYSTEM.DEF.SVRCONN";

  /**
   * Receive mode synchronous
   */
  public static final String Sync = "sync";

  /**
   * Receive mode asynchronous
   */
  public static final String Async = "async";

  /**
   * Sample topic/destination
   */
  public static final String Dest = "topic://jms/test";

  /**
   * Sample queue/destination
   */
  public static final String DestQueue = "queue:///SYSTEM.DEFAULT.LOCAL.QUEUE";

  /**
   * WMQ connection mode Client
   */
  public static final String Client = "client";

  /**
   * WMQ connection mode Bindings
   */
  public static final String Bindings = "bindings";

  /**
   * WMQ broker version Broker (v1)
   */
  public static final String Broker = "broker";

  /**
   * WMQ broker version Integrator (v2)
   */
  public static final String Integrator = "integrator";

  /**
   * Base message
   */
  public static final String Base = "base";

  /**
   * Bytes message
   */
  public static final String Bytes = "bytes";

  /**
   * Map message
   */
  public static final String Map = "map";

  /**
   * Object message
   */
  public static final String Object = "object";

  /**
   * Stream message
   */
  public static final String Stream = "stream";

  /**
   * Text message
   */
  public static final String Text = "text";

  /**
   * Delivery mode as app
   */
  public static final String AsApp = "as-app";

  /**
   * Delivery mode as destination
   */
  public static final String AsDest = "as-dest";

  /**
   * Delivery mode non-persistent
   */
  public static final String NonPersistent = "non-persistent";

  /**
   * Delivery mode persistent
   */
  public static final String Persistent = "persistent";

  /**
   * Infinite number of messages
   */
  public static final String Infinite = "infinite";

  /**
   * Connection factory object name in initial context
   */
  public static final String SampleCF = "sampleConnectionFactory";

  /**
   * Destination object name in initial context
   */
  public static final String SampleDest = "sampleDestination";

  /**
   * Allow WMQ JMS client to connect to any permitted version of queue manager
   */
  public static final String ProviderVersionUnspecified = "unspecified";

  /**
   * Insist WMQ JMS client to connect to a v6 (or earlier) queue manager
   */
  public static final String ProviderVersion6 = "6.0.0.0";

  /**
   * Insist WMQ JMS client to connect to a v7 (or newer) queue manager
   */
  public static final String ProviderVersion7 = "7.0.0.0";
} // end Literals
