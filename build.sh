#!/bin/bash

rm build -rdf;
mkdir build/compiled -p;
cd ./src/main/java;
javac org/gitcloner/Main.java -d ../../../build/compiled;
cd ../../../build/compiled;
jar cfe GitCloner.jar org.gitcloner.Main org/gitcloner/Main.class;
