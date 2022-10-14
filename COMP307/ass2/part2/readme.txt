To run part 2, ensure both the .jar file and the data files are in the same directory.

Then in a local terminal execute the command: "java -jar part2.jar".

The results should be printed to the terminal and spat out into "output.txt" in the local directory.

As it turns out, the ECS server uses Java 11. As I have Java 17, I'm not sure how to make it run on the ECS systems.

In this case, recompile the code.

Do this with this sequence of commands:

/*
javac src/code/*.java -cp jgap_3.6.3_jar/jgap.jar -d bin

cd bin

jar cfm ../part2.jar ../manifest.txt code

cd ../

java -jar part2.jar
*/

William Kilty
300473541