#!/usr/bin/env bash

set -euo pipefail
LOCAL_TGZ=jdk.tgz
REMOTE_TGZ=https://github.com/graalvm/oracle-graalvm-ea-builds/releases/download/jdk-22.0.0-ea.07/graalvm-jdk-22.0.0-ea.07_linux-x64_bin.tar.gz
sudo apt install wget
#wget $REMOTE_TGZ -O $LOCAL_TGZ

wget -O $LOCAL_TGZ $REMOTE_TGZ

mkdir -p "$JAVA_HOME"
rm -rf "$JAVA_HOME"
tar -zxf "$LOCAL_TGZ" -C .
mv "graalvm-jdk-22+36.1" "$JAVA_HOME"

#jextract --version
java --version
javac --version
native-image --version

