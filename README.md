
JustPayPhone
============

An InformaCam app to track time for workers on the move.


Building
--------

git clone https://github.com/guardianproject/JustPayPhone
cd JustPayPhone/
git submodules update --init --recursive
for f in `find external/ -name project.properties`; do \
    android update lib-project -p `dirname $f`; done
make -C external/InformaCam/external/IOCipher/external
ndk-build -C external/InformaCam/external/IOCipher
ndk-build -C external/InformaCam

cd app/
android update project -p . --recursive
ant debug


# TODO add this to ./setup-ant.sh
