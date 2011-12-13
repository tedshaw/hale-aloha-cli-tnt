package edu.hawaii.halealohacli.command;


import org.wattdepot.client.WattDepotClient;
import org.wattdepot.client.WattDepotClientException;
import org.wattdepot.resource.sensordata.jaxb.SensorData;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Implement the set-baseline Command interface. <br>
 * <br>
 * 
 * Usage: set-baseline [tower | lounge] [date]<br>
 * Returns an array of baseline value of the 24 hours in the day <br>
 * <br>
 * 
 * Note: <br>
 * Towers are: Mokihana, Ilima, Lehua, Lokelani <br>
 * Lounges are the tower names followed by a "-" followed by one of A, B, C, D, E. For example,
 * Mokihana-A.
 * 
 * @author Matthew Mizumoto
 */
public class SetBaseline implements Command {
  private String commandString;
  private String commandSyntax;
  private String commandDescription;
  private WattDepotClient wattDepotClient;
  static double baselines[] = new double[24];

  /**
   * Default constructor.
   * 
   * @param client WattDepotClient to be used by this command
   * @see org.wattdepot.client.WattDepotClient
   */
  public SetBaseline(WattDepotClient client) {
    commandString = "set-baseline";
    commandSyntax = commandString + " [tower | lounge] [date]";
    commandDescription =
        "Defines [date] as the baseline and obtains and saves "
            + "the amount of energy obtained hourly. \n"
            + "The [date] parameter is optional and defaults to yesterday if excluded.";
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
    
    String source = args[0];

    try {
      XMLGregorianCalendar startTime = wattDepotClient.getLatestSensorData(source).getTimestamp();
      XMLGregorianCalendar endTime = wattDepotClient.getLatestSensorData(source).getTimestamp();

      if (args.length == 1) { // case where user does not give date.
        SensorData data = wattDepotClient.getLatestSensorData(source);
        SensorData data2 = wattDepotClient.getLatestSensorData(source);
        startTime = data.getTimestamp();
        endTime = data2.getTimestamp();
        startTime.setDay(startTime.getDay() - 1);
        endTime.setDay(endTime.getDay() - 1);
      }
      else if (args.length == 2) { // case where the date is given by the user.
        String[] dateSplit = args[1].split("-");
        startTime.setYear(Integer.parseInt(dateSplit[0]));
        startTime.setMonth(Integer.parseInt(dateSplit[1]));
        startTime.setDay(Integer.parseInt(dateSplit[2]));
        endTime.setYear(Integer.parseInt(dateSplit[0]));
        endTime.setMonth(Integer.parseInt(dateSplit[1]));
        endTime.setDay(Integer.parseInt(dateSplit[2]));
      }
      else {
        System.out.println("Too many arguments");
      }

      for (int i = 0; i < 24; i++) {
        startTime.setTime(i, 0, 0, 0);
        endTime.setTime(i, 59, 59, 999);

        Double energy = wattDepotClient.getEnergyConsumed(source, startTime, endTime, 0);
        baselines[i] = energy;
        System.out.println("baseline " + i + ": " + baselines[i] / 1000);
        System.out.println();
      }
    }
    catch (WattDepotClientException e) {
      throw new InvalidArgumentException("Error attempting to access data from " + source,
          (Throwable) e);
    }
    
    System.out.print(SetBaseline.checkArray());
  }

  /**
   * checks to see if the baselines have already been set.
   * 
   * @return true if the baselines have been set
   */
  public static boolean checkArray() {
    if (String.valueOf(baselines[0]).equals("")) {
      return false;
    }
      return true;
  }

}
