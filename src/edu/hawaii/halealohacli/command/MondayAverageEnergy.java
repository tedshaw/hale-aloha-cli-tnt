package edu.hawaii.halealohacli.command;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import org.wattdepot.client.BadXmlException;
import org.wattdepot.client.MiscClientException;
import org.wattdepot.client.NotAuthorizedException;
import org.wattdepot.client.ResourceNotFoundException;
import org.wattdepot.client.WattDepotClient;
import org.wattdepot.client.WattDepotClientException;
import org.wattdepot.resource.source.jaxb.Source;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.datatype.DatatypeFactory;

/**
 * Lists of all sources defined on that server and the average energy consumed by that source during
 * the previous two Mondays, sorted in ascending order by watt-hours.
 * 
 * @author Ted Shaw
 */
public class MondayAverageEnergy implements Comparable<MondayAverageEnergy> {
  String sourceName;
  double energy;

  /**
   * Compares each source by energy.
   * 
   * @param newSource The source to be compared.
   * @return Whether newSource is before or after this.
   */
  public int compareTo(MondayAverageEnergy newSource) {
    if (this.energy > newSource.energy) {
      return 1;
    }
    else if (this.energy < newSource.energy) {
      return -1;
    }
    else {
      return this.sourceName.compareTo(newSource.sourceName);
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
    if (!(obj instanceof MondayAverageEnergy)) {
      return false;
    }
    // cast to native object is now safe
    MondayAverageEnergy newSource = (MondayAverageEnergy) obj;
    // native object compare
    return this.sourceName.equals(newSource.sourceName);
  }

  /**
   * Returns the hashCode using the source name.
   * 
   * @return The hash code.
   */
  @Override
  public int hashCode() {
    return this.sourceName.hashCode();
  }

  /**
   * Retrieves the list of Sources from the WattDepot server.
   * 
   * @param args First arg is the host URI, such as 'http://localhost:8182/wattdepot/'.
   * @throws DatatypeConfigurationException Do nothing.
   * @throws NotAuthorizedException Do nothing.
   * @throws ResourceNotFoundException Do nothing.
   * @throws BadXmlException Do nothing.
   * @throws MiscClientException Do nothing.
   */
  public static void main(String[] args) throws DatatypeConfigurationException,
      NotAuthorizedException, ResourceNotFoundException, BadXmlException, MiscClientException {
    WattDepotClient client = new WattDepotClient(args[0]);
    List<Source> sourceList = null;
    SortedSet<MondayAverageEnergy> energyList = new TreeSet<MondayAverageEnergy>();

    try {
      sourceList = client.getSources();
    }
    catch (WattDepotClientException e) {
      System.err.println("There was a problem accessing the server: " + e.toString());
    }

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", java.util.Locale.US);
    Calendar cal = Calendar.getInstance();

    for (int i = 1; i < 7; i++) {
      if (cal.get(Calendar.DAY_OF_WEEK) != java.util.Calendar.MONDAY) {
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
      }
    }

    String monday1 = formatter.format(cal.getTime());
    GregorianCalendar c = new GregorianCalendar();
    c.setTime(cal.getTime());
    XMLGregorianCalendar monday1Start = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
    cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 7);
    String monday2 = formatter.format(cal.getTime());
    c.setTime(cal.getTime());
    XMLGregorianCalendar monday2Start = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

    System.out.format("Server:  %s\n\n", client.getWattDepotUri());
    System.out.format("%-23sAverage energy for last two Mondays in watt-hours (%s, %s)\n",
        "Source", monday1, monday2);

    if (sourceList != null) {
      for (Source source : sourceList) {
        getAverage(source, client, monday1Start, energyList);
      }
      for (Source source : sourceList) {
        getAverage(source, client, monday2Start, energyList);
      }
    }

    printEnergy(energyList);

  }

  /**
   * Finds the average energy consumed between start and end time.
   * 
   * @param currentSource The source where average energy to be found.
   * @param client Client server source is on.
   * @param start Start time of energy check.
   * @param energyList List.
   * @throws NotAuthorizedException Do nothing.
   * @throws ResourceNotFoundException Do nothing.
   * @throws BadXmlException Do nothing.
   * @throws MiscClientException Do nothing.
   * @throws DatatypeConfigurationException Do nothing.
   */
  static void getAverage(Source currentSource, WattDepotClient client, XMLGregorianCalendar start,
      SortedSet<MondayAverageEnergy> energyList) throws NotAuthorizedException,
      ResourceNotFoundException, BadXmlException, MiscClientException,
      DatatypeConfigurationException {

    MondayAverageEnergy temp = new MondayAverageEnergy();
    temp.sourceName = currentSource.getName();
    temp.energy = 0;
    Double totalEnergy;
    int timeInterval = 15;
    boolean isThere = false;

    try {
      totalEnergy = client.getEnergyConsumed(temp.sourceName, start, getEnd(start), timeInterval);
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      totalEnergy = client.getEnergyConsumed(temp.sourceName, start, getEnd(start), timeInterval);
    }
    temp.energy = totalEnergy;

    if (!energyList.isEmpty()) {
      for (MondayAverageEnergy avg : energyList) {
        if (temp.sourceName.equals(avg.sourceName)) {
          avg.energy = (avg.energy + temp.energy) / 2;
          isThere = true;
        }
      }
    }

    if (!isThere) {
      energyList.add(temp);
    }

  }

  /**
   * Returns an end date, give a start date.
   * 
   * @param start The start date of a one day period.
   * @return The end date of a one day period.
   * @throws DatatypeConfigurationException Do nothing.
   */
  static XMLGregorianCalendar getEnd(XMLGregorianCalendar start)
      throws DatatypeConfigurationException {
    javax.xml.datatype.DatatypeFactory factory = javax.xml.datatype.DatatypeFactory.newInstance();
    // Duration to increment the calendar by one day
    javax.xml.datatype.Duration day = factory.newDurationDayTime(true, 1, 0, 0, 0);

    start.setHour(0);
    start.setMinute(0);
    start.setSecond(0);
    start.setMillisecond(0);
    XMLGregorianCalendar end = (XMLGregorianCalendar) start.clone();
    end.add(day);
    return end;
  }

  /**
   * Prints the source name and average energy from energyList.
   * 
   * @param energyList List of sources of which will be used to print.
   */
  static void printEnergy(SortedSet<MondayAverageEnergy> energyList) {
    for (MondayAverageEnergy source : energyList) {
      System.out.format("%-23s", source.sourceName);
      System.out.format("%s\n", (int) source.energy);
    }
  }

}