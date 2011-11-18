package edu.hawaii.halealohacli.command;

import static org.junit.Assert.assertEquals;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wattdepot.client.WattDepotClient;

/**
 * Unit test for RankTowers Command.
 * 
 * Towers are: Mokihana, Ilima, Lehua, Lokelani Lounges are the tower names followed by a "-"
 * followed by one of A, B, C, D, E. For example, Mokihana-A.
 * 
 * @author Ted Shaw
 */
public class TestRankTowers {

  private static String commandString;
  private static String commandSyntax;
  private static String commandDescription;
  private static WattDepotClient wattDepotClient;
  private static Command rankTowers;

  /**
   * Setup test by initializing Command.
   */
  @BeforeClass
  public static void testEnergySince() {
    commandString = "rank-towers";
    commandSyntax = "rank-towers [start] [end]";
    commandDescription =
        "Returns a list in sorted order from least to most energy consumed between the [start] "
            + "and [end] date (yyyy-mm-dd)";
    wattDepotClient = new WattDepotClient("http://server.wattdepot.org:8190/wattdepot/");
    rankTowers = new RankTowers(wattDepotClient);
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.RankTowers#toString()}.
   */
  @Test
  public void testToString() {
    assertEquals("Command String", commandString, rankTowers.toString());
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.RankTowers#getSyntax()}.
   */
  @Test
  public void testGetSyntax() {
    assertEquals("Command Syntax", commandSyntax, rankTowers.getSyntax());
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.EnergySince#getDescription()}.
   */
  @Test
  public void testGetDescription() {
    assertEquals("Command Description", commandDescription, rankTowers.getDescription());
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.RankTowers#execute(java.lang.String[])}
   * .
   */
  @AfterClass
  public static void testExecute() {
    String start = "2011-11-03";
    String end = "2011-11-04";
    try {
      rankTowers.execute(start, end);
    }
    catch (InvalidArgumentException e) {
      System.out.format("Error: %s\n", e.getMessage());
    }
  }

}
