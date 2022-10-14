To run part 3, ensure both the .jar file and the data file are in the same directory.

Then in a local terminal execute the command: "java -jar part3.jar ionosphere.data".

The results should be printed to the terminal and spat out into "output.txt" in the local directory.

As it turns out, the ECS server uses Java 11. As I have Java 17, I'm not sure how to make it run on the ECS systems.

In this case, recompile the code.

Do this with this sequence of commands:

/*
javac src/code/*.java src/code/math/*.java -d bin

cd bin

jar cfm ../part3.jar ../manifest.txt code

cd ../

java -jar part3.jar ionosphere.data"
*/

There is additionally the option to run with the split training and testing data, where it converges at around 80%

William Kilty
300473541