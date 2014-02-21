#!/bin/sh

target="android-18"

for f in `find external/ -name project.properties`; do
    android update lib-project -t $target -p `dirname $f`
done

android update project -t $target -p app/ --subprojects

cp external/InformaCam/libs/android-support-v4.jar external/InformaCam/external/OnionKit/libonionkit/libs/android-support-v4.jar
cp external/InformaCam/libs/android-support-v4.jar external/InformaCam/external/CacheWord/cachewordlib/libs/android-support-v4.jar
cp external/InformaCam/libs/android-support-v4.jar external/ActionBarSherlock/actionbarsherlock/libs/android-support-v4.jar

cp external/InformaCam/libs/iocipher.jar
cp external/InformaCam/external/CacheWord/cachewordlib/libs/iocipher.jar

