#!/bin/bash

# Set the toolchain directory
TOOLCHAIN_DIR="$(pwd)/toolchain"
export PATH="$TOOLCHAIN_DIR/bin:$PATH"
# Set the zlib version
ZLIB_VERSION="1.2.13"

# Set the CC environment variable
export CC="$TOOLCHAIN_DIR/bin/gcc"

# Download and extract the musl toolchain
#curl -LO https://musl.cc/x86_64-linux-musl-cross.tgz
tar xf x86_64-linux-musl-native.tgz
mv x86_64-linux-musl-native $TOOLCHAIN_DIR

# Download and extract the zlib sources
#curl -LO https://zlib.net/zlib-$ZLIB_VERSION.tar.gz
tar xf zlib-$ZLIB_VERSION.tar.gz

# Change into the zlib directory
cd zlib-$ZLIB_VERSION

# Configure, compile, and install zlib into the toolchain
./configure --prefix=$TOOLCHAIN_DIR --static
make
make install
cd ..

# Cleanup
#rm -rf x86_64-linux-musl-cross.tgz zlib-$ZLIB_VERSION.tar.gz

echo "Musl toolchain and zlib installation completed."

# Rest of the script for building static and mostly static binaries...