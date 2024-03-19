#!/usr/bin/env bash

./mvnw -DskipTests spring-javaformat:apply
git commit -am up
git push