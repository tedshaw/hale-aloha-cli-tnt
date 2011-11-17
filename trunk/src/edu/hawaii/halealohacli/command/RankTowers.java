package edu.hawaii.halealohacli.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.xml.datatype.XMLGregorianCalendar;
import org.wattdepot.client.WattDepotClient;
import org.wattdepot.client.WattDepotClientException;
import org.wattdepot.resource.sensordata.jaxb.SensorData;
import org.wattdepot.resource.source.jaxb.Source;
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
 * @author Ted Shaw
 */
public class RankTowers implements Command {
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
  public RankTowers(WattDepotClient client) {
    commandString = "rank-towers";
    commandSyntax = commandString + " [start] [stop]";
    commandDescription =
        "Returns a list in sorted order from least to most energy consumed between the [start] "
            + "and [end] date (yyyy-mm-dd)";
    wattDepotClient = client;
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
    String start = args[0];
    String end = args[1];
    XMLGregorianCalendar startTime = null, endTime = null;
    try {
      startTime = Tstamp.makeTimestamp(start);
    }
    catch (Exception e) {
      throw new InvalidArgumentException("Invalid date: " + start, (Throwable) e);
    }
    try {
      endTime = Tstamp.makeTimestamp(end);
    }
    catch (Exception e) {
      throw new InvalidArgumentException("Invalid date: " + end, (Throwable) e);
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    List<Source> sourceList = null;
    try {
      sourceList = wattDepotClient.getSources();
    }
    catch (WattDepotClientException e) {
      System.err.println("There was a problem accessing the server: " + e.toString());
    }

    try {
      if (sourceList != null) {
        for (Source source : sourceList) {
          String sourceName = source.getName();
          SensorData data = wattDepotClient.getLatestSensorData(sourceName);
          endTime = data.getTimestamp();
          Double energy = wattDepotClient.getEnergyConsumed(sourceName, startTime, endTime, 0);
          System.out.format("Total energy consumption by %s from %s to %s is: %.1f kWh\n",
              sourceName,
              format.format(new Date(startTime.toGregorianCalendar().getTimeInMillis())),
              format.format(new Date(endTime.toGregorianCalendar().getTimeInMillis())),
              energy / 1000);
        }
      }
    }
    catch (WattDepotClientException e) {
      System.err.println("There was a problem accessing the server: " + e.toString());
    }

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

}
