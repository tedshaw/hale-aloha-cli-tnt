package edu.hawaii.halealohacli.command;

/**
 * Define the Command interface.
 * 
 * @author Toy Lim
 */
public interface Command {
  /**
   * Return the command String.
   * 
   * @return command String
   */
  public String toString();

  /**
   * Return the command syntax String.
   * 
   * @return syntax String
   */
  public String getSyntax();

  /**
   * Return the command description String.
   * 
   * @return description String
   */
  public String getDescription();

  /**
   * Execute the command given the list of arguments.
   * 
   * @param args List of String arguments
   * @return true if command was executed, false if error is encountered
   * @throws InvalidArgumentException if any of the arguments is invalid
   */
  public Boolean execute(String... args) throws InvalidArgumentException;
}