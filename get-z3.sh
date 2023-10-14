#!/bin/sh

tag="z3-4.12.2"

mkdir -p build
cd build || exit 1

# get z3 release
if [ ! -d z3 ]
then
	echo "Cloning $tag"
	git clone --branch "$tag" https://github.com/Z3Prover/z3 || exit 1
	cd z3 || exit 1
else
	echo "Updating to $tag"
	cd z3 || exit 1
	git fetch origin tag "$tag"
	git checkout "$tag" || exit 1
fi

# Fix build with GCC 13
# TODO remove this line after updating to a version newer than z3-4.12.2
git cherry-pick -n c9d8e646ed505a9cd4c84b2cb31d846b8588109f

# prepare build
python scripts/mk_make.py --java || exit 1
cd build || exit 1

# build on all cores
make -j"$(nproc)" || exit 1

# move back to start directory
cd ../../..

cp -L build/z3/build/com.microsoft.z3.jar lib/
cp -L build/z3/build/libz3.so lib/
cp -L build/z3/build/libz3java.so lib/
