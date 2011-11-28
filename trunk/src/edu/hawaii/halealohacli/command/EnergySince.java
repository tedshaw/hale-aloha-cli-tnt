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
 * Implement the energy-since Command interface. <br>
 * <br>
 * 
 * Usage: energy-since [tower | lounge] [date] <br>
 * Returns the energy used since the date (yyyy-mm-dd) to now. <br>
 * <br>
 * 
 * Note: <br>
 * Towers are: Mokihana, Ilima, Lehua, Lokelani <br>
 * Lounges are the tower names followed by a "-" followed by one of A, B, C, D, E. For example,
 * Mokihana-A.
 * 
 * @author Toy Lim
 */
public class EnergySince implements Command {
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
  public EnergySince(WattDepotClient client) {
    commandString = "energy-since";
    commandSyntax = commandString + " [tower | lounge] [date]";
    commandDescription = "Returns the energy used since the date (yyyy-mm-dd) to now.";
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
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    try {
      SensorData data = wattDepotClient.getLatestSensorData(source);
      endTime = data.getTimestamp();
      Double energy = wattDepotClient.getEnergyConsumed(source, startTime, endTime, 0);
      System.out.format("Total energy consumption by %s from %s to %s is: %.1f kWh\n", source,
          format.format(new Date(startTime.toGregorianCalendar().getTimeInMillis())),
          format.format(new Date(endTime.toGregorianCalendar().getTimeInMillis())), energy / 1000);
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
      format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
      throw new InvalidArgumentException(
          "Error attempting to access data from date. Please a date on or after "
              + format.format(new Date(firstData.toGregorianCalendar().getTimeInMillis())),
          (Throwable) e);
    }
    catch (WattDepotClientException e) {
      throw new InvalidArgumentException("Error attempting to access data from " + source,
          (Throwable) e);
    }
  }
}
