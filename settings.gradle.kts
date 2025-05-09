/*
 * Copyright (C) 2024 OpenAni and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license, which can be found at the following link.
 *
 * https://github.com/open-ani/ani/blob/main/LICENSE
 */

rootProject.name = "anitorrent"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

//include(":anitorrent-api")
//include(":anitorrent-core")
include(":anitorrent-native")
include(":anitorrent-native-desktop-jni")
include(":ci-helper")

include(":catalog")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
