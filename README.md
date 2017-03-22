# FloatingClock
Java Floating clock to sit in the corner of the screen above all other programs to display the current system time. 

FloatingClock is a simple utility application written in Java that displays the current system time in the top right of the screen. 
It provides light customisation such as opacity, color and font size. Only tested on Windows 8.1. 

Multi-screen support added. The program will default to the primary screen but can be set to display on any screen via the settings pane.

# Usage
Navigate to directory above package in terminal (e.g. If package is at c:\FloatingClock, navigate to C:\)

Compile all source in package from outside of package:

javac FloatingClock/*.java

then run main class:
java FloatingClock/FloatingClock

Alternatively, compile all source and then package into runnable jar:

javac FloatingClock/*.java

jar cvfeP FloatingClock.jar FloatingClock.FloatingClock -C . FloatingClock/

