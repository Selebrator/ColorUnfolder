#!/bin/sh

tag="cvc5-1.1.2"

build() {
  mkdir -p build
  cd build || exit 1

  # get cvc5 release
  if [ ! -d cvc5 ]
  then
  	echo "Cloning $tag"
  	git clone --depth 1 --branch "$tag" https://github.com/cvc5/cvc5.git || exit 1
  	cd cvc5 || exit 1
  else
  	echo "Updating to $tag"
  	cd cvc5 || exit 1
  	git fetch origin tag "$tag"
  	git checkout "$tag" || exit 1
  fi

  # prepare build
  ./configure.sh production --static-binary --java-bindings --no-poly --auto-download --prefix=build/install || exit 1
  cd build || exit 1

  # build on all cores
  make -j"$(nproc)" || exit 1

  make install || exit 1

  # move back to start directory
  cd ../../..

  cp -L build/cvc5/build/install/share/java/cvc5.jar lib/
  cp -L build/cvc5/build/install/lib/libcvc5.so.1 lib/
  cp -L build/cvc5/build/install/lib/libcvc5jni.so lib/
}

download() {
  cd lib || exit 1
  curl -LO "https://github.com/cvc5/cvc5/releases/download/$tag/cvc5-Linux-shared.zip"
  unzip -oj "cvc5-Linux-shared.zip" \
  	"cvc5-Linux-shared/lib/libcvc5jni.so" \
  	"cvc5-Linux-shared/lib/libcvc5.so.1" \
  	"cvc5-Linux-shared/lib/libcvc5parser.so.1" \
  	"cvc5-Linux-shared/lib/libpoly.so.0" \
  	"cvc5-Linux-shared/lib/libpolyxx.so.0" \
  	"cvc5-Linux-shared/share/java/cvc5.jar"
  rm "cvc5-Linux-shared.zip"
}

download
