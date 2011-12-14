package edu.hawaii.halealohacli.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.xml.datatype.XMLGregorianCalendar;
import org.wattdepot.client.BadXmlException;
import org.wattdepot.client.WattDepotClient;
import org.wattdepot.client.WattDepotClientException;
import org.wattdepot.resource.sensordata.jaxb.SensorData;
import org.wattdepot.util.tstamp.Tstamp;

/**
 * Implement the monitor-goal Command interface. <br><br>
 * 
 * Usage: monitor-goal [tower | lounge] goal interval <br>
 * Prints out a timestamp, the current power being consumed by the [tower | lounge], and whether or
 * not the lounge is meeting its power conservation goal." <br>
 * 
 * Note: <br>
 * Towers are: Mokihana, Ilima, Lehua, Lokelani <br>
 * Lounges are the tower names followed by a "-" followed by one of A, B, C, D, E. For example,
 * Mokihana-A.
 * 
 * @author Terrence Chida
 * 
 */
public class MonitorGoal implements Command {
  private String commandString;
  private String commandSyntax;
  private String commandDescription;
  private WattDepotClient wattDepotClient;

  /**
   * Default constructor.
   * 
   * @param client WattDepotClient to be used by this command
   * @see org.wattdepot.client.WattDepotClient
   */
  public MonitorGoal(WattDepotClient client) {
    commandString = "monitor-goal";
    commandSyntax = commandString + " [tower | lounge] goal interval";
    commandDescription =
        "Prints out a timestamp, the current power being consumed by the [tower | lounge], and"
            + "whether or not the lounge is meeting its power conservation goal.";
    wattDepotClient = client;
  }

  /**
   * Return the command String.
   * 
   * @return command String
   */
  @Override
  public String toString() {
    return commandString;
  }

  /**
   * Return the command syntax String.
   * 
   * @return syntax String
   * @see edu.hawaii.halealohacli.command.Command#getSyntax()
   */
  @Override
  public String getSyntax() {
    return commandSyntax;
  }

  /**
   * Return the command description String.
   * 
   * @return description String
   * @see edu.hawaii.halealohacli.command.Command#getDescription()
   */
  @Override
  public String getDescription() {
    return commandDescription;
  }

  /**
   * Execute the command given the list of arguments.
   * 
   * @param args List of String arguments
   * @throws InvalidArgumentException if any of the arguments is invalid
   * @see edu.hawaii.halealohacli.command.Command#execute(java.lang.String[])
   */
  @Override
  public void execute(String... args) throws InvalidArgumentException {
    if (args == null || args.length < 1) {
      throw new InvalidArgumentException("No argument is given.");
    }
    if (args.length < 3) {
      throw new InvalidArgumentException("Some argument is missing.");
    }
    String source = args[0];
    String goal = args[1];
    String interval = args[2];
    if (!checkGoal(goal)) {
      System.err.println("You must enter an integer between 1 and 99 (inclusive) as a goal.");
    }
    // All the arguments are valid.
    if (checkInterval(interval)) {
      XMLGregorianCalendar currentTime = null, timeStamp = null;
      currentTime = Tstamp.makeTimestamp();
      System.out.println(currentTime);
      try {
        // Get and print out the current power consumption.
        SensorData data = wattDepotClient.getLatestSensorData(source);
        timeStamp = data.getTimestamp();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        System.out.format("%s's power as of %s was %.1f kW.\n", source,
            format.format(new Date(timeStamp.toGregorianCalendar().getTimeInMillis())),
            data.getPropertyAsDouble(SensorData.POWER_CONSUMED) / 1000.0);
        System.out.println(timeStamp.getHour());
      }
      catch (BadXmlException e) {
        XMLGregorianCalendar firstData = null;
        try {
          firstData = wattDepotClient.getSourceSummary(source).getFirstSensorData();
        }
        catch (WattDepotClientException e1) {
          System.err.println("Error attempting to access data from " + source);
          return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        throw new InvalidArgumentException(
            "Error attempting to access data from date. Please use a date on or after "
                + format.format(new Date(firstData.toGregorianCalendar().getTimeInMillis())),
            (Throwable) e);
      }
      catch (WattDepotClientException e) {
        throw new InvalidArgumentException("Error attempting to access data from " + source,
            (Throwable) e);
      }
    }

    else {
      System.err.println("You must enter an integer greater than 0 as the interval.");

    }
  } // end execute()

  /**
   * Method for validating the goal argument.
   * 
   * @param g The String to validate.
   * @return True if the String is a valid goal, and false otherwise.
   * @throws InvalidArgumentException If an invalid argument is given.
   */
  public static boolean checkGoal(String g) throws InvalidArgumentException {
    try {
      int goal = Integer.parseInt(g);
      if (goal < 1 || goal > 99) {
        return false;
      }
      return true;
    }
    catch (NumberFormatException nfe) {
      return false;
    }
  } // end checkGoal().

  /**
   * Method for validating the interval argument.
   * 
   * @param i The String to validate.
   * @return True if the String is a valid interval, and false otherwise.
   * @throws InvalidArgumentException If an invalid argument is given.
   */
  public static boolean checkInterval(String i) throws InvalidArgumentException {
    try {
      int interval = Integer.parseInt(i);
      if (interval < 1) {
        return false;
      }
      return true;
    }
    catch (NumberFormatException nfe) {
      return false;
    }
  } // end checkInterval().
} // end MonitorGoal.
