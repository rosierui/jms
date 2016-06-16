// SCCSID "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=MQJavaSamples/jms/interactive/helper/OptionsPresenter.java"
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26" 
 *   years="2008,2014" 
 *   crc="3081203984" > 
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

import com.ibm.msg.client.wmq.WMQConstants;

/**
 * Present options to the user.
 */
public class OptionsPresenter {

  /**
   * Context in which current options are presented.
   */
  private MyContext context;

  /**
   * Constructor.
   * 
   * @param ctx Initial Context
   */
  public OptionsPresenter(MyContext ctx) {
    context = ctx;

    System.out.println();
    System.out.println("Enter '?' for help with any question.");
    System.out.println();

    // Simple or advanced mode?
    Options.SamplesMode.Present();

    // Connection type
    Options.ConnectionType.Present();

    // All other options
    if (Options.SamplesMode.Value().equals(Literals.Simple)) {
      if (Options.ConnectionType.Value().equals(Literals.WMQ)) {
        // Present minimal WMQ options
        PresentWMQOptions();
      }
    }
    else if (Options.SamplesMode.Value().equals(Literals.Advanced)) {
      if (Options.ConnectionType.Value().equals(Literals.WMQ)) {
        // Present full WMQ options
        PresentFullWMQOptions();
      }
    } // end all other options
    return;
  } // end constructor


  /**
   * Present minimal WMQ options.
   */
  private void PresentWMQOptions() {
    Options.ConnectionMode.Present();

    Options.QueueManager.Present();

    if ((Options.ConnectionMode.ValueAsNumber() == WMQConstants.WMQ_CM_CLIENT)) {
      Options.HostnameWMQ.Present();
    }

    Options.DestinationWMQ.Present();

    Options.ProviderVersion.Present();

    if (!Options.DestinationWMQ.IsNull() && Options.DestinationWMQ.Value().startsWith("topic://")) {

      // Broker version is only applicable for ProviderVersion unspecified or v6
      if (Options.ProviderVersion.Value().equals(Literals.ProviderVersionUnspecified)
          || Options.ProviderVersion.Value().equals(Literals.ProviderVersion6)) {
        Options.BrokerVersion.Present();

        if ((Options.BrokerVersion.ValueAsNumber() == WMQConstants.WMQ_BROKER_V2)
            && (InContext(MyContext.Producer))) {
          // Must get broker publish queue name from the user
          Options.BrokerPublishQueue.Present(true);
        }
      }
    }

    if (InContext(MyContext.Producer)) {
      Options.MessageText.Present();
    }
    return;
  } // end PresentWMQOptions

  /**
   * Present full WMQ options.
   */
  private void PresentFullWMQOptions() {
    Options.ConnectionMode.Present();

    Options.QueueManager.Present();

    if ((Options.ConnectionMode.ValueAsNumber() == WMQConstants.WMQ_CM_CLIENT)) {
      Options.HostnameWMQ.Present();

      Options.PortWMQ.Present();

      Options.Channel.Present();
    }

    Options.DestinationWMQ.Present();

    Options.ProviderVersion.Present();

    if (!Options.DestinationWMQ.IsNull() && Options.DestinationWMQ.Value().startsWith("topic://")) {

      // Broker version is only applicable for ProviderVersion unspecified or v6
      if (Options.ProviderVersion.Value().equals(Literals.ProviderVersionUnspecified)
          || Options.ProviderVersion.Value().equals(Literals.ProviderVersion6)) {
        Options.BrokerVersion.Present();

        if ((Options.BrokerVersion.ValueAsNumber() == WMQConstants.WMQ_BROKER_V2)
            && (InContext(MyContext.Producer))) {
          // Must get broker publish queue name from the user
          Options.BrokerPublishQueue.Present(true);
        }
      }
    }

    Options.UserID.Present();

    if (!Options.UserID.IsNull()) {
      Options.Password.Present();
    }

    if (InContext(MyContext.Producer)) {
      Options.DeliveryModeWMQ.Present();
    }

    if (InContext(MyContext.Producer)) {
      Options.MessageType.Present();

      // User defined message text is not applicable if message type is base
      if (!Options.MessageType.Value().equals(Literals.Base)) {
        Options.MessageText.Present();
      }
    }

    if (InContext(MyContext.Consumer)) {
      Options.ReceiveMode.Present();

      Options.Selector.Present();
    }

    // Don't ask for the number of messages for WMQ async consumers as we cannot fully control the
    // number of messages received in this mode
    if (InContext(MyContext.Producer) || !(Options.ReceiveMode.Value().equals(Literals.Async))) {
      Options.NumberOfMessages.Present();
    }

    Options.Interval.Present();
    return;
  } // end PresentFullWMQOptions

  /**
   * Whether the argument context is same as current context.
   */
  private boolean InContext(MyContext x) {
    return (context == x);
  }

} // end OptionsPresenter

