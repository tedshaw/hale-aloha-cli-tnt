package edu.hawaii.halealohacli.command;

import static org.junit.Assert.assertEquals;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wattdepot.client.WattDepotClient;

/**
 * Unit test for MonitorPower Command. <br><br>
 * 
 * Note: <br>
 * Towers are: Mokihana, Ilima, Lehua, Lokelani <br>
 * Lounges are the tower names followed by a "-" followed by one of A, B, C, D, E. For example,
 * Mokihana-A.
 * 
 * @author Original Author Toy Lim
 * @author Modified by Ardell Klemme
 */
public class TestMonitorPower {
  private static String commandString;
  private static String commandSyntax;
  private static String commandDescription;
  private static WattDepotClient wattDepotClient;
  private static MonitorPower monitorPower;

  /**
   * Setup test by initializing Command.
   */
  @BeforeClass
  public static void testMonitorPower() {
    commandString = "monitor-power";
    commandSyntax = "monitor-power [tower | lounge] [interval]";
    commandDescription = "This command prints out a timestamp and the current power for "
        + "[tower | lounge] every [interval] seconds.  [interval] is an "
        + "optional argument and defaults to 10 seconds. Hitting the "
        + "Enter key stops this monitoring process and returns the user "
        + "to the command loop.";
    wattDepotClient = new WattDepotClient("http://server.wattdepot.org:8190/wattdepot/");
    monitorPower = new MonitorPower(wattDepotClient);
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.MonitorPower#toString()}.
   */
  @Test
  public void testToString() {
    assertEquals("Command String", commandString, monitorPower.toString());
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.MonitorPower#getSyntax()}.
   */
  @Test
  public void testGetSyntax() {
    assertEquals("Command Syntax", commandSyntax, monitorPower.getSyntax());
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.MonitorPower#getDescription()}.
   */
  @Test
  public void testGetDescription() {
    assertEquals("Command Description", commandDescription, monitorPower.getDescription());
  }

  /**
   * Test method for
   * {@link edu.hawaii.halealohacli.command.MonitorPower#execute(java.lang.String[])}.
   */
  @AfterClass
  public static void testExecute() {
    String[] towers = { "Mokihana", "Ilima", "Lehua", "Lokelani" };
    String[] suffixes = { "", "-A", "-B", "-C", "-D", "-E" };
    String[] intervals = {"10", "5", "25"};
    monitorPower.setDebug(true);
    
    for (String tower : towers) {
      for (String suffix : suffixes) {
        for (String interval : intervals) {
          String source = tower + suffix;
          try {
            monitorPower.execute(source, interval);
          }
          catch (InvalidArgumentException e) {
            System.out.format("Error: %s\n", e.getMessage());
          }
        }
      }
    }
  }

}
