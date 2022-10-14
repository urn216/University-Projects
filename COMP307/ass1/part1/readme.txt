To run part 1, ensure both the .jar file and the data files are in the same directory.

Then in a local terminal execute the command: "java -jar part1.jar wine-training wine-test".

The results should be printed to the terminal and spat out into "output.txt" in the local directory.

If you wish to change the K value you will have to recompile the code, as it is hard-coded in.

As it turns out, the ECS server uses Java 11. As I have Java 17, I'm not sure how to make it run on the ECS systems.

In this case, recompile the code.

Do this with this sequence of commands:

/*
javac src/code/*.java src/code/math/*.java -d bin

cd bin

jar cfm ../part1.jar ../manifest.txt code

cd ../

java -jar part1.jar wine-training wine-test
*/

William Kilty
300473541