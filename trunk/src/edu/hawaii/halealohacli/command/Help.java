package edu.hawaii.halealohacli.command;

import org.wattdepot.client.WattDepotClient;

/**
 * Returns a list of available commands.
 * 
 * @author Joshua Antonio
 * 
 */
public class Help implements Command {
  private String commandString;
  private String commandSyntax;
  private String commandDescription;
  private WattDepotClient wattDepotClient;

  /**
   * Default constructor.
   * 
   * @param wattDepotClient The WattDepot client
   */
  public Help(WattDepotClient wattDepotClient) {
    this.commandString = "help";
    this.commandSyntax = "help";
    this.commandDescription = "Returns a list of the available commands for this system.";
    this.wattDepotClient = wattDepotClient;
  }

  /**
   * Return the command string.
   * 
   * @return commandString
   */
  @Override
  public String toString() {
    return this.commandString;
  }

  /**
   * Return the command syntax.
   * 
   * @return commandSyntax
   */
  @Override
  public String getSyntax() {
    return this.commandSyntax;
  }

  /**
   * Return the command description.
   * 
   * @return commandDescription.
   */
  @Override
  public String getDescription() {
    return this.commandDescription;
  }

  /**
   * Print the list of available commands with the syntax and description.
   * 
   * @param args String[] arguments
   * @throws InvalidArgumentException if any of the arguments are invalid
   */
  @Override
  public void execute(String... args) throws InvalidArgumentException {
    CommandManager manager = new CommandManager(this.wattDepotClient);
    System.out.println("Here are the available commands for this system.");
    for (Command command : manager.getCommands()) {
      if (!command.getSyntax().equals("help")) {
        System.out.println(command.getSyntax());
        System.out.println(command.getDescription());
      }
    }
    System.out.println("quit");
    System.out.println("Terminates execution");
    System.out.println("Note: towers are:  Mokihana, Ilima, Lehua, Lokelani");
    System.out.println("Lounges are the tower names followed by a \"-\" followed by one of "
        + "A, B, C, D, E. For example, Mokihana-A.");
  }
}
