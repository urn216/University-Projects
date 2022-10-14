javac src/*.java -d bin

cd bin

jar cfm ../part2.jar ../manifest.txt ./

cd ../

cls

java -jar part2.jar

pause