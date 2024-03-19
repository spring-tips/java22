#!/usr/bin/env bash

set -euo pipefail
LOCAL_TGZ=jdk.tgz
REMOTE_TGZ=https://github.com/graalvm/oracle-graalvm-ea-builds/releases/download/jdk-22.0.0-ea.07/graalvm-jdk-22.0.0-ea.07_linux-x64_bin.tar.gz
#export JAVA_HOME="$HOME/Desktop/jdk_home"
apt install wget || echo "could not install wget"

mkdir -p "$JAVA_HOME"
rm -rf "$JAVA_HOME"
ls -la $LOCAL_TGZ || wget -O $LOCAL_TGZ $REMOTE_TGZ
ls -la "$JAVA_HOME" || echo "could not stat $JAVA_HOME"
ls -la $LOCAL_TGZ || echo "could not find local downloaded, $LOCAL_TGZ"
tar -zxf "$LOCAL_TGZ" -C .
ls -la "$JAVA_HOME"
mv "graalvm-jdk-22+36.1" "$JAVA_HOME"

java --version
javac --version
native-image --version