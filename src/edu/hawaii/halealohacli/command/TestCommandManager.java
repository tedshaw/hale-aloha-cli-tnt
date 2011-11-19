package edu.hawaii.halealohacli.command;

import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wattdepot.client.WattDepotClient;

/**
 * Unit test for CommandManager.
 * 
 * @author Toy Lim
 */
public class TestCommandManager {
  private static WattDepotClient wattDepotClient;
  private static CommandManager manager;

  /**
   * Setup test by initializing CommandManager.
   */
  @BeforeClass
  public static void testEnergySince() {
    wattDepotClient = new WattDepotClient("http://server.wattdepot.org:8190/wattdepot/");
    manager = new CommandManager(wattDepotClient);
  }

  /**
   * Test the getCommands() function.
   */
  @Test
  public void testGetCommands() {
    List<String> commandStrings =
        Arrays.asList("current-power", "daily-energy", "energy-since", "rank-towers", "help");
    Command[] commands = manager.getCommands();
    for (Command command : commands) {
      if (!commandStrings.contains(command.toString())) {
        System.out.format("Command %s not in list.\n", command.toString());
      }
      assertTrue(command.toString() + "is in list of command strings.",
          commandStrings.contains(command.toString()));
    }
  }

  /**
   * Test method for {@link edu.hawaii.halealohacli.command.Command#execute(java.lang.String[])}.
   */
  @AfterClass
  public static void testExecute() {
    String[] towers = { "Mokihana", "Ilima", "Lehua", "Lokelani" };
    String[] suffixes = { "", "-A", "-B", "-C", "-D", "-E" };
    for (String tower : towers) {
      for (String suffix : suffixes) {
        String source = tower + suffix;
        String date = "2011-11-01";
        Command[] commands = manager.getCommands();
        for (Command command : commands) {
          try {
            if (command.toString().equals("current-power")) {
              command.execute(source);
            }
            else if (command.toString().equals("daily-energy")
                || command.toString().equals("energy-since")) {
              command.execute(source, date);
            }
          }
          catch (InvalidArgumentException e) {
            System.out.format("Error: %s\n", e.getMessage());
          }
        }
      }
    }
    Command[] commands = manager.getCommands();
    for (Command command : commands) {
      if (command.toString().equals("rank-towers")) {
        try {
          command.execute("2011-11-01", "2011-11-09");
        }
        catch (InvalidArgumentException e) {
          System.out.format("Error: %s\n", e.getMessage());
        }
      }
    }
  }
}
