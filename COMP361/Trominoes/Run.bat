javac src/code/*.java -d bin

cd bin

jar cfm ../Trominoes.jar ../manifest.txt code

start "" java -jar ../Trominoes.jar

pause