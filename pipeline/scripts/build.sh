#!/usr/bin/env bash

set -eu

export GRADLE_USER_HOME="$(pwd)/.gradle"

version=$(cat version/version)

(
cd source
./gradlew -Pversion=${version} clean build --rerun-tasks --no-daemon
)

cp -a source/* dist/
