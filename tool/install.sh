#!/bin/bash

readonly _DIR="$( cd -P "$( dirname "$( readlink -f "${BASH_SOURCE[0]}" )" )" && pwd )"

pushd $_DIR >/dev/null
./gradlew shadowjar
cp build/libs/jog-0.0.1-all.jar jog.jar
popd >/dev/null
