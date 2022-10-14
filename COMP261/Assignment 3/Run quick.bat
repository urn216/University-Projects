javac -cp .;./ecs100.jar src/*.java -d bin

cd bin

start "" java -cp .;./../ecs100.jar SoundWaveform
