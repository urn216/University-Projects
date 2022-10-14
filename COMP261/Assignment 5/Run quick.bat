javac src/*.java -d bin

cd bin

java LempelZivCompress ../data/test1.txt > ../data/compressed.txt

java LempelZivDecompress ../data/compressed.txt

pause
