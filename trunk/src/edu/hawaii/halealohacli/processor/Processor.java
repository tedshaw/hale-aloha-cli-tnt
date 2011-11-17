package edu.hawaii.halealohacli.processor;

import org.wattdepot.client.BadXmlException;
import org.wattdepot.client.MiscClientException;
import org.wattdepot.client.NotAuthorizedException;
import org.wattdepot.client.ResourceNotFoundException;
import edu.hawaii.halealohacli.command.SourceLatency;

/**
 * Processing class.
 * @author Ted
 *
 */
public class Processor {
  String command;

  /**
   * Constructor using command argument.
   * @param cmd Command string.
   */
  public Processor(String cmd) {
    this.command = cmd;
  }

  /**
   * 
   * @param args Arguments
   * @throws NotAuthorizedException Do nothing.
   * @throws ResourceNotFoundException Do nothing.
   * @throws BadXmlException Do nothing.
   * @throws MiscClientException Do nothing.
   */
  public static void main(String[] args) throws NotAuthorizedException, ResourceNotFoundException,
      BadXmlException, MiscClientException {
    Processor process = new Processor(args[1]);

    if ("exit".equals(process.command)) {
      System.exit(1);
    }
    else if ("latency".equals(process.command)) {
      SourceLatency.main(args);
    }
  }

}
