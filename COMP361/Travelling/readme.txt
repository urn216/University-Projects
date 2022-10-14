To run part 2, ensure both the .jar file and the data files are in the same directory.

Then in a local terminal execute the commands:

/*
javac src/code/*.java src/code/math/*.java src/code/ui/*.java src/code/ui/elements/*.java src/code/ui/interactables/*.java -d bin

cd bin

jar cfm ../TSP.jar ../manifest.txt code

java -jar ../TSP.jar
*/

This will open a User Interface. Clicking through the menu should get you to start an instance of the algorithm, 
which should calculate away before your eyes. After 3,000 iterations, it will pause, and you can close the app
or press 'return to menu' and pick an instance to run again.

The results should be printed to the terminal and spat out into "output.txt" in the local directory.

William Kilty
300473541