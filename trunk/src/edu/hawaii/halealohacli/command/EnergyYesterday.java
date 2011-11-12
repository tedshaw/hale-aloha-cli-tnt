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
 * Lists of all sources defined on that server and the amount of energy in watt-hours consumed by
 * that source during the previous day, sorted in ascending order by watt-hours of consumption.
 * 
 * @author Ted Shaw
 */
public class EnergyYesterday implements Comparable<EnergyYesterday> {
  String sourceName;
  double energy;

  /**
   * Compares each source by energy.
   * 
   * @param newSource The source to be compared.
   * @return Whether newSource is before or after this.
   */
  public int compareTo(EnergyYesterday newSource) {
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
    if (!(obj instanceof EnergyYesterday)) {
      return false;
    }
    // cast to native object is now safe
    EnergyYesterday newSource = (EnergyYesterday) obj;
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
    SortedSet<EnergyYesterday> energyList;

    try {
      sourceList = client.getSources();
    }
    catch (WattDepotClientException e) {
      System.err.println("There was a problem accessing the server: " + e.toString());
    }

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", java.util.Locale.US);
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
    String yesterdayDate = formatter.format(cal.getTime());

    System.out.format("Server:  %s\n\n", client.getWattDepotUri());
    System.out.format("%-23sEnergy consumed in watt-hours (%s)\n", "Source", yesterdayDate);

    GregorianCalendar c = new GregorianCalendar();
    c.setTime(cal.getTime());
    XMLGregorianCalendar yesterdayStart = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

    energyList = getEnergy(client, sourceList, yesterdayStart);
    printEnergy(energyList);
  }

  /**
   * Retrieves the source name and yesterday's energy.
   * 
   * @param client The client to connect to.
   * @param sources List of sources to get energy.
   * @param start The day energy is measured.
   * @return List of sorted source latencys.
   * @throws NotAuthorizedException Do nothing.
   * @throws ResourceNotFoundException Do nothing.
   * @throws BadXmlException Do nothing.
   * @throws MiscClientException Do nothing.
   * @throws DatatypeConfigurationException Do nothing.
   */
  static SortedSet<EnergyYesterday> getEnergy(WattDepotClient client, List<Source> sources,
      XMLGregorianCalendar start) throws NotAuthorizedException, ResourceNotFoundException,
      BadXmlException, MiscClientException, DatatypeConfigurationException {
    SortedSet<EnergyYesterday> energyList = new TreeSet<EnergyYesterday>();

    if (sources != null) {
      for (Source source : sources) {
        EnergyYesterday temp = new EnergyYesterday();
        temp.sourceName = source.getName();
        temp.energy = client.getEnergyConsumed(temp.sourceName, start, getEnd(start), 15);
        energyList.add(temp);
      }
    }
    return energyList;
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
   * Prints the source name and yesterday's energy from energyList.
   * 
   * @param energyList List of sources of which will be used to print.
   */
  static void printEnergy(SortedSet<EnergyYesterday> energyList) {
    for (EnergyYesterday source : energyList) {
      System.out.format("%-23s", source.sourceName);
      System.out.format("%s\n", (int) source.energy);
    }
  }

}