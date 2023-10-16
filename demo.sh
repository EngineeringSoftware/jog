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
java -cp "${JOG_JAR}" jog.Main Example.java
# Clean some stat files.
rm *-stats.yml
echo "Generated C++ code saved in gen-code/."
echo "Generated Tests saved in gen-tests/."
echo "Shadow relations reported in shadows.yml."
