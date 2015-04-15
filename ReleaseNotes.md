# Version 2.0 #
added 3 functions:
  1. set-baseline
    * Sets the amount of energy used in each of the 24 hours of the specified day for the specified tower or lounge as a baseline.
  1. monitor-power
    * Prints out the current power consumption every user-defined number of seconds, until a carriage return is entered, which returns the user to the command loop.
  1. monitor-goal
    * If a baseline is set, takes a percentage of power consumption reduction as a goal and calculates whether that goal is being reached for the current hour, as compared to the day that the baseline is set upon.