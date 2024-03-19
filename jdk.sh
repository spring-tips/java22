#!/usr/bin/env bash

set -euo pipefail

LOCAL_TGZ=jdk.tgz
REMOTE_TGZ=https://github.com/graalvm/oracle-graalvm-ea-builds/releases/download/jdk-22.0.0-ea.07/graalvm-jdk-22.0.0-ea.07_linux-x64_bin.tar.gz
curl $REMOTE_TGZ > $LOCAL_TGZ
tar -zxf $LOCAL_TGZ -C "$JAVA_HOME"
ls -la $LOCAL_TGZ
ls -la