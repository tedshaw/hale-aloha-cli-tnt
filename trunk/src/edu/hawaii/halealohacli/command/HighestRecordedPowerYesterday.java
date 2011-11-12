package edu.hawaii.halealohacli.command;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import org.wattdepot.resource.sensordata.jaxb.SensorData;
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
 * List of all sources defined on that server and the highest recorded power associated with that
 * source during the previous day, sorted in ascending order by watts.
 * 
 * @author Ted Shaw
 */
public class HighestRecordedPowerYesterday implements Comparable<HighestRecordedPowerYesterday> {
  String sourceName;
  XMLGregorianCalendar time;
  double power;

  /**
   * Compares each source by power.
   * 
   * @param newSource The source to be compared.
   * @return Whether newSource is before or after this.
   */
  public int compareTo(HighestRecordedPowerYesterday newSource) {
    if (this.power > newSource.power) {
      return 1;
    }
    else if (this.power < newSource.power) {
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
    if (!(obj instanceof HighestRecordedPowerYesterday)) {
      return false;
    }
    // cast to native object is now safe
    HighestRecordedPowerYesterday newSource = (HighestRecordedPowerYesterday) obj;
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
    SortedSet<HighestRecordedPowerYesterday> powerList;

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
    System.out.format("%-23sHighest recorded power in watts (%s)\n", "Source", yesterdayDate);

    GregorianCalendar c = new GregorianCalendar();
    c.setTime(cal.getTime());
    XMLGregorianCalendar yesterdayStart = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

    powerList = getPower(client, sourceList, yesterdayStart);
    printPower(powerList);
  }

  /**
   * Gets the highest power recorded and the time.
   * 
   * @param client The client being connected to.
   * @param sources List of sources that power will be measured.
   * @param start Yesterday's start time.
   * @return Sorted set of highest power and times.
   * @throws NotAuthorizedException Do nothing.
   * @throws ResourceNotFoundException Do nothing.
   * @throws BadXmlException Do nothing.
   * @throws MiscClientException Do nothing.
   * @throws DatatypeConfigurationException Do nothing.
   */
  static SortedSet<HighestRecordedPowerYesterday> getPower(WattDepotClient client,
      List<Source> sources, XMLGregorianCalendar start) throws NotAuthorizedException,
      ResourceNotFoundException, BadXmlException, MiscClientException,
      DatatypeConfigurationException {
    SortedSet<HighestRecordedPowerYesterday> powerList =
        new TreeSet<HighestRecordedPowerYesterday>();

    if (sources != null) {
      for (Source source : sources) {
        HighestRecordedPowerYesterday temp = new HighestRecordedPowerYesterday();
        temp.sourceName = source.getName();
        temp.power = -1.0;
        List<SensorData> tempSensor;

        try {
          tempSensor = client.getSensorDatas(temp.sourceName, start, getEnd(start));
        }
        catch (Exception e1) {
          // TODO Auto-generated catch block
          tempSensor = client.getSensorDatas(temp.sourceName, start, getEnd(start));
        }

        // Non-virtual Sources
        for (SensorData currentData : tempSensor) {
          if (temp.power < currentData.getPropertyAsDouble("powerConsumed")) {
            temp.power = currentData.getPropertyAsDouble("powerConsumed");
            temp.time = currentData.getTimestamp();
          }
        }

        // Virtual Sources
        if (temp.power == -1.0) {
          Double tempPower;
          XMLGregorianCalendar hourVary = (XMLGregorianCalendar) start.clone();
          for (int i = 0; i < 24; i++) {
            hourVary.setHour(i);
            tempPower = client.getPowerConsumed(temp.sourceName, hourVary);

            if (temp.power < tempPower) {
              temp.power = tempPower;
              temp.time = hourVary;
            }
          }
        }
        powerList.add(temp);
      }
    }
    return powerList;
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
   * Prints the source name and yesterday's highest power from powerList.
   * 
   * @param powerList List of sources of which will be used to print.
   */
  static void printPower(SortedSet<HighestRecordedPowerYesterday> powerList) {
    SimpleDateFormat formatter = new SimpleDateFormat("KK:mma", java.util.Locale.US);

    for (HighestRecordedPowerYesterday source : powerList) {
      System.out.format("%-23s", source.sourceName);
      System.out.format("%s ", (int) source.power);

      Calendar cal = source.time.toGregorianCalendar();
      String highTime = formatter.format(cal.getTime());
      System.out.format("%s\n", highTime);
    }
  }

}