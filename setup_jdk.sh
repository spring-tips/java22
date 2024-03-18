#!/usr/bin/env bash
echo "hello, world!"

TGZ=https://github.com/graalvm/oracle-graalvm-ea-builds/releases/download/jdk-22.0.0-ea.07/graalvm-jdk-22.0.0-ea.07_linux-x64_bin.tar.gz
curl $TGZ

cd $HOME

pwd

curl $TGZ > jdk.tgz

tar -zxpf $TGZ

ls -la



