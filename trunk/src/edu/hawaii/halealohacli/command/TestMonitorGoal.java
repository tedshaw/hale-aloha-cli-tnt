package edu.hawaii.halealohacli.command;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test class for MonitorGoal.
 * 
 * @author Terrence Chida
 * 
 */
public class TestMonitorGoal {

  String pass1 = "1";
  String pass2 = "99";
  String pass3 = "50";
  String fail1 = "0";
  String fail2 = "100";
  String fail3 = "5.5";
  String fail4 = "x";
  String message1 = "Check goal argument.";
  String message2 = "Check interval argument.";

  /**
   * Test the checkGoal() method.
   * 
   * @throws InvalidArgumentException If an invalid argument is given.
   */
  @Test
  public void testCheckGoal() throws InvalidArgumentException {
    assertTrue(message1, MonitorGoal.checkGoal(pass1));
    assertTrue(message1, MonitorGoal.checkGoal(pass2));
    assertTrue(message1, MonitorGoal.checkGoal(pass3));
    assertFalse(message1, MonitorGoal.checkGoal(fail1));
    assertFalse(message1, MonitorGoal.checkGoal(fail2));
    assertFalse(message1, MonitorGoal.checkGoal(fail3));
    assertFalse(message1, MonitorGoal.checkGoal(fail4));
  }

  /**
   * Test the checkInterval() method.
   * 
   * @throws InvalidArgumentException If an invalid argument is given.
   */
  @Test
  public void testCheckInterval() throws InvalidArgumentException {
    assertTrue(message2, MonitorGoal.checkInterval(pass1));
    assertTrue(message2, MonitorGoal.checkInterval(pass2));
    assertTrue(message2, MonitorGoal.checkInterval(pass3));
    assertFalse(message2, MonitorGoal.checkInterval(fail1));
    assertFalse(message2, MonitorGoal.checkInterval(fail3));
    assertFalse(message2, MonitorGoal.checkInterval(fail4));
  }

} // end TestMonitorGoal.
