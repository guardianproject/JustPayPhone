#!/bin/sh

make -C external/InformaCam/external/IOCipher/external
ndk-build -C external/InformaCam/external/IOCipher
ndk-build -C external/InformaCam
