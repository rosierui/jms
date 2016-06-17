/*
 SimpleWMQClient

 The "Hello, World" of Java applications that talk JMS to an Websphere MQ
 queue manager.

 Please see the files build.sh and run.sh to see the JVM settings needed
 to build and run this class.

 Tested against W-MQ 7.5.0.2 (client runtime and server)

 (c)2016 Kevin Boone
 Distrubuted under the terms of the GNU Public Licence, version 3.0-- 
 You know the drill by now.
 */
package net.kevinboone.apacheintegration.simplewmgclient;

import com.ibm.msg.client.wmq.*;
import com.ibm.mq.jms.*;
import javax.jms.*;

public class SimpleWMQClient
  {
  public static void main (String[] args)
      throws Exception
    {
    // Set up the W-MQ QueueConnectionFactory
    MQQueueConnectionFactory qcf = new MQQueueConnectionFactory();

    // Host and port settings have their usual meanings
    qcf.setHostName ("192.168.0.4"); // 192.168.1.51
    qcf.setPort (1414);

    // Queue manager and channel -- the W-MQ administrator should
    //  supply these
    qcf.setQueueManager ("QMA");
    qcf.setChannel ("SYSTEM.DEF.SVRCONN");

    // Although there are many possible values of transport type,
    //  only 'client' and 'bindings' work in a Java client. Bindings
    //  is a kind of in-memory transport and only works when the client
    //  and the queue manager are on the same physical host. In most
    //  cases we need 'client'. 
    qcf.setTransportType (WMQConstants.WMQ_CM_CLIENT);

    // Create and start a connection
    // In a default setup of W-MQ, the password is not checked. The
    //   only auth check is that the user ID is an O/S user that is
    //   a member of the mqm group. 'mqm' itself is forbidden
    //   unless specifically allowed
    QueueConnection qc = qcf.createQueueConnection ("mqm", "nopassword");
    qc.start();

    // --- Everything below this line is generic JMS code ---

    // Create a queue and a session
    Queue q = new MQQueue ("QUEUE1");
    QueueSession s = qc.createQueueSession (false, Session.AUTO_ACKNOWLEDGE);

    // Create and send a TextMessage 
    QueueSender qs = s.createSender (q);
    Message m = s.createTextMessage ("Hello, World!");
    qs.send (m);

    // Create a QueueReceiver and wait for one message to be delivered
    QueueReceiver qr = s.createReceiver (q);
    Message m2 = qr.receive();

    System.out.println ("Received message: " + m); 

    s.close();
    qc.close();
    }
  }
