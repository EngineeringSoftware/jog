#!/bin/bash

VERSION="$( grep '^version ' tool/build.gradle | cut -d' ' -f2 | tr -d "'" )"
JOG_JAR="tool/build/libs/jog-${VERSION}-all.jar"

# Build
echo "Building JOG..."
pushd "tool" >/dev/null
./gradlew -q shadowJar
popd >/dev/null

# Clean previously generated
rm -fr gen-code gen-tests

# Run
echo "Reading the patterns from file Example.java..."
java -jar "${JOG_JAR}" Example.java
