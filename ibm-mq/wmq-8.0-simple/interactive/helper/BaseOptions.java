//SCCSID "@(#) MQMBID sn=p800-004-151022.DE su=_8QwZKXivEeWg74sVC8pxOw pn=MQJavaSamples/jms/interactive/helper/BaseOptions.java"
/*
 *   <copyright 
 *   notice="lm-source-program" 
 *   pids="5724-H72,5655-R36,5655-L82,5724-L26" 
 *   years="2008,2014" 
 *   crc="3240883704" > 
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

/**
 * An abstract class that can be extended to provide user option(s) functionality.
 */
abstract class BaseOptions {

  /**
   * Input messages for options.
   */
  protected static Hashtable<Keys, String> inputMessage;

  /**
   * Help messages for options.
   */
  protected static Hashtable<Keys, String[]> helpMessage;

  /**
   * Default values for options.
   */
  protected static Hashtable<Keys, Object> defaultValue;

  /**
   * Domain restriction for options. Domain restriction is a set of valid values for an option.
   */
  protected static Hashtable<Keys, String[]> domainRestriction;

  /**
   * Validity checker methods for options. Assume at most one checker method per option.
   */
  protected static Hashtable<Keys, IsValidType> isValid;

  /**
   * Map user input to some value.
   */
  protected static Hashtable<Keys, Hashtable<String, Integer>> nameToValue;

  /**
   * User responses received so far.
   */
  private static String userResponses = "";

  /**
   * Console read.
   */
  private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

  /**
   * Key used to identify this option.
   */
  private Keys key;

  /**
   * String value for this option.
   */
  private String val;

  /**
   * Whether the value for this option was manually/programmatically set (i.e., without user input).
   */
  private boolean manuallySet;

  /**
   * Constructor.
   */
  protected BaseOptions(Keys key) {
    // The key for this option
    this.key = key;

    // Set the default value, if any, for this option
    val = DefaultValue();

    // This is a newly created option, so it hasn't been manually set
    manuallySet = false;
    return;
  } // end BaseOptions

  /**
   * Override String representation.
   * 
   * @return String
   */
  public String ToString() {
    return val;
  }

  /**
   * Whether the value for this option is null or non-null.
   * 
   * @return boolean
   */
  public boolean IsNull() {
    return (val == null);
  }

  /**
   * Present this option to the user, read the input, possibly set a default value and validate. The
   * option may be presented as a mandatory option or as a non-mandatory option.
   * 
   * @param mandatory Whether this is a mandatory option
   */
  public void Present(boolean mandatory) {
    // Check if the option has been already set manually
    if (manuallySet) {
      // This option has been set manually, there is no need to present it to the user
      return;
    }

    // User input as String
    String input = null;

    // Display input message
    DisplayInputMessage(mandatory);

    try {
      // Console read
      input = reader.readLine(); // TODO: need to close reader somehow after all options have
      // been presented
    }
    catch (IOException ioex) {
      System.out.println("Error: Unable to read input from standard input.");
      ioex.printStackTrace();
    }

    // If needed, set a default value
    if (input.length() == 0) {
      input = DefaultValue();
    }

    // Does user need help?
    if ((input != null) && input.trim().equals("?")) {
      // Display help message
      DisplayHelpMessage();

      // And recurse...
      Present(mandatory);
    } else {
      // Validate read
      if ((input == null) && (mandatory == true)) {
        // Invalid input, display missing value message
        DisplayMissingValueMessage();

        // And recurse...
        Present(mandatory);
      } else if (!IsAllowedValue(input)) {
        // Invalid input, display invalid value message
        DisplayInvalidValueMessage();

        // And recurse...
        Present(mandatory);
      } else {
        // Valid input
        // Set the value, after removing whitespace at the start
        // and end of the input String
        val = (input != null) ? input.trim() : null;

        // Concatenate user response with responses so far
        userResponses += (input != null) ? (input.trim() + "\n") : "\n";
      }
    }

    return;
  } // end Present

  /**
   * Present this option as a non-mandatory option to the user.
   */
  public void Present() {
    Present(false);
    return;
  }

  /**
   * Display input message for an option.
   * 
   * @param mandatory Whether this is a mandatory option
   */
  private void DisplayInputMessage(boolean mandatory) {
    String msg;

    if (DefaultValue() == null) {
      msg = (mandatory) ? InputMessage() + " (mandatory): " : InputMessage() + " (optional): ";
    } else {
      msg = InputMessage() + " [" + DefaultValue() + "]: ";
    }

    System.out.print(msg);
    return;
  } // end DisplayInputMessage

  /**
   * Display help message for an option.
   */
  private void DisplayHelpMessage() {
    String[] lines = HelpMessage();
    for (int i = 0; i < lines.length; i++) {
      System.out.print((i == 0) ? " - " : "   ");
      System.out.println(lines[i]);
    }

    DisplayInfoMessage();
    return;
  } // end DisplayHelpMessage

  /**
   * Display a missing value message.
   */
  private void DisplayMissingValueMessage() {
    System.out.println(" - A value must be specified for this option.");
    System.out.println();
    return;
  }

  /**
   * Display an invalid value message.
   */
  private void DisplayInvalidValueMessage() {
    System.out.println(" - Incorrect value for this option.");

    DisplayInfoMessage();
    return;
  }

  /**
   * Display additional information message for this option.
   */
  private void DisplayInfoMessage() {
    if (DomainRestriction() != null) {
      System.out.print(" - Valid values are: ");
      System.out.println(ListOfValidValues() + ".");
    }

    if (DefaultValue() != null) {
      System.out.print(" - Default value is: ");
      System.out.println(DefaultValue() + ".");
    }

    System.out.println();
    return;
  } // end DisplayInfoMessage

  /**
   * Create a list of valid values for this option. Returns a list of valid values as String.
   */
  private String ListOfValidValues() {
    // Early return
    if (DomainRestriction() == null) {
      return null;
    }

    StringBuffer sb = new StringBuffer();

    String[] allowedValues = DomainRestriction();

    for (int i = 0; i < allowedValues.length; i++) {
      String allowed = allowedValues[i];
      if (i == allowedValues.length - 1) // last element
      {
        sb.append(" or ");
      } else if (i != 0) { // any but first element
        sb.append(", ");
      }
      sb.append(allowed);
    }

    return sb.toString();
  } // end ListOfValidValues

  /**
   * Determine whether the input value is an allowed value.
   * 
   * @param input Input value to validate
   * @return boolean result
   */
  private boolean IsAllowedValue(String input) {
    // If input is null, then assume it is valid
    if (input == null) {
      return true;
    }

    // If there is a reference to validity checker method
    if (isValid.containsKey(key)) {
      return IsValid(input);
    }

    // Check if there is no domain restriction
    if (DomainRestriction() == null) {
      // No restriction implies that input value is in domain
      return true;
    }

    // Now, check if the value is within domain
    String[] allowedValues = DomainRestriction();
    for (int i = 0; i < allowedValues.length; i++) {
      if (input.equals(allowedValues[i])) {
        return true;
      }
    }
    return false;
  } // end IsAllowedValue

  /**
   * Read-only property with concatenated user reponses received so far.
   * 
   * @return String of user responses
   */
  public static String UserResponses() {
    return userResponses;
  }

  /**
   * Read-Write property for option value.
   * 
   * @return String of option
   */
  public String Value() {
    // Option holds an alias to the value, so lookup the value
    if (nameToValue.containsKey(key)) {
      return nameToValue.get(key).get(val).toString();
    }
    return val;
  } // end get_Value

  /**
   * Manually set a value for this option. Only non-null values are permitted.
   * 
   * @param value to set
   */
  public void set_Value(String value) {
    // Only allow to set non-null values
    if (value != null) {
      val = value;
      manuallySet = true;
    }
  } // end set_Value

  /**
   * Read-only property for option value as 32-bit signed integer.
   * 
   * @return int
   */
  public int ValueAsNumber() {
    return Integer.parseInt(Value());
  }

  /**
   * Read-only property for input message for the option.
   * 
   * @return
   */
  private String InputMessage() {
    return inputMessage.get(key);
  }

  /**
   * Read-only property for help message for the option.
   * 
   * @return
   */
  private String[] HelpMessage() {
    return helpMessage.get(key);
  }

  /**
   * Read-only property for default value for the option.
   * 
   * @return
   */
  private String DefaultValue() {
    return (defaultValue.get(key) != null) ? defaultValue.get(key).toString() : null;
  }

  /**
   * Read-only property for domain restriction for the option.
   * 
   * @return
   */
  private String[] DomainRestriction() {
    return domainRestriction.get(key);
  }

  /**
   * Read-only property for reference to validity checker for the option.
   */
  private boolean IsValid(String input) {
    return isValid.get(key).Check(input);
  }

} // end BaseOptions
