javac src/code/*.java -cp jgap_3.6.3_jar/jgap.jar -d bin

cd bin

jar cfm ../part2.jar ../manifest.txt code

cd ../


java -jar part2.jar 

pause