#!/bin/sh

tag="cvc5-1.0.8"

mkdir -p build
cd build

# get cvc5 release
if [ ! -d cvc5 ]
then
	echo "Cloning $tag"
	git clone --depth 1 --branch "$tag" https://github.com/cvc5/cvc5.git || exit 1
	cd cvc5
else
	echo "Updating to $tag"
	cd cvc5
	git fetch origin tag "$tag"
	git checkout "$tag" || exit 1
fi

# prepare build
./configure.sh production --static-binary --java-bindings --no-poly --auto-download --prefix=build/install || exit 1
cd build

# build on all cores
make -j$(nproc) || exit 1

make install || exit 1

# move back to start directory
cd ../../..

cp -L build/cvc5/build/install/share/java/cvc5.jar lib/
cp -L build/cvc5/build/install/lib/libcvc5.so lib/
cp -L build/cvc5/build/install/lib/libcvc5jni.so lib/
