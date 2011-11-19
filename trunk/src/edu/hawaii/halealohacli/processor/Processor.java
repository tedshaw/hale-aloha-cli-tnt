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
  String command;
  String[] arguments = null;
  
  /**
   * Constructor that separates user input into a command and a list of arguments.
   * 
   * @param userInput User input from command line.
   */
  public Processor(String[] userInput) {
    this.command = userInput[0];
    if (userInput.length > 1) {
      this.arguments = new String[userInput.length - 1];
      for (int i = 1; i < userInput.length; i++) {
        this.arguments[i - 1] = userInput[i];
      }
    }
  }

  /**
   * Calls the appropriate command.
   * 
   * @param client The WattDepotClient.
   */
  public void process(WattDepotClient client) {
    CommandManager manager = new CommandManager(client);
    Boolean validCommand = false;
    if (this.command.equals("help")) {
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
        if (this.command.equals(commandInstance.toString())) {
          validCommand = true;
          try {
            commandInstance.execute(arguments);
          }
          catch (InvalidArgumentException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            System.out.println("Error: Invalid Syntax. Please use the following.");
            System.out.println(commandInstance.getSyntax());
          }
        }
      }
    }
    if (!validCommand) {
      System.out.println("Error: Not a valid command. Type help for a list of commands.");
    }
  }

}
