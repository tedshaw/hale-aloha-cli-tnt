package edu.hawaii.halealohacli.command;

import java.util.List;
import java.util.Arrays;
import org.wattdepot.client.WattDepotClient;

/**
 * return a list of instances of all the Command classes.
 * 
 * @author Toy Lim
 */
public class CommandManager {
  private List<Command> commandList;

  /**
   * Default constructor.
   * 
   * @param client WattDepotClient client for data query
   */
  public CommandManager(WattDepotClient client) {
    commandList =
        Arrays.asList(new CurrentPower(client), new DailyEnergy(client), new EnergySince(client),
            new RankTowers(client), new Help(client));
  }

  /**
   * Return a list of instances of all the Command classes.
   * 
   * @return an array of instances of all the Command classes
   */
  public Command[] getCommands() {
    return commandList.toArray(new Command[commandList.size()]);
  }
}
