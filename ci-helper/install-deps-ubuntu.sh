#!/bin/bash
#
# Copyright (C) 2024 OpenAni and Contributors
# Copyright (C) 2025 RinLin_NYA/AsahinaHotaru
#
# This script is free software: 
# You can use, modify, distribute and redistribute this script under the term below:
# Use of this source code is governed by the Apache-2.0 license, which can be found at the following link.
#
# https://github.com/open-ani/mediamp/blob/main/LICENSE
#

# Install CMake and Clang Compiler
sudo apt install -y clang
sudo apt install -y cmake
sudo apt install -y ninja-build
sudo apt install -y llvm

# Install OpenSSL
sudo apt install -y openssl
sudo apt install -y libssl-dev
sudo apt install -y swig