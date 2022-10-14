javac src/code/*.java src/code/math/*.java -d bin

cd bin

jar cfm ../part3.jar ../manifest.txt code

cd ../

cls

java -jar part3.jar ionosphere.data

pause