package edu.hawaii.halealohacli.command;

/**
 * InvalidArgumentException: whenever an argument parser encounters an invalid argument, this
 * exception should be thrown.
 * 
 * @author Toy Lim
 */
public class InvalidArgumentException extends Exception {
  /**
   * Default constructor.
   */
  public InvalidArgumentException() {
    super("The given argument is invalid.");
  }

  /**
   * Constructor with a custom message.
   * 
   * @param message Error message String
   */
  public InvalidArgumentException(String message) {
    super(message);
  }

  /**
   * Constructor with a custom message and Throwable cause trace.
   * 
   * @param message Error message String
   * @param cause Throwable cause trace
   */
  public InvalidArgumentException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Determines if a de-serialized file is compatible with this class.
   * 
   * Maintainers must change this value if and only if the new version of this class is not
   * compatible with old versions. See Sun docs for <a
   * href=http://java.sun.com/products/jdk/1.1/docs/guide /serialization/spec/version.doc.html>
   * details. </a>
   * 
   * Not necessary to include in first version of the class, but included here as a reminder of its
   * importance.
   */
  private static final long serialVersionUID = 1790884005718859161L;
}
