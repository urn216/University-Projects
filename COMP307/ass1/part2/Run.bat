javac src/code/*.java src/code/math/*.java -d bin

cd bin

jar cfm ../part2.jar ../manifest.txt code

cd ../

cls

java -jar part2.jar data/hepatitis-training data/hepatitis-test

pause