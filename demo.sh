#!/bin/bash

# Build
echo "Building JOG..."
./tool/install.sh

# Clean previously generated
rm -fr gen-code gen-tests

# Run
echo "Reading the patterns from file Example.java..."
java -jar tool/jog.jar Example.java
