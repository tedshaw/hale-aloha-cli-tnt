package edu.hawaii.halealohacli.command;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import org.wattdepot.client.WattDepotClient;

/**
 * @author Ardell Klemme
 * 
 */
public class MonitorPower implements Command {
  private String commandString;
  private String commandSyntax;
  private String commandDescription;
  private String source;
  private CurrentPower power;

  private static final int DEFAULT = 10000; // default to 10 seconds.
  private String interval = "10";
  private int period = 10;
  private Timer timer = new Timer();
  private InputStream input = System.in;

  /**
   * Default constructor.
   * 
   * @param client WattDepotClient to be used by this command
   * @see org.wattdepot.client.WattDepotClient
   */
  public MonitorPower(WattDepotClient client) {
    commandString = "monitor-power";
    commandSyntax = commandString + " [tower | lounge] [interval]";
    commandDescription =
        "This command prints out a timestamp and the current power for "
            + "[tower | lounge] every [interval] seconds.  [interval] is an "
            + "optional argument and defaults to 10 seconds. Hitting the "
            + "Enter key stops this monitoring process and returns the user "
            + "to the command loop.";
    WattDepotClient wattDepotClient = client;
    power = new CurrentPower(wattDepotClient);
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
   * Return the source.
   * 
   * @return command String
   */
  public String getSource() {
    return source;
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
    source = args[0];
    if (args.length < 2) {
      // user didn't specify an interval.
      period = DEFAULT;
    }
    else {
      interval = args[1];
    }

    try {
      period = Integer.valueOf(interval.trim()) * 1000;
    }
    catch (Exception e) {
      // non-integer entered for interval...change to default time.
      period = DEFAULT;
      System.out.println("Unable to parse interval from argument, "
          + "using default (10 seconds) interval.");
    }

    TimerTask task = new TimerTask() {

      // internal run method for task
      public void run() {

        try {
          power.execute(source);
        }
        catch (InvalidArgumentException e) {
          // problem accessing power information. Need to stop task.
          System.out.println("Unable to get current power data from source, "
              + "hit Enter to continue.");
          cancel();
        }
      }
    };

    timer.scheduleAtFixedRate(task, 0, period);

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

  /**
   * Gets current-power object.
   * 
   * @return power as a current-power object.
   */
  public CurrentPower getPower() {
    return power;
  }

}
