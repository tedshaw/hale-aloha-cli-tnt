package edu.hawaii.halealohacli;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import org.wattdepot.client.WattDepotClient;

/**
 * Main class of command line interface.
 * @author Ted
 *
 */
public class Main {

  /**
   * Main method.
   * @param args Argument.
   */
  public static void main(String[] args) {
    String url = "http://server.wattdepot.org:8190/wattdepot/";
    WattDepotClient client = new WattDepotClient(url);

    // Check to make sure a connection can be made.
    // If no connection, then exit right now.
    if (client.isHealthy()) {
      System.out.format("Connected successfully to: %s%n", url);
    }
    else {
      System.out.format("Could not connect to: %s%n", url);
      return;
    }

    // open up standard input
    while (true) {
      // prompt the user to enter their name
      System.out.print("Enter a command: ");

      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

      String command = null;

      // read the command from the command-line; need to use try/catch with the
      // readLine() method
      try {
        command = br.readLine();
      }
      catch (IOException ioe) {
        System.out.println("IO error trying to read your command.");
        //System.exit(1);
      }

      System.out.println("Thanks for the command " + command);
    }

  }

}
