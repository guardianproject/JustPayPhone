#!/bin/sh

target="android-19"

for f in `find external/ -name project.properties`; do
    android update lib-project -t $target -p `dirname $f`
done

android update project -t $target -p app/ --subprojects

cp external/InformaCam/libs/android-support-v4.jar external/InformaCam/external/OnionKit/libnetcipher/libs/android-support-v4.jar
cp external/InformaCam/libs/android-support-v4.jar external/InformaCam/external/CacheWord/cachewordlib/libs/android-support-v4.jar
cp external/InformaCam/libs/android-support-v4.jar external/ActionBarSherlock/actionbarsherlock/libs/android-support-v4.jar

cp external/InformaCam/libs/iocipher.jar external/InformaCam/external/CacheWord/cachewordlib/libs/iocipher.jar

