package edu.hawaii.halealohacli.command;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import javax.xml.datatype.XMLGregorianCalendar;
import org.wattdepot.client.BadXmlException;
import org.wattdepot.client.MiscClientException;
import org.wattdepot.client.NotAuthorizedException;
import org.wattdepot.client.ResourceNotFoundException;
import org.wattdepot.client.WattDepotClient;
import org.wattdepot.client.WattDepotClientException;
import org.wattdepot.resource.source.jaxb.Source;
import org.wattdepot.resource.sensordata.jaxb.SensorData;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * List of all sources defined on that server and the number of seconds since data was received for
 * that source, sorted in ascending order by this latency value.
 * 
 * @author Ted Shaw
 */
public class SourceLatency implements Comparable<SourceLatency> {
  String sourceName;
  long latency;

  /**
   * Compares each source to get ordered list by latency.
   * 
   * @param newSource The source to be compared.
   * @return Whether newSource is before or after this.
   */
  public int compareTo(SourceLatency newSource) {
    if (this.latency > newSource.latency) {
      return 1;
    }
    else if (this.latency < newSource.latency) {
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
    if (!(obj instanceof SourceLatency)) {
      return false;
    }
    // cast to native object is now safe
    SourceLatency newSource = (SourceLatency) obj;
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
   * Retrieves the list of Sources from the WattDepot server. Lists latency.
   * 
   * @param args First arg is the host URI, such as 'http://localhost:8182/wattdepot/'.
   * @throws MiscClientException Do nothing.
   * @throws BadXmlException Do nothing.
   * @throws ResourceNotFoundException Do nothing.
   * @throws NotAuthorizedException Do nothing.
   */
  public static void main(String[] args) throws NotAuthorizedException, ResourceNotFoundException,
      BadXmlException, MiscClientException {
    WattDepotClient client = new WattDepotClient(args[0]);
    List<Source> sourceList = null;
    SortedSet<SourceLatency> latencyList;

    try {
      sourceList = client.getSources();
    }
    catch (WattDepotClientException e) {
      System.err.println("There was a problem accessing the server: " + e.toString());
    }

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy kk:mm:ss", java.util.Locale.US);
    Date today = new Date();
    String currentTime = formatter.format(today);

    System.out.format("Server:  %s\n\n", client.getWattDepotUri());
    System.out.format("%-23sLatency (in seconds, as of %s)\n", "Source", currentTime);

    latencyList = getLatency(client, sourceList);
    printLatency(latencyList);
  }

  /**
   * Retrieves the source name and latency.
   * 
   * @param client The client to connect to.
   * @param sources List of sources to get latency.
   * @return List of sorted source latencys.
   * @throws NotAuthorizedException Do nothing.
   * @throws ResourceNotFoundException Do nothing.
   * @throws BadXmlException Do nothing.
   * @throws MiscClientException Do nothing.
   */
  static SortedSet<SourceLatency> getLatency(WattDepotClient client, List<Source> sources)
      throws NotAuthorizedException, ResourceNotFoundException, BadXmlException,
      MiscClientException {
    SortedSet<SourceLatency> latencyList = new TreeSet<SourceLatency>();

    if (sources != null) {
      for (Source source : sources) {
        SourceLatency temp = new SourceLatency();
        temp.sourceName = source.getName();

        SensorData data = client.getLatestSensorData(temp.sourceName);

        XMLGregorianCalendar timestamp = data.getTimestamp();
        long now = Calendar.getInstance(Locale.US).getTimeInMillis();
        temp.latency = now - timestamp.toGregorianCalendar().getTimeInMillis();
        latencyList.add(temp);
      }
    }
    return latencyList;
  }

  /**
   * Prints the source name and latency from latencyList.
   * 
   * @param latencyList List of sources of which will be used to print.
   */
  static void printLatency(SortedSet<SourceLatency> latencyList) {
    for (SourceLatency source : latencyList) {
      System.out.format("%-23s", source.sourceName);
      System.out.format("%s\n", source.latency / 1000);
    }
  }

}