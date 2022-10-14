To run part 2, ensure both the .jar file and the files folder are in the same directory.

Then in a local terminal execute the command: "java -jar part2.jar".

The results should be spat out into "n32-k5nn.sol" and "n32-k5sv.sol" in the files directory.

If you wish to swap the input data, you will need to edit Core.java and which String inst is commented out, then recompile.

As it turns out, the ECS server uses Java 11. As I have Java 17, I'm not sure how to make it run on the ECS systems.

In this case, recompile the code.

Do this with this sequence of commands:

/*
javac src/*.java -d bin

cd bin

jar cfm ../part2.jar ../manifest.txt ./

cd ../

java -jar part2.jar
*/

William Kilty
300473541