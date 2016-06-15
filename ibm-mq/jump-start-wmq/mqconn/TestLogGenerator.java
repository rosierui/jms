package mqconn;

import java.util.Random;

public class TestLogGenerator
{

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    // Generates test logs in apache format
    //172.0.0.1 - - [21/Sep/2005:23:06:37 +0100] "GET / HTTP/1.1" 404 - "-" "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8b3) Gecko/20050712 Firefox/1.0+"
    String[] ipAddresses={"192.168.28.1","192.168.128.23","192.68.78.19"};
    String[] requests={"GET / HTTP/1.1","GET /index.html HTTP/1.1","GET /logo.png HTTP/1.1"};
    String[] statusCodes={"200","201"};
    String[] userAgents={"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.6) Gecko/20060728 Firefox/1.5.0.6","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)"};
    String[] times={"[21/Sep/2005:23:06:37 +0100]","[20/Sep/2005:22:06:37 +0100]","[22/Sep/2005:21:16:37 +0100]"};
    try
    {
      
      for(int i=0;i<Integer.parseInt(args[0]);i++)
      {
      Random rnd=new Random();
      StringBuffer line=new StringBuffer();
      line.append("192.168.");
      line.append(rnd.nextInt(255));
      line.append(".");
      line.append(rnd.nextInt(255));
      line.append(" - - ");
      line.append(times[rnd.nextInt(times.length)]);
      line.append(" \"");
      line.append(requests[rnd.nextInt(requests.length)]);      
      line.append("\" ");
      line.append(statusCodes[rnd.nextInt(statusCodes.length)]);      
      line.append(" ");
      line.append(rnd.nextInt(10000));
      line.append(" \"-\" \"");
      line.append(userAgents[rnd.nextInt(userAgents.length)]);            
      line.append("\"");
      System.out.println(line.toString());
      }
      
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

  }

}
