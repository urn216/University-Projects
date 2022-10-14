javac src/*.java -d bin

cd bin

java LempelZivCompress ../data/war_and_peace.txt > ../data/compressed.txt

java LempelZivDecompress ../data/compressed.txt > ../data/decom.txt

pause
