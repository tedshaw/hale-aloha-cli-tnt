package edu.hawaii.halealohacli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.wattdepot.client.WattDepotClient;
import edu.hawaii.halealohacli.processor.Processor;

/**
 * Main class of command line interface.
 * 
 * @author Ted Shaw, Joshua Antonio
 * 
 */
public class Main {

  /**
   * Main method.
   * 
   * @param args Argument.
   */
  public static void main(String[] args) {
    String url = "http://server.wattdepot.org:8190/wattdepot/";
    WattDepotClient client = new WattDepotClient(url);
    Processor processor = new Processor(client);

    // Check to make sure a connection can be made.
    // If no connection, then exit right now.
    if (client.isHealthy()) {
      System.out.format("Connected successfully to: %s%n", url);
    }
    else {
      System.out.format("Could not connect to: %s%n", url);
      return;
    }
    
    while (true) {
      System.out.print("Enter a command: ");

      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      String command = null;

      try {
        command = br.readLine();
      }
      catch (IOException ioe) {
        System.out.println("IO error trying to read your command.");
      }

      if ("quit".equals(command)) {
        return;
      }

      String[] userInput = command.split("\\s+");
       
      processor.process(userInput);
    }

  }

}
