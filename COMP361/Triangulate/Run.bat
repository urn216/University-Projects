javac src/code/*.java src/code/shapes/*.java -d bin

cd bin

jar cfm ../Triangulate.jar ../manifest.txt code

start "" java -jar ../Triangulate.jar

pause