package edu.hawaii.halealohacli.command;

import static org.junit.Assert.assertEquals;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wattdepot.client.WattDepotClient;

/**
 * Unit test for EnergySince Command. <br><br>
 * 
 * Note: <br>
 * Towers are: Mokihana, Ilima, Lehua, Lokelani <br>
 * Lounges are the tower names followed by a "-" followed by one of A, B, C, D, E. For example,
 * Mokihana-A.
 * 
 * @author Toy Lim
 */
public class TestEnergySince {

  private static String commandString;
  private static String commandSyntax;
  private static String commandDescription;
  private static WattDepotClient wattDepotClient;
  private static Command energySince;

  /**
   * Setup test by initializing Command.
   */
  @BeforeClass
  public static void testEnergySince() {
    commandString = "energy-since";
    commandSyntax = "energy-since [tower | lounge] [date]";
    commandDescription = "Returns the energy used since the date (yyyy-mm-dd) to now.";
    wattDepotClient = new WattDepotClient("http://server.wattdepot.org:8190/wattdepot/");
    energySince = new EnergySince(wattDepotClient);
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.EnergySince#toString()}.
   */
  @Test
  public void testToString() {
    assertEquals("Command String", commandString, energySince.toString());
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.EnergySince#getSyntax()}.
   */
  @Test
  public void testGetSyntax() {
    assertEquals("Command Syntax", commandSyntax, energySince.getSyntax());
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.EnergySince#getDescription()}.
   */
  @Test
  public void testGetDescription() {
    assertEquals("Command Description", commandDescription, energySince.getDescription());
  }

  /**
   * Test method for
   * {@link edu.hawaii.halealohacli.command.EnergySince#execute(java.lang.String[])}.
   */
  @AfterClass
  public static void testExecute() {
    String[] towers = { "Mokihana", "Ilima", "Lehua", "Lokelani" };
    String[] suffixes = { "", "-A", "-B", "-C", "-D", "-E" };
    for (String tower : towers) {
      for (String suffix : suffixes) {
        String source = tower + suffix;
        String date = "2011-11-23";
        try {
          energySince.execute(source, date);
        }
        catch (InvalidArgumentException e) {
          System.out.format("Error: %s\n", e.getMessage());
        }
      }
    }
  }

}
