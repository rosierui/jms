//SCCSID "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=MQJavaSamples/jms/interactive/helper/IsValidType.java"
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26" 
 *   years="2008,2014" 
 *   crc="3204013458" > 
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
 * Abstract class for validity checker classes.
 */
abstract class IsValidType {

  /**
   * Sub classes to provide implementation for this.
   * 
   * @param input
   * @return boolean True if valid, false otherwise
   */
  public abstract boolean Check(String input);

  /**
   * @param input
   * @return True if not space
   */
  public boolean containsNoSpace(String input) {
    return (input.indexOf(" ") == -1);
  }
}

/**
 * Whether input is greater than or equal to zero.
 */
class IsGreaterThanOrEqualZero extends IsValidType {

  /**
   * 
   * @param input String to check
   * @return boolean True if valid, false otherwise
   */
  @Override
  public boolean Check(String input) {
    try {
      int num = Integer.parseInt(input);
      return (num >= 0);
    }
    catch (NumberFormatException nfe) {
      // No use of nfe at the moment
      return false;
    }
  }
} // end IsGreaterThanOrEqualZero

/**
 * Whether input is one word (i.e., does not contain whitespace).
 */
class IsOneWord extends IsValidType {

  /**
   * 
   * @param input String to check
   * @return boolean True if valid, false otherwise
   */
  @Override
  public boolean Check(String input) {
    return (input.indexOf(" ") == -1);
  }
} // end IsOneWord

/**
 * Whether input is a valid destination.
 */
class IsValidDestination extends IsValidType {

  /**
   * 
   * @param input String to check
   * @return boolean True if valid, false otherwise
   */
  @Override
  public boolean Check(String input) {
    if ((input != null) && containsNoSpace(input)) {
      return true;
    }
    return false;
  }
} // end IsValidDestination

/**
 * Whether input is a valid connection factory object name.
 */
class IsValidICConnFactName extends IsValidType {

  /**
   * 
   * @param input String to check
   * @return boolean True if valid, false otherwise
   */
  @Override
  public boolean Check(String input) {
    return ((input != null) && !input.equals(Options.ICDestName.Value()) && containsNoSpace(input));
  }
}

/**
 * Whether input is a valid destination object name.
 */
class IsValidICDestName extends IsValidType {

  /**
   * 
   * @param input String to check
   * @return boolean True if valid, false otherwise
   */
  @Override
  public boolean Check(String input) {
    return ((input != null) && !input.equals(Options.ICConnFactName.Value()) && containsNoSpace(input));
  }
}

/**
 * Whether input is a valid initial context URI
 */
class IsValidInitialContextURI extends IsValidType {

  /**
   * 
   * @param input String to check
   * @return boolean True if valid, false otherwise
   */
  @Override
  public boolean Check(String input) {
    // No input is valid
    if (input == null) {
      return true;
    }

    // Input with space(s) is invalid
    if (!containsNoSpace(input)) {
      return false;
    }

    // If input does not contain ://, then assume valid
    if (input.indexOf("://") == -1) {
      return true;
    }

    // Input contains ://, now check if the prefix is valid
    if (input.startsWith("file://") || input.startsWith("wsvc://") || input.startsWith("http://")
        || input.startsWith("LDAP://") || input.startsWith("cosnaming://")) {
      return true;
    }
    return false;
  }
} // end IsValidInitialContextURI

/**
 * Whether input is a valid number of messages.
 */
class IsValidNumberOfMessages extends IsValidType {

  /**
   * 
   * @param input String to check
   * @return boolean True if valid, false otherwise
   */
  @Override
  public boolean Check(String input) {
    // Infinite number of messages
    if ((input != null) && input.equals(Literals.Infinite)) {
      return true;
    }

    // Finite number of messages
    try {
      int num = Integer.parseInt(input);
      return (num >= 0);
    }
    catch (NumberFormatException nfe) {
      // No use of nfe at the moment
      return false;
    }
  }
} // end IsValidNumberOfMessages

/**
 * Whether input is a valid port number.
 */
class IsValidPortNumber extends IsValidType {

  /**
   * 
   * @param input String to check
   * @return boolean True if valid, false otherwise
   */
  @Override
  public boolean Check(String input) {
    try {
      int port = Integer.parseInt(input);
      return ((port >= 0) && (port <= 65535));
    }
    catch (NumberFormatException nfe) {
      // No use of nfe at the moment
      return false;
    }
  }
} // end IsValidPortNumber

/**
 * Whether input is a valid RTT destination.
 */
class IsValidRTTDestination extends IsValidType {

  /**
   * 
   * @param input String to check
   * @return boolean True if valid, false otherwise
   */
  @Override
  public boolean Check(String input) {
    if ((input != null) && (input.indexOf("queue://") == -1) && containsNoSpace(input)) {
      return true;
    }
    return false;
  }
} // end IsValidRTTDestination
