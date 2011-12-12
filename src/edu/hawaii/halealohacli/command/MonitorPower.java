package edu.hawaii.halealohacli.command;

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
            + "optional argument and defaults to 10 seconds. Entering any "
            + "character (such as a carriage return) stops this monitoring "
            + "process and returns the user to the command loop.";
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
    String interval = "";
    Long period = (long) 10;
    Timer timer = new Timer();
    if (args == null || args.length < 1) {
      throw new InvalidArgumentException("No argument is given.");
    }
    source = args[0];
    if (args.length < 2) {
      interval = "10";
    }
    else {
      interval = args[1];
    }

    try {
      period = Long.valueOf(interval.trim()) * 1000;
    }
    catch (Exception e) {
      //throw new Exception("Unable to parse interval from argument.");
      System.out.println("Unable to parse interval from argument.");
    }
    
    timer.scheduleAtFixedRate(new TimerTask() {
      
      public void run() {
          try {
            power.execute(source);
          }
          catch (InvalidArgumentException e) {
            e.printStackTrace();
          }                
      } //end run()
      
    }, 0, period); //end TimerTask()
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
