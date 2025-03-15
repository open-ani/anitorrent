#!/bin/bash
#
# Copyright (C) 2024 OpenAni and Contributors
# Copyright (C) 2025 Ushio Project by Kasumi's IT
#
# This script is free software: 
# You can use, modify, distribute and redistribute this script under the term below:
# Use of this source code is governed by the GPL-3.0 license, which can be found at the following link.
#
# https://github.com/open-ani/anitorrent/blob/main/LICENSE
#

# First, we should keep the environment is always updated
echo ""
echo "Checking and updating this system..."
echo ""
sleep 1
sudo apt-get update
sudo apt-get -y full-upgrade
echo ""
echo "Update completed."
echo ""
sleep 2

# Install CMake and Clang Compiler
echo ""
echo "Installing CMake and Clang Compiler..."
echo ""
sleep 1
sudo apt-get install -y clang
sudo apt-get install -y cmake
sudo apt-get install -y ninja-build
sudo apt-get install -y llvm
echo ""
echo "Compiler install completed."
echo ""
sleep 2

# Install OpenSSL
echo ""
echo "Installing OpenSSL which Anitorrent depends on..."
echo ""
sleep 1
sudo apt-get install -y openssl
sudo apt-get install -y libssl-dev
echo ""
echo "OpenSSL install completed."
echo ""
sleep 2

# Install swig
echo ""
echo "Installing swig..."
echo ""
sleep 1
sudo apt-get install -y swig
echo ""
echo "Swig install completed."
echo ""
sleep 2

# Install OpenJDK 21
echo ""
echo "Uninstalling OpenJDK 17 if it exists..."
echo ""
sudo apt-get purge -y openjdk-17-jdk openjdk-17-jre
sudo apt -y autoremove
echo ""
echo "Installing OpenJDK 21..."
echo ""
sleep 1
sudo apt-get install -y openjdk-21-jdk
java --version
echo ""
echo "OpenJDK 21 install completed."
echo ""
sleep 2
# Finally, we're done
echo "Dependencies setup completed."