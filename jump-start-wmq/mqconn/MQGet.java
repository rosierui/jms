package mqconn;

import com.ibm.mq.MQException;

public class MQGet extends MQConnector {
  public MQGet() {
  }

  public String getMessages(String[] args) throws MQException {
   String message=getMessageFromQueue();
   return message;
  }

  public static void main(String[] args) {
    MQGet mqget = new MQGet();
    MQConnector.DEBUG=false;
    try {
      mqget.initMq();
      mqget.openQueue();

      for(String msg=mqget.getMessages(args);msg!=null;msg=mqget.getMessages(args)) {
         System.out.println(msg);
      }

      mqget.closeQueue();
      mqget.disconnectMq();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println("Usage: "+mqget.getClass().getName()+" ");
    }
  }
}
