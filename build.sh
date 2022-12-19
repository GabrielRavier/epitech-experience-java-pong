#!/usr/bin/env bash

javac src/*.java
jar -cmvf META-INF/MANIFEST.MF BasicPong.jar -C src .
