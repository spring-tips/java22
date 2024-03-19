#!/usr/bin/env bash

apt install wget
wget https://github.com/graalvm/oracle-graalvm-ea-builds/releases/download/jdk-22.0.0-ea.07/graalvm-jdk-22.0.0-ea.07_linux-x64_bin.tar.gz || echo "could not downlaod the tgz via wget"
set -euo pipefail
mkdir -p $JAVA_HOME
LOCAL_TGZ=jdk.tgz
REMOTE_TGZ=https://github.com/graalvm/oracle-graalvm-ea-builds/releases/download/jdk-22.0.0-ea.07/graalvm-jdk-22.0.0-ea.07_linux-x64_bin.tar.gz
curl -o $LOCAL_TGZ $REMOTE_TGZ
ls -la $JAVA_HOME || echo "could not stat $JAVA_HOME"
ls -la $LOCAL_TGZ || echo "could not find local downloaded, $LOCAL_TGZ"
du -hs $LOCAL_TGZ
tar -zxf "$LOCAL_TGZ" -C "$JAVA_HOME"
ls -la $LOCAL_TGZ
