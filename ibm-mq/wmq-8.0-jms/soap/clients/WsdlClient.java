// Library:       WebSphere MQ
// Component:     WMQ SOAP Sample Programs
// Part:          WsdlClient.java
//
//    <copyright 
//    notice="lm-source-program" 
//    pids="5724-H72," 
//    years="1994,2012" 
//    crc="1095283266" > 
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

import com.ibm.mq.soap.*;
import org.apache.axis.utils.Options;
import java.net.URL;
import javax.xml.rpc.Call;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;
import javax.xml.namespace.QName;

/**
 * This class contains an example of to invoke a service using generic Jax-RPC
 * classes.  The underlying implementation will use Axis, but this client code
 * does not need to be exposed to any Axis classes.
 */
public class WsdlClient
{
	public static final String SCCSID = "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=samples/soap/java/clients/WsdlClient.java";
	public static void main( String[] args )
	{
		String wsdlService;
		String wsdlPort;
		String namespace;
		String wsdlSource;

		String symbol="XXX";
		String s;

		try
		{
			// Setup options and register WMQ extensions
			Register.extension();
			Options opts = new Options( args );

			// Setup basic options, for .NET if -D specified
			if (opts.isFlagSet('D') != 0) 
			{
				wsdlService = "StockQuoteDotNet";
				wsdlPort = "StockQuoteDotNetSoap";
				namespace = "http://stock.samples";
				wsdlSource = "file:generated/StockQuoteDotNet_Wmq.wsdl";
			} 
			else 
			{
				wsdlService = "StockQuoteAxisService";
				wsdlPort = "soap.server.StockQuoteAxis_Wmq";
				namespace = "soap.server.StockQuoteAxis_Wmq";
				wsdlSource = "file:generated/soap.server.StockQuoteAxis_Wmq.wsdl";
			}

			// Check for wsdlPort supplied via -w option on the command line
			if (null != (s = (opts.isValueSet('w')))) wsdlPort = s;

			// Display final value of wsdlPort
			System.out.println( "start WsdlClient demo, wsdl port " +
				wsdlPort + " resolving uri to ..." );

			// Prepare a Call on a specific WSDL Port
			QName servQN = new QName( namespace, wsdlService );
			QName portQN = new QName( namespace, wsdlPort );
			Service service = ServiceFactory.newInstance().createService( new URL( wsdlSource ), servQN );
			Call call = (Call)service.createCall( portQN, "getQuote" );

			String wsdlTargetURI = call.getTargetEndpointAddress().toString();
			System.out.println( "  '" + wsdlTargetURI + "'" );

			// Invoke the target service
			Object ret = call.invoke( new Object[] { symbol} );
			System.out.println( "Response: " + ret );
		} 
		catch ( Exception e )
		{
			System.out.println( "\n>>> EXCEPTION WHILE RUNNING WsdlClient DEMO <<<\n" );
			e.printStackTrace();
			System.exit( 2 );
		}
	}

}
