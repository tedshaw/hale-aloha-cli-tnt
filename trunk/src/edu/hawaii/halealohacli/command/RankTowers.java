package edu.hawaii.halealohacli.command;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.datatype.XMLGregorianCalendar;
import org.wattdepot.client.WattDepotClient;
import org.wattdepot.client.WattDepotClientException;
import org.wattdepot.util.tstamp.Tstamp;

/**
 * Implement the rank-towers Command interface.
 * 
 * Usage: rank-towers [start] [end] Returns the energy used since the date (yyyy-mm-dd) to now.
 * Returns a list in sorted order from least to most energy consumed between the [start] and [end]
 * date (yyyy-mm-dd) Towers are: Mokihana, Ilima, Lehua, Lokelani <br>
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
    commandSyntax = commandString + " [start] [end]";
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
   * Data structure for sorting energy.
   * 
   * @author Ted Shaw
   * 
   */
  private static class EnergySort implements Comparable<EnergySort> {
    String source;
    double energy;

    /**
     * Used to sort the energy.
     * 
     * @param other The tower to be compared.
     * @return Whether tower is before or after this.
     */
    public int compareTo(EnergySort other) {
      if (this.energy > other.energy) {
        return 1;
      }
      else if (this.energy < other.energy) {
        return -1;
      }
      else {
        return this.source.compareTo(other.source);
      }
    }

    /**
     * Implements equals using only the source name.
     * 
     * @param obj Comparison object
     * @return If obj is equal or not to this.
     */
    @Override
    public boolean equals(Object obj) {
      // check identity
      if (this == obj) {
        return true;
      }
      // check instance
      if (!(obj instanceof EnergySort)) {
        return false;
      }
      // cast to native object is now safe
      EnergySort newSource = (EnergySort) obj;
      // native object compare
      return this.source.equals(newSource.source);
    }

    /**
     * Returns the hashCode using the source name.
     * 
     * @return The hash code.
     */
    @Override
    public int hashCode() {
      return this.source.hashCode();
    }
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
      System.out.println("Invalid date: " + start);
      return;
    }
    try {
      endTime = Tstamp.makeTimestamp(end);
    }
    catch (Exception e) {
      System.out.println("Invalid date: " + end);
      return;
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    SortedSet<EnergySort> sortedEnergy = new TreeSet<EnergySort>();
    List<String> sourceList = new ArrayList<String>();

    sourceList.add("Mokihana");
    sourceList.add("Ilima");
    sourceList.add("Lehua");
    sourceList.add("Lokelani");

    for (String sourceName : sourceList) {
      EnergySort temp = new EnergySort();
      temp.source = sourceName;
      try {
        temp.energy = wattDepotClient.getEnergyConsumed(temp.source, startTime, endTime, 0);
      }
      catch (WattDepotClientException e) {
        throw new InvalidArgumentException("Error attempting to access data from " + sourceName,
            (Throwable) e);
      }
      sortedEnergy.add(temp);
    }

    System.out.format("For the interval %s to %s, energy consumption by tower was:\n",
        format.format(new Date(startTime.toGregorianCalendar().getTimeInMillis())),
        format.format(new Date(endTime.toGregorianCalendar().getTimeInMillis())));
    for (EnergySort currentEnergy : sortedEnergy) {
      System.out.format("%-10s %s kWh\n", currentEnergy.source, (int) currentEnergy.energy / 1000);
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
