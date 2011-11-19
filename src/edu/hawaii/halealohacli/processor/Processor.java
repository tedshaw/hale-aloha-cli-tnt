package edu.hawaii.halealohacli.processor;

import org.wattdepot.client.WattDepotClient;
import edu.hawaii.halealohacli.command.Command;
import edu.hawaii.halealohacli.command.CommandManager;
import edu.hawaii.halealohacli.command.InvalidArgumentException;

/**
 * Processing class.
 * 
 * @author Ted Shaw, Joshua Antonio
 * 
 */
public class Processor {
  CommandManager manager;

  /**
   * Constructor that separates user input into a command and a list of arguments.
   * 
   * @param client The WattDepotClient.
   */
  public Processor(WattDepotClient client) {
    manager = new CommandManager(client);
  }

  /**
   * Calls the appropriate command.
   * 
   * @param userInput User input from command line.
   */
  public void process(String[] userInput) {
    String commandString;
    String[] arguments = null;
    commandString = userInput[0];
    if (userInput.length > 1) {
      arguments = new String[userInput.length - 1];
      for (int i = 1; i < userInput.length; i++) {
        arguments[i - 1] = userInput[i];
      }
    }
    Boolean validCommand = false;
    if ("help".equals(commandString)) {
      validCommand = true;
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
    else {
      for (Command commandInstance : manager.getCommands()) {
        if (commandString.equals(commandInstance.toString())) {
          validCommand = true;
          try {
            commandInstance.execute(arguments);
          }
          catch (InvalidArgumentException e) {
            System.out.println("Error: Invalid Syntax. Please use the following.");
            System.out.println(commandInstance.getSyntax());
          }
        }
      }
    }
    if (!validCommand) {
      System.out.println("Error: Not a valid command. Type \"help\" for a list of commands.");
    }
  }

}
