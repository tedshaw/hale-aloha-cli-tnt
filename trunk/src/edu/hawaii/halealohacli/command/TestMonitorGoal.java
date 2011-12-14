package edu.hawaii.halealohacli.command;

import static org.junit.Assert.assertEquals;
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

  // variables for testCheckGoal() and testCheckInterval().
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
   * Test the checkSources() method.
   */
  @Test
  public void testCheckSource() {
    // An array of inputs that should pass.
    String[] passSources =
        { "Mokihana", "Ilima", "Lehua", "Lokelani", "Mokihana-A", "Ilima-B", "Lehua-C",
            "Lokelani-D" };
    // An array of inputs that should fail.
    String[] failSources =
        { "mokihana", "Ilima-F", "Mokihana-Lehua", "0", "foo", "Lehua-AB", "LokelaniD", "" };
    for (int i = 0; i < passSources.length - 1; i++) {
      assertTrue("Check pass source " + i, MonitorGoal.checkSource(passSources[i]));
      assertFalse("Check fail source " + i, MonitorGoal.checkSource(failSources[i]));
    }
  } // end testCheckSources().

  /**
   * Test the checkGoal() method.
   */
  @Test
  public void testCheckGoal() {
    assertTrue(message1, MonitorGoal.checkGoal(pass1));
    assertTrue(message1, MonitorGoal.checkGoal(pass2));
    assertTrue(message1, MonitorGoal.checkGoal(pass3));
    assertFalse(message1, MonitorGoal.checkGoal(fail1));
    assertFalse(message1, MonitorGoal.checkGoal(fail2));
    assertFalse(message1, MonitorGoal.checkGoal(fail3));
    assertFalse(message1, MonitorGoal.checkGoal(fail4));
  } // end testCheckGoal().

  /**
   * Test the checkInterval() method.
   */
  @Test
  public void testCheckInterval() {
    assertTrue(message2, MonitorGoal.checkInterval(pass1));
    assertTrue(message2, MonitorGoal.checkInterval(pass2));
    assertTrue(message2, MonitorGoal.checkInterval(pass3));
    assertFalse(message2, MonitorGoal.checkInterval(fail1));
    assertFalse(message2, MonitorGoal.checkInterval(fail3));
    assertFalse(message2, MonitorGoal.checkInterval(fail4));
  } // end testCheckInterval().

  /**
   * Test the checkAll() method.
   * 
   * @throws InvalidArgumentException If an invalid argument is given.
   */
  @Test
  public void testCheckAll() throws InvalidArgumentException {
    String[] args1 = { "Ilima", "foo", "10" };
    String[] args2 = { "Lehua", "5", "bar" };
    String[] args3 = { "Lehua", "5", "10" };
    String[] args4 = { "qux", "bar", "baz" };
    String testMsg = "Check args.";
    String sourceMsg =
        "Please enter a valid tower or lounge name. Type 'help' to see the available options.\n";
    String goalMsg = "You must enter an integer between 1 and 99 (inclusive) as a goal.\n";
    String intervalMsg = "You must enter an integer greater than 0 as the interval.\n";
    String baselineMsg = "You must set the baseline first. Enter 'help' to learn how to do this.\n";
    StringBuilder sb = new StringBuilder();

    assertEquals(testMsg, MonitorGoal.checkAll(args1), sb.append(goalMsg).append(baselineMsg)
        .toString());
    sb.setLength(0);
    assertEquals(testMsg, MonitorGoal.checkAll(args2), sb.append(intervalMsg).append(baselineMsg)
        .toString());
    sb.setLength(0);
    assertEquals(testMsg, MonitorGoal.checkAll(args3), sb.append(baselineMsg).toString());
    sb.setLength(0);
    assertEquals(testMsg, MonitorGoal.checkAll(args4),
        sb.append(sourceMsg).append(goalMsg).append(intervalMsg).append(baselineMsg).toString());
  } // end testCheckAll().
  
  
  /**
   * Test the calculateGoal() method.
   */
  @Test
  public void testCalculateGoal() {
    // Only valid values should be used here.
    int goal1 = 1;
    int goal2 = 5;
    int goal3 = 45;
    int goal4 = 72;
    int goal5 = 99;
    double baseline1 = 30.0;
    double baseline2 = 10.0;
    double baseline3 = 100.0;
    String testMsg = "Check calculated goal.";
    
    assertEquals(testMsg, MonitorGoal.calculateGoal(goal1, baseline1), 29.7, 0.1);
    assertEquals(testMsg, MonitorGoal.calculateGoal(goal2, baseline2), 9.5, 0.1);
    assertEquals(testMsg, MonitorGoal.calculateGoal(goal3, baseline3), 55.0, 0.1);
    assertEquals(testMsg, MonitorGoal.calculateGoal(goal4, baseline2), 2.8, 0.1);
    assertEquals(testMsg, MonitorGoal.calculateGoal(goal5, baseline3), 1.0, 0.1);
  } // end testCalculateGoal().
  
  
  /**
   * Test the isGoalBeingMet() method.
   */
  @Test
  public void testIsGoalBeingMet() {
    double goal1 = 29.7;
    double goal2 = 100.0;
    double current1 = 30.2;
    double current2 = 99.0;
    String testMsg = "Check goal and current usage.";
    
    assertFalse(testMsg, MonitorGoal.isGoalBeingMet(goal1, current1));
    assertTrue(testMsg, MonitorGoal.isGoalBeingMet(goal2, current2));
  } // end testIsGoalBeingMet().

} // end TestMonitorGoal.
