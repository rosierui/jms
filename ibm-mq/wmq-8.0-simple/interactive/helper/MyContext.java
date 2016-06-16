//SCCSID "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=MQJavaSamples/jms/interactive/helper/MyContext.java"
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26" 
 *   years="2008,2014" 
 *   crc="504206978" > 
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
 * The context in which options are presented.
 */
public final class MyContext {

  private String id;

  /**
   * Private constructor.
   * 
   * @param String for name
   */
  private MyContext(String name) {
    id = name;
    return;
  }

  /**
   * Overridden toString()
   * 
   * @return ID
   */
  @Override
  public String toString() {
    return id;
  }

  /**
   * For SampleProducer application
   */
  public final static MyContext Producer = new MyContext("Producer");

  /**
   * For SampleConsumer application
   */
  public final static MyContext Consumer = new MyContext("Consumer");
} // end Context

