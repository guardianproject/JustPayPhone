
JustPayPhone
============

An InformaCam app to track time for workers on the move.


Building
--------

git clone https://github.com/guardianproject/JustPayPhone
cd JustPayPhone/
git submodules update --init --recursive
./setup-ant.sh
./build-native.sh
cd app/
ant clean debug
