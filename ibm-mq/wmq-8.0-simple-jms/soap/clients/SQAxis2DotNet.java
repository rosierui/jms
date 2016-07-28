// Library:       WebSphere MQ
// Component:     WMQ SOAP Sample Programs
// Part:          SQAxis2DotNet.java
//
//    <copyright 
//    notice="lm-source-program" 
//    pids="5724-H72," 
//    years="1994,2012" 
//    crc="4037464822" > 
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

import dotNetService.*;

/**
 * This class contains an example of how to use Axis generated proxy classes
 * to invoke a service
 * This sample program makes a request to the DotNet service providing stock
 * quotes and prints out the received response. The request is synchronous
 * and the program waits until it gets a response. The proxies required by the
 * client for the Axis service are generated from the Java file provided as
 * part of the SOAP server samples.
 */
public class SQAxis2DotNet
{
  public static final String SCCSID = "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=samples/soap/java/clients/SQAxis2DotNet.java";
  public static void main( String[] args )
  {
    String symbol = "XXX";
    
    // Must register WMQ transport extensions before doing SOAP/MQ
    com.ibm.mq.soap.Register.extension();
    
    try
    {
      // Use the locator to get a handle to the service on a specific WSDL Port
      StockQuoteDotNet locator = new StockQuoteDotNetLocator();

      StockQuoteDotNetSoap_PortType service=null;
      if (args.length == 0)
         service = locator.getStockQuoteDotNetSoap();
      else
         service = locator.getStockQuoteDotNetSoap(new java.net.URL(args[0]));          

      // Invoke the target service doc style
      float result = service.getQuoteDOC( symbol );

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
