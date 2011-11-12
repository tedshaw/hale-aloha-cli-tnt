package edu.hawaii.halealohacli.command;

import java.util.List;
import org.wattdepot.client.WattDepotClient;
import org.wattdepot.client.WattDepotClientException;
import org.wattdepot.resource.source.jaxb.Source;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * List of all sources defined on that server and their descriptions, sorted in alphabetical order
 * by source name.
 * 
 * @author Ted Shaw
 */
public class SourceListing implements Comparable<SourceListing> {
  String sourceName;
  String sourceDescription;

  /**
   * Compares each source to get list in alphabetical order.
   * 
   * @param newSource The source to be compared.
   * @return Whether newSource is before or after this.
   */
  public int compareTo(SourceListing newSource) {
    int comparison = this.sourceName.compareTo(newSource.sourceName);
    if (comparison != 0) {
      return comparison;
    }
    return this.sourceDescription.compareTo(newSource.sourceDescription);
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
    if (!(obj instanceof SourceListing)) {
      return false;
    }
    // cast to native object is now safe
    SourceListing newSource = (SourceListing) obj;
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
   * Retrieves the list of Sources from the WattDepot server. List in alphabetical order.
   * 
   * @param args First arg is the host URI, such as 'http://localhost:8182/wattdepot/'.
   */
  public static void main(String[] args) {
    WattDepotClient client = new WattDepotClient(args[0]);
    List<Source> sourceList = null;
    SortedSet<SourceListing> sortedSources = new TreeSet<SourceListing>();

    try {
      sourceList = client.getSources();
    }
    catch (WattDepotClientException e) {
      System.err.println("There was a problem accessing the server: " + e.toString());
    }

    System.out.format("Server:  %s\n\n", client.getWattDepotUri());
    System.out.format("%-25sDescription\n", "Source");

    if (sourceList != null) {
      for (Source source : sourceList) {
        sortedSources.add(getSourceDescription(source));
      }
    }

    printDescription(sortedSources);

  }

  /**
   * Gets source name and description.
   * 
   * @param newSource The source used for retrieving name and description.
   * @return SourceListing object with name and description.
   */
  static SourceListing getSourceDescription(Source newSource) {
    SourceListing temp = new SourceListing();
    temp.sourceName = newSource.getName();
    temp.sourceDescription = newSource.getDescription();
    return temp;
  }

  /**
   * Prints the source name and description from sourceList.
   * 
   * @param sourceList List of sources of which will be used to print.
   */
  static void printDescription(SortedSet<SourceListing> sourceList) {
    for (SourceListing source : sourceList) {
      System.out.format("%-25s", source.sourceName);
      System.out.println(source.sourceDescription);
    }
  }

}