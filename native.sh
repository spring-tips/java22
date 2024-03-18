#!/usr/bin/env bash

./mvnw clean
./mvnw -Pnative -DskipTests native:compile
./target/demo