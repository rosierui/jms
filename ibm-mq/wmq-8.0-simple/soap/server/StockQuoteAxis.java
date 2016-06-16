// Library:       WebSphere MQ
// Component:     WMQ SOAP Sample Programs
// Part:          StockQuoteAxis.java
//
//    <copyright 
//    notice="lm-source-program" 
//    pids="5724-H72," 
//    years="2005,2012" 
//    crc="2015982679" > 
//    Licensed Materials - Property of IBM  
//     
//    5724-H72, 
//     
//    (C) Copyright IBM Corp. 2005, 2012 All Rights Reserved.  
//     
//    US Government Users Restricted Rights - Use, duplication or  
//    disclosure restricted by GSA ADP Schedule Contract with  
//    IBM Corp.  
//    </copyright> 
  
package soap.server;

import java.lang.Thread;
import java.io.FileWriter;

public class StockQuoteAxis {

  public static final String SCCSID = "@(#) @(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=samples/soap/java/server/StockQuoteAxis.java";

  public float getQuote (String symbol) throws Exception 
  {
    return( (float) 55.25 );
  }

  public void getQuoteOneWay (String symbol) throws Exception {
    try {
      // Write the results for this service to a file
      FileWriter f = new FileWriter("getQuoteOneWay.txt", true);
      f.write("One way service result via proxy is: 44.44\n");
      f.close();
    } catch (Exception ee) {
      System.out.println("Error writing result file in getQuoteOneWay");
      ee.printStackTrace();
    };
  }


  public int asyncQuote( int delay)
     {
     try
     {
        Thread.sleep(delay);
     }
     catch (Exception e)
     {
        System.out.println("Exception in asyncQuote during sleep");
     }
     return delay;
     }

	public float getQuoteTran (String symbol) throws Exception 
	{
		if (symbol.equalsIgnoreCase("ROLLBACK"))
		{
			System.out.println("Rollback was requested, exiting from service by calling System.exit().");

			//NB: We exit the listener here purely to demonstrate a severe failure in the service. Real
			//    code should not of course force an exit in this way.
			System.exit(0);
		}
		return( (float) 55.25 );
	}

}
