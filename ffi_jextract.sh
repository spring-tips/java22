#!/usr/bin/env bash
LINUX=https://download.java.net/java/early_access/jextract/22/3/openjdk-22-jextract+3-13_linux-x64_bin.tar.gz
MACOS=https://download.java.net/java/early_access/jextract/22/3/openjdk-22-jextract+3-13_macos-x64_bin.tar.gz

OS=$(uname)

DL=""
STDIO=""

if [ "$OS" = "Darwin" ]; then
    DL="$MACOS"
    STDIO=/Library/Developer/CommandLineTools/SDKs/MacOSX.sdk/usr/include/stdio.h
elif [ "$OS" = "Linux" ]; then
    DL=$LINUX
    STDIO=/usr/include/stdio.h
else
    echo "Are you running on Windows? This might work inside the Windows Subsystem for Linux"
fi

LOCAL_TGZ=tmp/jextract.tgz
REMOTE_TGZ=$DL
JEXTRACT_HOME=jextract-22


mkdir -p "$( dirname  $LOCAL_TGZ )"
wget -O $LOCAL_TGZ $REMOTE_TGZ
tar -zxf "$LOCAL_TGZ" -C .
export PATH=$PATH:$JEXTRACT_HOME/bin

jextract  --output src/main/java  -t com.example.stdio $STDIO




