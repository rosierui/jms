// Library:       WebSphere MQ
// Component:     WMQ SOAP Sample Programs
// Part:          SQAxis2Axis.java
//
//    <copyright 
//    notice="lm-source-program" 
//    pids="5724-H72," 
//    years="1994,2012" 
//    crc="151696309" > 
//    Licensed Materials - Property of IBM  
//     
//    5724-H72, 
//     
//    (C) Copyright IBM Corp. 1994, 2012 All Rights Reserved.  
//     
//    US Government Users Restricted Rights - Use, duplication or  
//    disclosure restricted by GSA ADP Schedule Contract with  
//    IBM Corp.  
//    </copyright> 
  
package soap.clients;

import java.net.URL;
import soap.server.*;

/**
 * This class contains an example of how to use Axis generated proxy classes to
 * invoke a service. The proxies for the service are generated from
 * StockQuoteAxis.java which is supplied with the SOAP samples.
 */
public class SQAxis2Axis
{
  public static final String SCCSID = "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=samples/soap/java/clients/SQAxis2Axis.java";
  public static void main( String[] args )
  {
    String symbol = "XXX";
    
    // Must register WMQ transport extensions before doing SOAP/MQ
    com.ibm.mq.soap.Register.extension();
    
    try
    {
      // Use the locator to get a handle to the service on a specific WSDL Port
      // The locator class is in the proxy class StockQuoteAxisServiceLocator
      // generated when the service is deployed.
      StockQuoteAxisService locator = new StockQuoteAxisServiceLocator();
      
      StockQuoteAxis service=null;
      if (args.length == 0)
         service = locator.getSoapServerStockQuoteAxis_Wmq();
      else
         service = locator.getSoapServerStockQuoteAxis_Wmq(new java.net.URL(args[0]));
      
      // Invoke the target service and print the returned result.
      float result = service.getQuote( symbol );
      System.out.println( "Response: " + result );
    }
    catch ( Exception e )
    {
      System.out.println("\n>>> EXCEPTION WHILE RUNNING ProxyClient DEMO <<<\n");
      e.printStackTrace();
      System.exit( 2 );
    }
  }
}
