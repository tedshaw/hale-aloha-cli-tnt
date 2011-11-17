package edu.hawaii.halealohacli.command;

import static org.junit.Assert.assertEquals;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wattdepot.client.WattDepotClient;

/**
 * Unit test for DailyEnergy Command. <br><br>
 * 
 * Note: <br>
 * Towers are: Mokihana, Ilima, Lehua, Lokelani <br>
 * Lounges are the tower names followed by a "-" followed by one of A, B, C, D, E. For example,
 * Mokihana-A.
 * 
 * @author Joshua Antonio
 */
public class TestDailyEnergy {

  private static String commandString;
  private static String commandSyntax;
  private static String commandDescription;
  private static WattDepotClient wattDepotClient;
  private static Command dailyEnergy;

  /**
   * Setup test by initializing Command.
   */
  @BeforeClass
  public static void testEnergySince() {
    commandString = "daily-energy";
    commandSyntax = commandString + " [tower | lounge] [date]";
    commandDescription = "Returns the energy in kWh used by the tower or lounge for the " 
                         + "specified date (yyyy-mm-dd).";
    
    wattDepotClient = new WattDepotClient("http://server.wattdepot.org:8190/wattdepot/");
    dailyEnergy = new DailyEnergy(wattDepotClient);
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.DailyEnergy#toString()}.
   */
  @Test
  public void testToString() {
    assertEquals("Command String", commandString, dailyEnergy.toString());
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.DailyEnergy#getSyntax()}.
   */
  @Test
  public void testGetSyntax() {
    assertEquals("Command Syntax", commandSyntax, dailyEnergy.getSyntax());
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.DailyEnergy#getDescription()}.
   */
  @Test
  public void testGetDescription() {
    assertEquals("Command Description", commandDescription, dailyEnergy.getDescription());
  }

  /**
   * Test method for
   * {@link edu.hawaii.halealohacli.command.DailyEnergy#execute(java.lang.String[])}.
   */
  @AfterClass
  public static void testExecute() {
    String[] towers = { "Mokihana", "Ilima", "Lehua", "Lokelani" };
    String[] suffixes = { "", "-A", "-B", "-C", "-D", "-E" };
    for (String tower : towers) {
      for (String suffix : suffixes) {
        String source = tower + suffix;
        String date = "2011-11-17";
        try {
          dailyEnergy.execute(source, date);
        }
        catch (InvalidArgumentException e) {
          System.out.format("Error: %s\n", e.getMessage());
        }
      }
    }
  }

}
