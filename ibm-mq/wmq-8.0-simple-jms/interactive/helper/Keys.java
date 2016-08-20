// SCCSID "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=MQJavaSamples/jms/interactive/helper/Keys.java"
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26" 
 *   years="2008,2014" 
 *   crc="2595392295" > 
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
 * A set of keys that define options for the sample applications.
 */
final class Keys {

  // Private constructor.
  private Keys() {
    // Empty
    return;
  }

  /**
   * Samples Mode
   */
  public static final Keys SamplesMode = new Keys();

  /**
   * Connection Type
   */
  public static final Keys ConnectionType = new Keys();

  /**
   * Hostname for WMQ
   */
  public static final Keys HostnameWMQ = new Keys();

  /**
   * Port for WMQ
   */
  public static final Keys PortWMQ = new Keys();

  /**
   * Channel
   */
  public static final Keys Channel = new Keys();

  /**
   * Connection Mode
   */
  public static final Keys ConnectionMode = new Keys();

  /**
   * Queue Manager
   */
  public static final Keys QueueManager = new Keys();

  /**
   * Provider Version
   */
  public static final Keys ProviderVersion = new Keys();

  /**
   * Broker Version
   */
  public static final Keys BrokerVersion = new Keys();

  /**
   * Broker Publish Queue
   */
  public static final Keys BrokerPublishQueue = new Keys();

  /**
   * Destination for WMQ
   */
  public static final Keys DestinationWMQ = new Keys();

  /**
   * UserID
   */
  public static final Keys UserID = new Keys();

  /**
   * Password
   */
  public static final Keys Password = new Keys();

  /**
   * Number of Messages
   */
  public static final Keys NumberOfMessages = new Keys();

  /**
   * Receive Mode
   */
  public static final Keys ReceiveMode = new Keys();

  /**
   * Delivery Mode for WMQ
   */
  public static final Keys DeliveryModeWMQ = new Keys();

  /**
   * Selector
   */
  public static final Keys Selector = new Keys();

  /**
   * Message Text
   */
  public static final Keys MessageText = new Keys();

  /**
   * Message Type
   */
  public static final Keys MessageType = new Keys();

  /**
   * Interval
   */
  public static final Keys Interval = new Keys();

  /**
   * Initial Context URI
   */
  public static final Keys InitialContextURI = new Keys();

  /**
   * Initial Context Factory Name
   */
  public static final Keys ICConnFactName = new Keys();

  /**
   * Initial Context Destination Name
   */
  public static final Keys ICDestName = new Keys();
}
