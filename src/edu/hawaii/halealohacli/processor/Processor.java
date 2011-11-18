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
    for (Command commandInstance : manager.getCommands()) {
      if (this.command.equals(commandInstance.toString())) {
        try {
          commandInstance.execute(arguments);
        }
        catch (InvalidArgumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

}
