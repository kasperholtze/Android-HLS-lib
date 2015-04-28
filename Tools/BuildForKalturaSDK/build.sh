#!/bin/bash

resolveDir() {
  cd "$1"; pwd;
}

if [ -z "$1" ]; then
  echo Please specify path to Kaltura Player SDK > /dev/stderr
  exit 1
fi

KALTURA_PLAYER_SDK=$(resolveDir "$1")
HLS_PLAYER_SDK=$(resolveDir $(dirname $0)/../../HLSPlayerSDK)

cd $(dirname $0)

ndk-build -C $HLS_PLAYER_SDK

rm -rf temp
mkdir temp
cd temp
mkdir lib
cp -R $HLS_PLAYER_SDK/libs/armeabi $HLS_PLAYER_SDK/libs/armeabi-v7a $HLS_PLAYER_SDK/libs/x86 $HLS_PLAYER_SDK/libs/mips lib
jar cfvM lib.jar lib

cp lib.jar $KALTURA_PLAYER_SDK/hLSPlayerSDK/libs
rm -rf "$KALTURA_PLAYER_SDK"/hLSPlayerSDK/src/main/java/com
cp -R "$HLS_PLAYER_SDK"/src/com "$KALTURA_PLAYER_SDK"/hLSPlayerSDK/src/main/java


