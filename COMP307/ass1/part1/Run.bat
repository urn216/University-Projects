javac src/code/*.java src/code/math/*.java -d bin

cd bin

jar cfm ../part1.jar ../manifest.txt code

cd ../

cls

java -jar part1.jar wine-training wine-test

pause