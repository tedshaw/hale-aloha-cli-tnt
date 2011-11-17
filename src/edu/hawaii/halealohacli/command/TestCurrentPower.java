package edu.hawaii.halealohacli.command;

import static org.junit.Assert.assertEquals;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wattdepot.client.WattDepotClient;

/**
 * Unit test for CurrentPower Command.
 * 
 * Note: <br>
 * Towers are: Mokihana, Ilima, Lehua, Lokelani <br>
 * Lounges are the tower names followed by a "-" followed by one of A, B, C, D, E. For example,
 * Mokihana-A.
 * 
 * @author Toy Lim
 */
public class TestCurrentPower {
  private static String commandString;
  private static String commandSyntax;
  private static String commandDescription;
  private static WattDepotClient wattDepotClient;
  private static Command currentPower;

  /**
   * Setup test by initializing Command.
   * 
   * @throws java.lang.Exception possible exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    commandString = "current-power";
    commandSyntax = "current-power [tower | lounge]";
    commandDescription = "Returns the current power in kW for the associated tower or lounge.";
    wattDepotClient = new WattDepotClient("http://server.wattdepot.org:8190/wattdepot/");
    currentPower = new CurrentPower(wattDepotClient);
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.CurrentPower#toString()}.
   */
  @Test
  public void testToString() {
    assertEquals("Command String", commandString, currentPower.toString());
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.CurrentPower#getSyntax()}.
   */
  @Test
  public void testGetSyntax() {
    assertEquals("Command Syntax", commandSyntax, currentPower.getSyntax());
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.CurrentPower#getDescription()}.
   */
  @Test
  public void testGetDescription() {
    assertEquals("Command Description", commandDescription, currentPower.getDescription());
  }

  /**
   * Test method for
   * {@link edu.hawaii.halealohacli.command.CurrentPower#execute(java.lang.String[])}.
   */
  @AfterClass
  public static void testExecute() {
    String[] towers = { "Mokihana", "Ilima", "Lehua", "Lokelani" };
    String[] suffixes = { "", "-A", "-B", "-C", "-D", "-E" };
    for (String tower : towers) {
      for (String suffix : suffixes) {
        String source = tower + suffix;
        try {
          currentPower.execute(source);
        }
        catch (InvalidArgumentException e) {
          System.out.format("Error: %s\n", e.getMessage());
        }
      }
    }
  }

}
