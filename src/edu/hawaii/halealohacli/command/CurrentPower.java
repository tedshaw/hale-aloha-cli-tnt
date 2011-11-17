package edu.hawaii.halealohacli.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.xml.datatype.XMLGregorianCalendar;
import org.wattdepot.client.WattDepotClient;
import org.wattdepot.client.WattDepotClientException;
import org.wattdepot.resource.sensordata.jaxb.SensorData;

/**
 * Implement the current-power Command interface. <br>
 * Usage: current-power [tower | lounge] <br>
 * Returns the current power in kW for the associated tower or lounge. <br>
 * 
 * Note: <br>
 * Towers are: Mokihana, Ilima, Lehua, Lokelani <br>
 * Lounges are the tower names followed by a "-" followed by one of A, B, C, D, E.
 * For example, Mokihana-A.
 * 
 * @author Toy Lim
 */
public class CurrentPower implements Command {
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
  public CurrentPower(WattDepotClient client) {
    commandString = "current-power";
    commandSyntax = commandString + " [tower | lounge]";
    commandDescription = "Returns the current power in kW for the associated tower or lounge.";
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
    for (String source : args) {
      try {
        SensorData data = wattDepotClient.getLatestSensorData(source);
        XMLGregorianCalendar timestamp = data.getTimestamp();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        System.out.format("%s's power as of %s was %.1f kW.\n", source, 
            format.format(new Date(timestamp.toGregorianCalendar().getTimeInMillis())), 
            data.getPropertyAsDouble("powerConsumed"));
      }
      catch (WattDepotClientException e) {
        throw new InvalidArgumentException("Error attempting to access data from " + source, 
            (Throwable) e);
      }
    }
  }
}
