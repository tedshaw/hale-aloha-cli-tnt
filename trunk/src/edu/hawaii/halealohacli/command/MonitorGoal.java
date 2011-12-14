package edu.hawaii.halealohacli.command;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.datatype.XMLGregorianCalendar;
import org.wattdepot.client.BadXmlException;
import org.wattdepot.client.WattDepotClient;
import org.wattdepot.client.WattDepotClientException;
import org.wattdepot.resource.sensordata.jaxb.SensorData;
import org.wattdepot.util.tstamp.Tstamp;

/**
 * Implement the monitor-goal Command interface. <br>
 * <br>
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
  private Timer timer = new Timer();
  private InputStream input = System.in;

  /**
   * Default constructor.
   * 
   * @param client WattDepotClient to be used by this command
   * @see org.wattdepot.client.WattDepotClient
   */
  public MonitorGoal(WattDepotClient client) {
    commandString = "monitor-goal";
    commandSyntax = commandString + " [tower | lounge] goal interval";
    commandDescription = "Every [interval] seconds, prints out a timestamp, the current power " +
        "being consumed by the [tower | lounge], and whether or not the lounge is meeting its" +
        " power conservation goal.";
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
    final String[] arguments = args;
    StringBuilder sb = checkAll(args);
    // All the arguments are valid.
    if (sb == null || sb.toString().isEmpty()) {
      int interval = Integer.parseInt(args[2]);

      TimerTask task = new TimerTask() {
        // internal run method for task
        public void run() {
          try {
            processor(arguments);
          }
          // processor will take care of printing out specific error messages.
          catch (InvalidArgumentException e) {
            System.err.println("Please try again.");
          }
        }
      };

      timer.scheduleAtFixedRate(task, 0, interval * 1000);

      try {
        // task is running, enter loop to check for user input.
        while (input.available() == 0) {
          try {
            Thread.sleep(100);
          }
          catch (InterruptedException e) {
            // catch InterruptedException. don't do anything.
          }
        }
      }
      catch (IOException e) {
        // catches IOException, issue with input stream.
        System.out.println("Unfortunately, there was a problem with the input stream. "
            + "Please choose another command.");
      }

      // loop finished, either user cancelled or issue with input stream, cancel task.
      task.cancel();

      try {
        Thread.sleep(300);
      }
      catch (InterruptedException e) {
        // catches InterrruptedException. don't do anything.
      }

      try {
        // clear input buffer so that we don't get an invalid command error!
        while (input.available() > 0) {
          input.read();
        }
      }
      catch (IOException e) {
        // catches IOException. don't do anything.
        System.out.print("");
      }
    }
    else {
      System.err.println(sb.toString());
    }
  } // end execute()

  
  /**
   * Method for validating the goal argument.
   * 
   * @param g The String to validate.
   * @return True if the String is a valid goal, and false otherwise.
   */
  public static boolean checkGoal(String g) {
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
   */
  public static boolean checkInterval(String i) {
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

  
  /**
   * Checks all the command line arguments, as well as whether the baseline was set or not.
   * 
   * @param args The command line arguments.
   * @return A StringBuilder containing the errors raised, if any.
   * @throws InvalidArgumentException If an invalid number of arguments were given.
   */
  public static StringBuilder checkAll(String[] args) throws InvalidArgumentException {
    if (args == null || args.length < 1) {
      throw new InvalidArgumentException("No argument is given.");
    }
    if (args.length < 3) {
      throw new InvalidArgumentException("Some argument is missing.");
    }
    String source = args[0];
    String goal = args[1];
    String interval = args[2];
    String regex = "^(Mokihana|Ilima|Lehua|Lokelani)[-]?[A-E]?$";
    StringBuilder sb = new StringBuilder();
    if (!source.matches(regex)) {
      sb.append("Please enter a valid tower or lounge name. Type 'help' to see the available "
          + "options.\n");
    }
    if (!checkGoal(goal)) {
      sb.append("You must enter an integer between 1 and 99 (inclusive) as a goal.\n");
    }
    if (!checkInterval(interval)) {
      sb.append("You must enter an integer greater than 0 as the interval.\n");
    }
    if (!SetBaseline.checkArray()) {
      sb.append("You must set the baseline first. Enter 'help' to learn how to do this.\n");
    }
    return sb;
  } // end checkAll()

  /**
   * Calculates the target power consumption goal.
   * 
   * @param goal The goal as a percentage in power consumption reduction.
   * @param baseline The amount to base the reduction off of.
   * @return The target power consumption amount as a double.
   */
  public static double calculateGoal(int goal, double baseline) {
    double percent = 100 - goal;
    return baseline * percent / 100.0;
  } // end calculateGoal()

  /**
   * Checks whether the power consumption goal is being met for the given tower or lounge.
   * 
   * @param goal The targeted amount to be consuming.
   * @param current The actual amount of power being currently consumed.
   * @return True if the goal is being met, and false otherwise.
   */
  public static boolean isGoalBeingMet(double goal, double current) {
    if (current <= goal) {
      return true;
    }
    return false;
  } // end isGoalBeingMet()

  
  /**
   * The main processor for the MonitorGoal class. Runs the checking methods and prints out all
   * the information and data.
   * @param args The command line arguments.
   * @throws InvalidArgumentException If an invalid number of arguments were given.
   */
  public void processor(String... args) throws InvalidArgumentException {
    String source = args[0];
    int goal = Integer.parseInt(args[1]);
    XMLGregorianCalendar currentTime = null, timeStamp = null;
    currentTime = Tstamp.makeTimestamp();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    System.out.format("The current time is: %s\n",
        format.format(new Date(currentTime.toGregorianCalendar().getTimeInMillis())));
    // System.out.println(currentTime);
    try {
      // Get and print out the current power consumption.
      SensorData data = wattDepotClient.getLatestSensorData(source);
      timeStamp = data.getTimestamp();
      double currentPow = data.getPropertyAsDouble(SensorData.POWER_CONSUMED) / 1000.0;
      // System.out.println(timeStamp.getHour());
      final String CONGRATS = "Congratulations, you are meeting your power consumption goal!";
      final String FAIL =
          "I'm sorry, you are not currently meeting your power consumption goal. "
              + "Please conserve a little more energy.";
      double baseline = 0;
      // get the baseline value for the current hour.
      for (int i = 0; i < 24; i++) {
        if (timeStamp.getHour() == i) {
          baseline = SetBaseline.baselines[i] / 1000.0;
          break;
        }
      }
      System.out.format("The baseline for %s is %.1f kWh.\n", source, baseline);
      double desiredConsumption = calculateGoal(goal, baseline);
      System.out.format("To meet your goal of a %s percent reduction in power consumption, you"
          + " must be using less than %.1f kW of power.\n", goal, desiredConsumption);
      System.out.format("As of %s, %s was using %.1f kW of power.\n", source,
          format.format(new Date(timeStamp.toGregorianCalendar().getTimeInMillis())), currentPow);
      if (isGoalBeingMet(desiredConsumption, currentPow)) {
        System.out.println(CONGRATS);
      }
      else {
        System.out.println(FAIL);
      }
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
      throw new InvalidArgumentException("Error attempting to access data from date. Please "
          + "use a date on or after " + format.format(new Date(firstData.toGregorianCalendar().
              getTimeInMillis())), (Throwable) e);
    }
    catch (WattDepotClientException e) {
      throw new InvalidArgumentException("Error attempting to access data from " + source,
          (Throwable) e);
    }
  } // end processor()
  
} // end MonitorGoal.
