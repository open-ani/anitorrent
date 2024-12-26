# Anitorrent-native

A wrapper of the [libtorrent](https://github.com/arvidn/libtorrent) library for Kotlin.

Supported targets:

- Desktop JVM: Windows x86_64, macOS x86_64, macOS AArch64
- Android: armeabi-v7a, arm64-v8a, x86, x86_64

## Installation

From Maven Central. 

```kotlin
kotlin {
    sourceSets {
        val version = "0.1.0"
        commonMain {
            dependencies {
                implementation("org.openani.anitorrent:anitorrent-native:0.1.0")
            }
        }
        desktopMain {
            dependencies {
                implementation("org.openani.anitorrent:anitorrent-native:0.1.0:desktop")
            }
        }
    }
}

```

# License

```
    Anitorrent
    Copyright (C) 2024  The OpenAni Team and contributors

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
```