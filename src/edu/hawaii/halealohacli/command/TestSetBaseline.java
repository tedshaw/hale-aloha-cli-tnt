package edu.hawaii.halealohacli.command;

import static org.junit.Assert.assertEquals;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wattdepot.client.WattDepotClient;

/**
 * Unit test for SetBaseline Command. <br><br>
 * 
 * Note: <br>
 * Towers are: Mokihana, Ilima, Lehua, Lokelani <br>
 * Lounges are the tower names followed by a "-" followed by one of A, B, C, D, E. For example,
 * Mokihana-A.
 * 
 * @author Matthew Mizumoto
 */
public class TestSetBaseline {
  private static String commandString;
  private static String commandSyntax;
  private static String commandDescription;
  private static WattDepotClient wattDepotClient;
  private static Command setBaseline;

  /**
   * Setup test by initializing Command.
   */
  @BeforeClass
  public static void testCurrentPower() {
    commandString = "set-baseline";
    commandSyntax = "set-baseline [tower | lounge] [date]";
    commandDescription =
        "Defines [date] as the baseline and obtains and saves "
            + "the amount of energy obtained hourly. \n"
            + "The [date] parameter is optional and defaults to yesterday if excluded.";    
    wattDepotClient = new WattDepotClient("http://server.wattdepot.org:8190/wattdepot/");
    setBaseline = new SetBaseline(wattDepotClient);
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.CurrentPower#toString()}.
   */
  @Test
  public void testToString() {
    assertEquals("Command String", commandString, setBaseline.toString());
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.CurrentPower#getSyntax()}.
   */
  @Test
  public void testGetSyntax() {
    assertEquals("Command Syntax", commandSyntax, setBaseline.getSyntax());
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.CurrentPower#getDescription()}.
   */
  @Test
  public void testGetDescription() {
    assertEquals("Command Description", commandDescription, setBaseline.getDescription());
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
          setBaseline.execute(source);
        }
        catch (InvalidArgumentException e) {
          System.out.format("Error: %s\n", e.getMessage());
        }
      }
    }
  }

}
