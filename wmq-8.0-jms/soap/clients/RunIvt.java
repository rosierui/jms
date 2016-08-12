// Library:       WebSphere MQ
// Component:     WMQ SOAP Sample Programs
// Part:          RunIvt.java
//
//    <copyright 
//    notice="lm-source-program" 
//    pids="5724-H72," 
//    years="1994,2012" 
//    crc="427770486" > 
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

import java.io.*;
import java.util.Vector;   // For list of failed tests

import com.ibm.mq.*;

/**
 * This class drives the SOAP IVT process. It determines what tests are to be
 * run. It reads the file containing the details of the test and then performs
 * the test. At the end it prints the results of the test run.
 * The format of the tests in the test file is as follows:
 *    Name of test
 *    Description of test
 *    Expected response
 *    Environment (Java or DotNet)
 *     
 */

public class RunIvt {

  public static final String SCCSID = "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=samples/soap/java/clients/RunIvt.java";
  private static String START = "amqwstartwin.cmd";
  private static final String CONFIGFILE = "ivttests.txt";
  private static final String LINUXCONFIGFILE = "ivttests_unix.txt";

  FileReader file;
  BufferedReader buff;
  private String cmd;       // cmd to run for test
  private String task;      // name of test
  private String response;  // expected response
  private String desc;      // description
  private String requires;  // list of prereq servers for test
  private boolean usingJMSax=false; // running Java tests if true
  private boolean usingDotNet=false; // running Dotnet tests if true
  private boolean hold = false;  // true to prevent killing SOAP listeners
  private boolean killall = false; // true to kill ALL listeners
  private String configFile=null; // name of file with tests
  private Vector failedTests=null; // holds list of failed tests
  private boolean verbose = false; // give out all error information (such as stack dumps) if true

  int testsRan = 0;
  int testsOK = 0;

// Arguments to the program are:
// "killall"  - kill all listeners
// "hold"  - hold all listeners at the end of the run.
// "-c configfile" - name of test file to use (if different to default)
 
  RunIvt(String argv[])
  {
	  System.out.println("\n\n");

    // Set up START with the script to run to execute a command
    // On windows the 'start' command is used and on unix 'xterm' is used.
    if (onWindows())
      START="amqwstartwin.cmd";
    else
      START="amqwstartwin.sh";

    // Set up the required MQ bits - queue manager, MQ listener etc.
    SetupMQ();

    failedTests = new Vector();

    // Process argument list

    int numArgs = argv.length;
    for (int i=0; i < argv.length; i++)
    {
      if (argv[i].equalsIgnoreCase("killall"))
      {
        killall=true;
        numArgs--;
      }

      if (argv[i].equalsIgnoreCase("hold"))
      {
        hold=true;
        numArgs--;
      }

      // Look for -c <config-file>
      if (argv[i].equals("-c"))
      {
        // Next argument, if present, if the test file to use
        if (argv.length > i+1)
        {
          configFile = argv[++i];  
          System.out.println("Using test configuration file: " + configFile);
          numArgs--;
        }
        numArgs--;
      }

	  // Look for "-v" flag, if specified causes all error information to be output
	  if (argv[i].equals("-v"))
	  {
		  verbose = true;
		  numArgs--;
	  }	  
    
	}

    //Run the tests

    // Read the file containing the tests
    OpenConfig();

    // For all tests in the config file, read the next test, and if it has been
    // specified to be run, print a description of the test, initialise the
    // environment if this is the first test for that environment (Axis or
    // DotNet) and then run the test. If the test fails, update the failed
    // tests array.
    while (GetaTest())
    {
      // Do we run this test? (No args means run all tests)
      if (numArgs == 0 || testSpecified(argv, task))
      {
        head(task, desc);
        startRequired();
        testsRan++;
        if (!runTest(cmd, response))
          failedTests.add(task);
      }
    }
    // We are done with the test file so close it.
    CloseConfig();

    System.out.println("----------------------------------\n");

    // Stop the listener(s) unless "hold" specified
    stopRequired();

    // Print the results of the test run.
    System.out.println("===========================================");
    System.out.println(testsRan + " tests run, of which " + (testsRan-testsOK) + " failed.\n\n");

    // Print deatils of failed tests, if any
    if (!failedTests.isEmpty())
       {
       System.out.println("FAILED TESTS: ");
       System.out.println("=============\n");
       for (int i=0; i < failedTests.size(); i++)
         {
         String t = (String) failedTests.get(i);
         System.out.println("  " + t);
         }
       }
  }

  private boolean testSpecified(String argv[], String aTest)
  {

    // Go through the arg list to check if this test matches what is
    // specified .
    for (int i=0; i< argv.length; i++)
    {
      if (aTest.equalsIgnoreCase(argv[i]))
        return true;
    }
    return false;
  }

  private void startRequired()
  {
    // Initialise the required environment for the test (Axis or DotNet).
    requires = requires.toLowerCase();
    if (0 <= requires.indexOf("jmsax"))
      useJMSax();
    if (0 <= requires.indexOf("dotnet"))
      useDotNet();
  }

  private void stopRequired()
  {
    // Kill the SOAP listeners unless 'hold' was specified.
    if (hold)
      return;

    killListener();
  }

  private void OpenConfig()
  {
    // Open the file with the tests. This is either the file supplied with the
    // SOAP samples or the file specified on the command line.

    try
    {
      if (configFile == null)
      {
        if (onWindows())
          configFile = CONFIGFILE;
        else
          configFile = LINUXCONFIGFILE;
      }

      file = new FileReader(configFile);

      buff = new BufferedReader(file);
    } catch (IOException e)
    {
      System.out.println("ERROR: " + e.toString());
    }
  }

  private void CloseConfig()
  {
    // Close the file with the tests.

    try
    {
      buff.close();
    } catch (IOException e)
    {
      System.out.println("ERROR: " + e.toString());
    }
  }

  private String GetConfigLine()
  {
    // Read the next line of the test in the test file and return it to the
    // caller. Ignore any comments (lines starting with a '#') and any blank
    // lines.
    try
    {
      boolean eof=false;
      while (!eof)
      {
        String line = buff.readLine();
        if (line == null)
          eof = true;
        else
        {
          line.trim();
          if (0 == line.length())
            continue;
          if (line.startsWith("#"))
            continue;
          return line;
        }
      }
    } catch (IOException e)
    {
      System.out.println("ERROR: " + e.toString());
    }

    return null;
  }

  private boolean GetaTest()
  {
    // Read the next test and set the test parameters

    if (null == (task     = GetConfigLine()))
      return false;
    if (null == (desc     = GetConfigLine()))
      return false;
    if (null == (cmd      = GetConfigLine()))
      return false;
    if (null == (response = GetConfigLine()))
      return false;
    if (null == (requires = GetConfigLine()))
      return false;
    return true;
  }

  private void head(String title, String desc)
  {
    // Print a description of the test
    System.out.println("\n----- [" + title + "] --------------------------------");
    System.out.println(desc);
  }

  private void SetupMQ()
  {
   // Setup the required WMQ enviroment by calling the supplied setup script.
   // The queue manager is created and started and the required channels and
   // queues are defined.
   if (onWindows())
      CmdExec("setupWMQSOAP.cmd");
    else
      CmdExec("setupWMQSOAP.sh");
  }

  private void useJMSax()
  {
   // Initialise the Java/Axis environment. This currently is starting up the
   // SOAP Java listener by calling the start script generated by the
   // deployment process. This is only done once when the first java test is run.
    if (!usingJMSax)
    {
      if (!javaListenerRunning())
      {
        if (onWindows())
        {
          StartWindow("generated\\server\\startWMQJListener.cmd", "SimpleJavaListener");
        }
        else
        {
          StartWindow("./generated/server/startWMQJListener.sh", "SimpleJavaListener");
        }
      }
      usingJMSax=true;
    }
  }

  private void killListener()
  {

    // Stop the SOAP Java and/or SOAP DotNet listeners. This is done by calling
    // the end script generated by the deployment process.
    if (usingJMSax || killall)
    {
      if (onWindows())
        CmdExec("generated\\server\\endWMQJListener.cmd");
      else
        CmdExec("generated/server/endWMQJListener.sh");

      usingJMSax=false;
    }

    if (usingDotNet || killall)
    {
      CmdExec("generated\\server\\endWMQNListener.cmd");
      usingDotNet=false;
    }
  }

  private void useDotNet()
  {
   // Initialise the DotNet environment. This currently is starting up the
   // SOAP DotNet listener. This is done by calling the start script generated
   // by the deployment process. This is only done once when the first DotNet
   // test is run.
  if (!usingDotNet)
    {
      if (!dotNetListenerRunning())
      {
        String STARTSERVER="generated\\server\\startWMQNListener.cmd";
        StartWindow(STARTSERVER, "MQSoapListener");
      }
      usingDotNet=true; 
    }
  }

  private void StartWindow(String command, String winTitle)
  {
   // Execute the 'command' in a new window.
    System.out.println("+++ server: " + command);
    CmdExec(START + " " + winTitle + " " + command);
  }

  private boolean runTest(String aDemo, String expected)
  {
    // Run a single test and check whether is passes or fails.
    // Print the deatiled results if the test fails.
    String command = aDemo;
    System.out.println("--- client: " + command);
    System.out.println("");
    String actual = CmdExec(command);
    if (! actual.endsWith(expected)) {
      System.out.println("\n************** TEST FAILED !! TEST FAILED !! TEST FAILED *************");
      System.out.println(  "EXPECTED: " + expected);
      System.out.println(  "ACTUAL:   " + actual);
      System.out.println(  "**********************************************************************\n");
      return false;
    } else {
      System.out.println("OK.");
      testsOK++;
    }
    return true;
  }


  private boolean StillRunning(Process p)
  {
    // Check if the specified process is still running - ie waiting for a
    // response and return the result.

    try
    {
      int x = p.exitValue();
      return false;
    } catch (Exception err)
    {
      return true;
    }
  }

  private String CmdExec(String cmdline) {
    // Execute the command line (ie the test itself) and wit for a response or
    // en error. Return the last response from the process or an error.
    String line, lastline;
    lastline="";
    boolean done=false;
	boolean errorState=false;

    try {
      Runtime myrun = Runtime.getRuntime();
      Process p = myrun.exec(cmdline);

      BufferedReader input = 
        new BufferedReader
        (new InputStreamReader(p.getInputStream())); 

      BufferedReader error = 
        new BufferedReader
        (new InputStreamReader(p.getErrorStream())); 

      while (!done && StillRunning(p))
      {
        Thread.sleep(50);
        while (!done && (input.ready() || error.ready()))
        {
          if (input.ready())
          {
            line = input.readLine();
            if (line.startsWith("Exiting"))
              done=true;
		  if (line.startsWith(">>> EXCEPTION"))
		  {
			  done = true;
			  errorState = true;
		  }
            if (line.startsWith("LOG: Attempting download of new URL file:"))
              done=true;
            System.out.println(line);
            if (!done)
              lastline=line;
          }

          if (error.ready())
          {
            line = error.readLine();
			System.out.println(line);
            if (line.startsWith("Exception in thread"))
            {
              lastline=line;
			  errorState = true;
              done=true;
            }
          }
        }
      }

	  // If we detected thst the client failed then go and get any remaining output
	  if (verbose && errorState)
	  {
		  while ( input.ready() || error.ready() )
		  {
			  if (input.ready())
			  {
				  line = input.readLine();
				  System.out.println(line);
			  }

			  if (error.ready())
			  {
				  line = error.readLine();
				  System.out.println(line);
			  }
		  }
	  }

      input.close();
      error.close();

    } catch (Exception err) {
      System.out.println("Failed to execute process: " + cmdline);
      err.printStackTrace();
    }

    return lastline;
  }

  private boolean onWindows2K()
  {
    if (System.getProperty("os.name").equals("Windows 2000"))
      return true;
    return false;
  }

  private boolean onWindows()
  {
    if (System.getProperty("os.name").toLowerCase().startsWith("win"))
      return true;
    return false;
  }

  private boolean javaListenerRunning()
  {
    // Checks the queue SOAPJ.demos on qMgr WMQSOAP.DEMO.QM to see if the
    // listener is already running
    return listenerRunning("SOAPJ.demos");
  }

  private boolean dotNetListenerRunning()
  {
    // Checks the queue SOAPN.demos on qMgr WMQSOAP.DEMO.QM to see if the
    // listener is already running
    return listenerRunning("SOAPN.demos");
  }

  private boolean listenerRunning(String qName)
  {
    boolean status = false;

    // Checks the "open for input" count on queue qName on qMgr WMQSOAP.DEMO.QM
    try
    {
      MQQueueManager qm = new MQQueueManager("WMQSOAP.DEMO.QM");
      MQQueue q = qm.accessQueue(qName, MQC.MQOO_INQUIRE, null, null, null);
      if (q.getOpenInputCount() == 0)
        status = false; //Listener not running
      else
        status = true; //Listener running
      q.close();
      qm.disconnect();
    }
    catch (MQException e)
    {
      status = false; //Listener not running
    }
    return status;
  }

  public static void main(String argv[]) {
    new RunIvt(argv);
  }
}
