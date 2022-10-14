javac src/*.java -d bin

cd bin

jar cfm ../Ass4_challenge.jar ../manifest.txt *.class assets data

start "" java -jar ../Ass4_challenge.jar