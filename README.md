# Anitorrent-native

A wrapper of the [libtorrent](https://github.com/arvidn/libtorrent) library for Kotlin Multiplatform
and Android.

This project is part of the [Animeko](https://github.com/openani/animeko) project.
It's not a comprehensive wrapper, and only features that were required by Animeko are implemented.
We are happy to merge Pull Requests that adds new features that suit your needs.

Anitorrent mainly handles Java-C++ type conversion, error handling, and native library distribution
for you. Currently, this library has only low-level APIs (that almost directly calls libtorrent).
High-level APIs are located in the Animeko repository, and we will stabilize them to this repository later.

Supported targets:

- Desktop JVM: Windows x86_64, macOS x86_64, macOS AArch64, GNU/Linux x86_64.
- Android: armeabi-v7a, arm64-v8a, x86, x86_64.
- iOS is planned.

## Installation

This project has two parts, the Java JNI part and the native part. They are both published to Maven
Central.

> [!Note]
>
> Because this libtorrent is a C++ library, setting it up is a bit complicated.
> Don't worry, Anitorrent has already much simplified the process so just make sure you carefully
> follow the instructions.


In `settings.gradle.kts`, add the following to include the version catalog, then reload the project
in the IDE.

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("anitorrentLibs") {
            from("org.openani.anitorrent:catalog:0.1.0")
        }
    }
}
```

If Desktop JVM is one of your project targets, in `build.gradle.kts`, add the following helper
function. If you only target Android, you can skip this step.

```kotlin
fun getAnitorrentTriple(): String? {
    enum class Os {
        Windows, MacOS, Linux, Unknown
    }

    fun getOs(): Os {
        val os = System.getProperty("os.name").lowercase(Locale.getDefault())
        return when {
            os.contains("win") -> Os.Windows
            os.contains("mac") -> Os.MacOS
            os.contains("nux") -> Os.Linux
            else -> Os.Unknown
        }
    }

    enum class Arch {
        X86_64, AARCH64,
    }

    fun getArch(): Arch {
        val arch = System.getProperty("os.arch").lowercase(Locale.getDefault())
        return when {
            arch.contains("x86_64") || arch.contains("amd64") -> Arch.X86_64
            arch.contains("aarch64") || arch.contains("arm") -> Arch.AARCH64
            else -> throw UnsupportedOperationException("Unknown architecture: $arch")
        }
    }

    return when (getOs()) {
        Os.MacOS -> {
            when (getArch()) {
                Arch.X86_64 -> "macos-x64"
                Arch.AARCH64 -> "macos-aarch64"
            }
        }

        Os.Windows -> {
            when (getArch()) {
                Arch.X86_64 -> "windows-x64"
                else -> error("Unsupported architecture: ${getArch()}")
            }
        }

        Os.Linux -> {
            when (getArch()) {
                Arch.X86_64 -> "linux-x64"
                else -> error("Unsupported architecture: ${getArch()}")
            }
        }
        Os.Unknown -> error("Unsupported OS: ${getOs()}")
    }
}
```

Then follow the instructions below according to your project targets.

If your project is multiplatform and targets both desktop JVM and Android, you need to add the
dependencies for **each** target.

### For Android-only Projects

Add the following to `build.gradle.kts`:

```kotlin
dependencies {
    // This adds both Java and Native parts
    implementation(anitorrentLibs.anitorrent.native)
}
```

### For JVM-only Projects

Add the following to `build.gradle.kts`:

```kotlin
dependencies {
    // This adds Java part
    implementation(anitorrentLibs.anitorrent.native)

    // This adds Native part for your host OS.
    val triple = getAnitorrentTriple()
    if (triple != null) {
        implementation(
            anitorrentLibs.anitorrent.native.desktop.asProvider().map { notation ->
                "$notation:${triple}"
            },
        )
    }
}
```

### If desktop JVM is one of your KMP targets

Add the following to `build.gradle.kts`:

```kotlin
kotlin {
    // Change "desktopMain" to name of your desktop JVM source set
    sourceSets.getByName("desktopMain").dependencies {
        // This adds Java part
        implementation(anitorrentLibs.anitorrent.native)

        // This adds Native part for your host OS.
        val triple = getAnitorrentTriple()
        if (triple != null) {
            implementation(
                anitorrentLibs.anitorrent.native.desktop.asProvider().map { notation ->
                    "$notation:${triple}"
                },
            )
        }
    }
}
```

### If Android is one of your KMP targets

Add the following to `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets.androidMain.dependencies {
        // This adds both Java and Native parts
        implementation(anitorrentLibs.anitorrent.native)
    }
}
```

### If both Android and Desktop JVM are your KMP targets

If your project targets both desktop JVM and Android JVM, you might have a source set that is shared
between these two JVM-like targets. For example, it might have been configured using:

```kotlin
kotlin {
    applyDefaultHierarchyTemplate {
        common {
            group("commonJvm") {
                withJvm()
                withAndroidTarget()
            }
        }
    }
}
```

In this case, Anitorrent Java part can be (additionally) added to the shared source set as follows,
so that you can access the Anitorrent API from the shared source set.
If you don't have such a source set, you can skip this step.

```kotlin
kotlin {
    sourceSets.getByName("commonJvmMain").dependencies {
        // This adds Java part
        implementation(anitorrentLibs.anitorrent.native)
    }
}
```

## Loading the library at runtime

TODO

## License

Anitorrent is licensed under Gnu General Public License v3.0. You can find the full license text in
the `LICENSE` file.

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