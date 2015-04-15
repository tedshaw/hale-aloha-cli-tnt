**Table of Contents**


# 1.0 Download #
Please download the latest distribution of the Hale Aloha CLI from the [Downloads](http://code.google.com/p/hale-aloha-cli-tnt/downloads/list) page.

# 2.0 Install #
To install the Hale Aloha CLI, unzip the distribution.
Open a command prompt, and go to the extracted hale-aloha-cli-tnt directory.
Type "java -jar hale-aloha-cli.jar" to run the CLI.
Type "help" for a list of commands.

The following is sample input and output:

```
% java -jar hale-aloha-cli.jar
Connected successfully to the Hale Aloha WattDepot server.
> current-power Ilima-A
Ilima-A's power as of 2011-11-07 13:48:56 was 2.3 kW.
> daily-energy Mokihana 2011-11-05
Mokihana's energy consumption for 2011-11-05 was: 89 kWh.
> energy-since Lehua-E 2011-11-01
Total energy consumption by Lehua-E from 2011-11-01 00:00:00 to 2011-11-09 12:34:45 is: 345.2 kWh
> rank-towers 2011-11-01 2011-11-09
For the interval 2011-11-01 to 2011-11-09, energy consumption by tower was:
Mokihana  345 kWh
Ilima     389 kWh
Lehua     401 kWh
Lokelani  423 kWh
> help
Here are the available commands for this system.
current-power [tower | lounge]
Returns the current power in kW for the associated tower or lounge.
daily-energy [tower | lounge] [date]
Returns the energy in kWh used by the tower or lounge for the specified date (yyyy-mm-dd).
energy-since [tower | lounge] [date]
Returns the energy used since the date (yyyy-mm-dd) to now.
monitor-goal [tower | lounge] goal interval
Every [interval] seconds, prints out a timestamp, the current power being consumed by the [tower | lounge], and whether or not the lounge is meeting its power conservation goal.
monitor-power [tower | lounge] [interval]
This command prints out a timestamp and the current power for [tower | lounge] every [interval] seconds.  [interval] is an optional argument and defaults to 10 seconds. Hitting the Enter key stops this monitoring process and returns the user to the command loop.
rank-towers [start] [end]
Returns a list in sorted order from least to most energy consumed between the [start] and [end] date (yyyy-mm-dd)
set-baseline [tower | lounge] [date]
Defines [date] as the baseline and obtains and saves the amount of energy obtained hourly. 
The [date] parameter is optional and defaults to yesterday if excluded.
quit
Terminates execution
Note: towers are:  Mokihana, Ilima, Lehua, Lokelani
Lounges are the tower names followed by a "-" followed by one of A, B, C, D, E. For example, Mokihana-A.
> quit
%
```