#!/bin/sh

for f in `find . -name *.java`; do
  cat HEADER_LICENSE $f > /home/sub/tmp/oldfile
  mv /home/sub/tmp/oldfile $f
  echo "License Header copied to $f"
done  