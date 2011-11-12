package edu.hawaii.halealohacli.command;

import java.util.List;
import org.wattdepot.client.BadXmlException;
import org.wattdepot.client.MiscClientException;
import org.wattdepot.client.NotAuthorizedException;
import org.wattdepot.client.ResourceNotFoundException;
import org.wattdepot.client.WattDepotClient;
import org.wattdepot.client.WattDepotClientException;
import org.wattdepot.resource.source.jaxb.Source;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Displays hierarchical list of all sources defined on that server.
 * 
 * @author Ted Shaw
 */
public class SourceHierarchy implements Comparable<SourceHierarchy> {
  String sourceName;
  List<String> subSourceName;
  List<Source> subSources;
  List<SourceHierarchy> subSourceHier;

  /**
   * Compares each source to get list in alphabetical order.
   * 
   * @param newSource The source to be compared.
   * @return Whether newSource is before or after this.
   */
  public int compareTo(SourceHierarchy newSource) {
    return this.sourceName.compareTo(newSource.sourceName);
  }

  /**
   * Implements equals using only the source name.
   * 
   * @param obj Comparison object.
   * @return If obj is equal or not to this.
   */
  @Override
  public boolean equals(Object obj) {
    // check identity
    if (this == obj) {
      return true;
    }
    // check instance
    if (!(obj instanceof SourceHierarchy)) {
      return false;
    }
    // cast to native object is now safe
    SourceHierarchy newSource = (SourceHierarchy) obj;
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
   * @throws NotAuthorizedException Do nothing.
   * @throws ResourceNotFoundException Do nothing.
   * @throws BadXmlException Do nothing.
   * @throws MiscClientException Do nothing.
   */
  public static void main(String[] args) throws NotAuthorizedException, ResourceNotFoundException,
      BadXmlException, MiscClientException {
    String server = args[0];
    WattDepotClient client = new WattDepotClient(server);
    List<Source> sourceList = null;
    List<SourceHierarchy> sourceHierList = new ArrayList<SourceHierarchy>();

    try {
      sourceList = client.getSources();
    }
    catch (WattDepotClientException e) {
      System.err.println("There was a problem accessing the server: " + e.toString());
    }
    System.out.format("Server:  %s\n\n", client.getWattDepotUri());

    if (sourceList != null) {
      for (Source source : sourceList) {
        sourceHierList.add(getSubSources(client, source));
      }
    }

    Set<SourceHierarchy> duplicates = new TreeSet<SourceHierarchy>();

    for (SourceHierarchy mainSource : sourceHierList) {
      duplicates = getDuplicates(mainSource.subSourceHier, duplicates);
    }

    removeDuplicates(sourceHierList, duplicates);

    for (SourceHierarchy currentHier : sourceHierList) {
      printSubSources(currentHier, 0);
    }

  }

  /**
   * Yes.
   * 
   * @param client WattDepotClient server.
   * @param source The source which the subsources are retrieved.
   * @return The completed SourceHierarchy object.
   * @throws NotAuthorizedException Do nothing.
   * @throws ResourceNotFoundException Do nothing.
   * @throws BadXmlException Do nothing.
   * @throws MiscClientException Do nothing.
   */
  static SourceHierarchy getSubSources(WattDepotClient client, Source source)
      throws NotAuthorizedException, ResourceNotFoundException, BadXmlException,
      MiscClientException {
    SourceHierarchy temp = new SourceHierarchy();
    temp.subSourceName = new ArrayList<String>();
    temp.subSources = new ArrayList<Source>();
    temp.subSourceHier = new ArrayList<SourceHierarchy>();

    temp.sourceName = source.getName();

    if (source.isSetSubSources()) {
      List<String> tempSubSourceUri = source.getSubSources().getHref();
      for (String subUri : tempSubSourceUri) {
        String currentSubName = org.wattdepot.util.UriUtils.getUriSuffix(subUri);
        temp.subSourceName.add(currentSubName);
        Source currentSub;
        try {
          currentSub = client.getSource(currentSubName);
        }
        catch (Exception e) {
          // TODO Auto-generated catch block
          currentSub = client.getSource(currentSubName);
        }
        temp.subSources.add(currentSub);
        temp.subSourceHier.add(getSubSources(client, currentSub));
      }
    }
    return temp;
  }

  /**
   * Prints out the hierarchical structure of the client.
   * 
   * @param currentSource Source and its subsequent subsources that will be printed.
   * @param indent The number of spaces to indent for subsources.
   */
  static void printSubSources(SourceHierarchy currentSource, int indent) {
    System.out.println(currentSource.sourceName);
    for (SourceHierarchy currentSubSource : currentSource.subSourceHier) {
      for (int i = 0; i < indent + 3; i++) {
        System.out.print(" ");
      }
      printSubSources(currentSubSource, indent + 3);
    }
  }

  /**
   * Find the root sources that are subsources.
   * 
   * @param subHier The list of sources to check for duplicates.
   * @param duplicateList The list of duplicated sources.
   * @return The duplicate source list.
   */
  static Set<SourceHierarchy> getDuplicates(List<SourceHierarchy> subHier,
      Set<SourceHierarchy> duplicateList) {

    for (SourceHierarchy currentHier : subHier) {
      if (!duplicateList.contains(currentHier)) {
        duplicateList.add(currentHier);
      }
      if (!currentHier.subSourceHier.isEmpty()) {
        duplicateList.addAll(getDuplicates(currentHier.subSourceHier, duplicateList));
      }
    }

    return duplicateList;
  }

  /**
   * Removes the duplicates for the root sources.
   * 
   * @param hiers List of sources where duplicates are deleted.
   * @param duplicates List of duplicate sources.
   */
  static void removeDuplicates(List<SourceHierarchy> hiers, Set<SourceHierarchy> duplicates) {

    for (int i = 0; i < hiers.size(); i++) {
      for (SourceHierarchy dupe : duplicates) {
        if (hiers.get(i).sourceName.equals(dupe.sourceName)) {
          hiers.remove(i);
        }
      }
    }
  }

}