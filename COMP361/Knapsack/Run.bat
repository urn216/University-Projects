javac src/code/*.java src/code/math/*.java src/code/ui/*.java src/code/ui/elements/*.java src/code/ui/interactables/*.java src/code/knapsacks/*.java -d bin

cd bin

jar cfm ../Knapsack.jar ../manifest.txt code

java -jar ../Knapsack.jar