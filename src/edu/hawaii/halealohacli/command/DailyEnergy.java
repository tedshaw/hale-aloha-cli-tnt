package edu.hawaii.halealohacli.command;

import javax.xml.datatype.XMLGregorianCalendar;
import org.wattdepot.client.WattDepotClient;
import org.wattdepot.client.WattDepotClientException;
import org.wattdepot.util.tstamp.Tstamp;

/**
 * Prints the daily energy consumption for a given location and date.
 * 
 * @author Joshua Antonio, Toy Lim
 */
public class DailyEnergy implements Command {
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
  public DailyEnergy(WattDepotClient client) {
    commandString = "daily-energy";
    commandSyntax = commandString + " [tower | lounge] [date]";
    commandDescription = "Returns the energy in kWh used by the tower or lounge for the " 
                         + "specified date (yyyy-mm-dd).";
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
    if (args.length < 2) {
      throw new InvalidArgumentException("Some argument is missing.");
    }
    String source = args[0];
    String date = args[1];
    XMLGregorianCalendar startTime = null, endTime = null;
    try {
      startTime = Tstamp.makeTimestamp(date);
    }
    catch (Exception e) {
      throw new InvalidArgumentException("Invalid date: " + date, (Throwable) e);
    }
    endTime = Tstamp.incrementDays(startTime, 1);
    try {
      Double energy = wattDepotClient.getEnergyConsumed(source, startTime, endTime, 0);
      System.out.format("%s's energy consumption for %s was: %.0f kWh.", source, date, 
          energy / 1000);
    }
    catch (WattDepotClientException e) {
      throw new InvalidArgumentException("Error attempting to access data from " + source, 
          (Throwable) e);
    }
  }
}
