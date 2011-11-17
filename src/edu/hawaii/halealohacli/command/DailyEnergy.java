package edu.hawaii.halealohacli.command;

import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.wattdepot.client.BadXmlException;
import org.wattdepot.client.MiscClientException;
import org.wattdepot.client.NotAuthorizedException;
import org.wattdepot.client.ResourceNotFoundException;
import org.wattdepot.client.WattDepotClient;
import org.wattdepot.resource.source.jaxb.Source;
import org.wattdepot.util.tstamp.Tstamp;

/**
 * Prints the daily energy consumption for a given location and date.
 * 
 * @author Joshua Antonio
 * 
 */
public class DailyEnergy implements Command {

  /*
   * (non-Javadoc)
   * 
   * @see edu.hawaii.halealohacli.command.Command#getSyntax()
   */
  @Override
  public String getSyntax() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.hawaii.halealohacli.command.Command#getDescription()
   */
  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.hawaii.halealohacli.command.Command#execute(java.lang.String[])
   */
  @Override
  public Boolean execute(String... args) throws NotAuthorizedException, BadXmlException,
      MiscClientException, ResourceNotFoundException {
    String test = "Ilima";
    String date = "2011-11-05";
    double energy = 0;

    WattDepotClient client = new WattDepotClient(args[0]);
    List<Source> sources;
    sources = client.getSources();

    XMLGregorianCalendar startTime = null, endTime = null;
    try {
      startTime = Tstamp.makeTimestamp(date);
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    endTime = Tstamp.incrementDays(startTime, 1);

    for (Source source : sources) {
      if (source.getName().equals(test)) {
        System.out.print(source.getName() + "'s energy consumption for " + date + " was: ");
        energy = client.getEnergyConsumed(source.getName(), startTime, endTime, 15);
        energy /= 1000;
        System.out.println((int) energy + " kWh.");
        return true;
      }
    }

    return false;
  }
}
