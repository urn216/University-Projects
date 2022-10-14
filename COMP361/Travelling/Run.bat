javac src/code/*.java src/code/math/*.java src/code/ui/*.java src/code/ui/elements/*.java src/code/ui/interactables/*.java -d bin

cd bin

jar cfm ../TSP.jar ../manifest.txt code

java -jar ../TSP.jar