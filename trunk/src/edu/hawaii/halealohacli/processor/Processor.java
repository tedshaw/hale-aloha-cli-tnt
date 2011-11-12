package edu.hawaii.halealohacli.processor;

import org.wattdepot.client.BadXmlException;
import org.wattdepot.client.MiscClientException;
import org.wattdepot.client.NotAuthorizedException;
import org.wattdepot.client.ResourceNotFoundException;
import edu.hawaii.halealohacli.command.SourceLatency;

public class Processor {
  String command;

  public Processor(String cmd) {
    this.command = cmd;
  }

  /**
   * 
   * @param args
   * @throws NotAuthorizedException
   * @throws ResourceNotFoundException
   * @throws BadXmlException
   * @throws MiscClientException
   */
  public static void main(String[] args) throws NotAuthorizedException, ResourceNotFoundException, BadXmlException, MiscClientException {
    Processor process = new Processor(args[1]);
    
    if (process.command.equals("exit")) {
      System.exit(1);
    }
    else if(process.command.equals("latency")){
      SourceLatency.main(args);
    }

  }

}
