build:
	javac -d ./bin ./src/*.java

run:
	java -cp ./bin Homework ${ARGS}

clean:
	rm -fr ./bin
